package com.trading.backend.task;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.trading.backend.annotation.Traceable;
import com.trading.backend.bo.WorthGrantBO;
import com.trading.backend.client.IAssetServiceApi;
import com.trading.backend.client.IEarnServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.enums.PossessBusinesStageEnum;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.enums.PossessStageEnum;
import com.trading.backend.common.util.Functions;
import com.trading.backend.common.util.Maps;
import com.trading.backend.common.util.Predicator;
import com.trading.backend.config.ExecutorConfigurer;
import com.trading.backend.constant.RedisKey;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.domain.TempCouponRedeem;
import com.trading.backend.http.request.earn.opponent.CashEarnAcquireParam;
import com.trading.backend.http.request.earn.opponent.PositionAcquireParam;
import com.trading.backend.http.response.earn.PositionVO;
import com.trading.backend.kafka.message.DepositInfoModel;
import com.trading.backend.kafka.message.KycStageModel;
import com.trading.backend.mapper.CouponMapper;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.mapper.TempCouponRedeemMapper;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.service.ICashCouponService;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.INoviceService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.util.NumberCriteria;
import com.trading.backend.util.PageContext;
import com.trading.backend.util.Converter;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ~~ trading.s
 * @date 17:43 11/23/21
 */
@Component @Slf4j
public class DisposableTask {

    @Autowired
    private TempCouponRedeemMapper couponRedeemMapper;
    @Value("${coupon-redeem.type1}")
    private String type1;
    @Value("${coupon-redeem.type2}")
    private String type2;
    @Value("${coupon-redeem.type3}")
    private String type3;
    @Value("${coupon-redeem.type4}")
    private String type4;
    @Value("${coupon-redeem.type5}")
    private String type5;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private ExecutorConfigurer executor;
    @Autowired
    private CouponPossessMapper possessMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private ICashCouponService iCashCoupService;
    @Autowired
    private INoviceService noviceService;
    @Autowired
    private IPossesService possesService;
    @Autowired
    private ICashCouponService iCashCouponService;
    @Autowired
    private IAssetServiceApi assetServiceApi;
    @Autowired
    private IEarnServiceApi earnServiceApi;


    @XxlJob("rewardEarnReedemCoupon") @Traceable
    public void rewardEarnReedemCoupon(String holder) {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("rewardEarnReedemCoupon jobParam={}", jobParam);
        if (StringUtils.isBlank(jobParam)) return;
        List<PossessDO> possList = possesService.getAllAppyScenePossess(jobParam, CouponApplySceneEnum.EARN_CASH);
        List<PossessDO> filter = Functions.filter(possList, Predicator.nonNull(PossessDO::getBusinessId));
        filter.forEach(posDo -> {
            CashCouponRule cashActivRule = (CashCouponRule) Converter.deserialize(posDo.getCouponRule(), CouponTypeEnum.CASHRETURN_TYPE.getCode());
            CashEarnAcquireParam cashEarnAcquireParam =
                    CashEarnAcquireParam.fromCashCouponRule(posDo.getUid(), cashActivRule);
            PositionAcquireParam request = new PositionAcquireParam();
            request.setUser_ids(Collections.singletonList(cashEarnAcquireParam.getUser_id()));
            request.setCoins(Functions.toList(cashEarnAcquireParam.getCoin_rule(), CashEarnAcquireParam.EarnCoinRule::getHolding_coin, String::toUpperCase));
            request.setStatus(Lists.newArrayList("FINISH", "HOLDING"));
            // rpc获取仓位信息
            List<PositionVO> positions = earnServiceApi.getPositions(request);
            Optional<PositionVO> any = positions.stream().filter(Predicator.isEqual(PositionVO::getPosition_id, posDo.getBusinessId())).findAny();
            if (any.isPresent()) {
                WorthGrantBO grantBo = new WorthGrantBO(
                        posDo.getUid(), posDo.getWorthCoin(), posDo.getWorth().toPlainString(),
                        CouponApplySceneEnum.getByCode(posDo.getApplyScene()), posDo.getPossessId(), posDo.getCouponId());

                boolean success = assetServiceApi.activityDeposit(String.valueOf(grantBo.getPossessId()), grantBo.getUid(), grantBo.getGrantSize(), grantBo.getGrantCoin());
                if (!success) {
                    log.error("GrantTaskId {} has executed, duplicated deposit is forbidden", posDo.getPossessId());
                }
                iCashCouponService.accomplish1(grantBo);
            }
        });
    }



    @XxlJob("referralSend") @Traceable
    public void sendReferralMsg() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("referralSend jobParam={}", jobParam);
        if (StringUtils.isBlank(jobParam)) return;
        noviceService.updateNoviceProgress(jobParam);
    }

    @XxlJob("possSourceFix") @Traceable
    public void possSourceFix() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("referralSend jobParam={}", jobParam);
        Example example = new Example(CouponPossess.class);
        example.createCriteria().andIn("couponId", Lists.newArrayList(66L, 62L, 67L, 135L, 136L, 137L))
               .andEqualTo("source", 1);
        int rows = possessMapper.updateByExampleSelective(new CouponPossess().setSource(0), example);
        log.info("Update Source to 1, rows = {}", rows);

        Example example1 = new Example(CouponPossess.class);
        example1.createCriteria().andIn("couponId",
                        Lists.newArrayList(138L, 95L, 139L, 142L, 103L, 96L, 141L, 134L, 100L, 144L, 97L, 93L, 98L, 140L, 94L, 105L, 92L, 101L, 102L, 143L))
                .andEqualTo("source", 1);
        int rows1 = possessMapper.updateByExampleSelective(new CouponPossess().setSource(2), example1);
        log.info("Update Source to 2, rows = {}", rows1);
    }

    @XxlJob("activate") @Traceable
    public void activate(String param) {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("activate jobParam={}", jobParam);
        if (StringUtils.isBlank(jobParam)) return;
        String uid = jobParam.split(",")[0];
        int applyScene = Integer.parseInt(jobParam.split(",")[1]);
        // long possessId = Long.parseLong(jobParam);
        if (applyScene == 51)
            iCashCoupService.activOnKycSubmit(KycStageModel.kycPassMock(uid), iCashCoupService.postback());
        else if (applyScene == 52)
            iCashCoupService.activOnDepositSettle(
                    DepositInfoModel.depositMock(uid), iCashCoupService.postback());
    }


    @XxlJob("modifyCoupnRule")
    public void modifyCoupnRule() {
        List<Long> ids = Lists.newArrayList(95L, 96L, 97L, 98L, 100L, 101L, 138L, 139L);
        Example example = new Example(Coupon.class);
        example.selectProperties("id", "rule");
        example.createCriteria().andEqualTo("type", 0).andIn("id", ids);
        List<Coupon> coupons = couponMapper.selectByExample(example);

        coupons.forEach(coupon -> {
            JSONArray coin_rules = coupon.getRule().getJSONArray("coin_rules");
            coin_rules.forEach(coin_rule -> {
                JSONObject coinRule = (JSONObject) coin_rule;
                String applyCoin = coinRule.getString("apply_coin");
                if (StringUtils.equalsIgnoreCase(applyCoin, "btc")) {
                    coinRule.put("max_amount", NumberCriteria.defaultScale("100"));
                    coinRule.put("min_amount", NumberCriteria.defaultScale("0.001"));
                }
                if (StringUtils.equalsIgnoreCase(applyCoin, "eth")) {
                    coinRule.put("max_amount", NumberCriteria.defaultScale("1300"));
                    coinRule.put("min_amount", NumberCriteria.defaultScale("0.01"));
                }
                if (StringUtils.equalsIgnoreCase(applyCoin, "usd")) {
                    coinRule.put("max_amount", NumberCriteria.defaultScale("5000000"));
                    coinRule.put("min_amount", NumberCriteria.defaultScale("100"));
                }
                if (StringUtils.equalsIgnoreCase(applyCoin, "usdt")) {
                    coinRule.put("max_amount", NumberCriteria.defaultScale("5000000"));
                    coinRule.put("min_amount", NumberCriteria.defaultScale("100"));
                }
            });
            couponMapper.updateByPrimaryKeySelective(coupon);
        });
    }



    @XxlJob("modifyCouponDays")
    public void modifyCouponDays() {
        log.info("modifyCouponDays start");

        Example example = new Example(Coupon.class);
        example.selectProperties("id", "descr", "rule");
        example.createCriteria().andEqualTo("type", 0);

        List<Coupon> coupons = couponMapper.selectByExample(example);
        coupons.forEach(coupon -> {
            boolean update = false;
            if (coupon.getDescr().contains("360")) {
                String s = coupon.getDescr().replaceAll("360", "365");
                coupon.setDescr(s);
                update = true;
            }
            JSONObject rule = coupon.getRule();
            if (Objects.equals(rule.getInteger("max_subscr_days"), 360)) {
                rule.put("max_subscr_days", 365);
                coupon.setRule(rule);
                update = true;
            }
            if (update)
                couponMapper.updateByPrimaryKeySelective(coupon);
        });
    }















    @XxlJob("removeInvalid2Grants")
    public void removeInvalid2Grants() {
        log.info("removeInvalid2Grants start");

        double max = (double) Instant.now().toEpochMilli() + Duration.ofDays(100).toMillis();
        long total = redisService.countZSet(RedisKey.TO_GRANT_CASH_POOL, 0, max);

        BiFunction<Integer, Integer, List<String>> biFunction = (page, pageSize) -> {
            Set<Object> pageInfos = redisService.getSortedSetPage(RedisKey.TO_GRANT_CASH_POOL, 0, max, page - 1, pageSize);
            return Functions.toList(Lists.newArrayList(pageInfos), String::valueOf);
        };

        Function<List<String>, Integer> consumer = possIdUids -> {
            List<Long> possessIds = possIdUids.stream().map(possIdUid -> Long.parseLong(StringUtils.split(possIdUid, ":")[0])).collect(Collectors.toList());

            Example example = new Example(CouponPossess.class);
            example.selectProperties("id", "uid", "possessStage", "businessStage");
            example.createCriteria().andIn("id", possessIds);
            List<CouponPossess> possessList = possessMapper.selectByExample(example);
            Set<String> rightfulSetValues = possessList.stream().filter(poss -> poss.getPossessStage() == 0 && poss.getBusinessStage() == 2)
                                                    .map(poss -> StringUtils.join(poss.getId(), ":", poss.getUid())).collect(Collectors.toSet());
            List<String> illigalValues = Functions.filter(possIdUids, Predicator.notExist(rightfulSetValues));
            if (CollectionUtil.isNotEmpty(illigalValues)) {
                return (int) redisService.removeZSetByValue(RedisKey.TO_GRANT_CASH_POOL, illigalValues.toArray());
            }
            return 0;
        };

        executor.serial(total, biFunction, Function.identity(), consumer);
        log.info("removeInvalid2Grants complete!");
    }













    private static final Map<Integer, List<Long>> APPLY_SCENED_COUPONS = Maps.of(
            1, Lists.newArrayList(61L, 66L),
            2, Lists.newArrayList(62L),
            3, Lists.newArrayList(63L, 67L)
    );
    private static final Set<String> APPLY_SCENE1 = new HashSet<>(4096);
    private static final Set<String> APPLY_SCENE2 = new HashSet<>(4096);
    private static final Set<String> APPLY_SCENE3 = new HashSet<>(4096);
    private static final Set<String> APPLY_SCENE123 = new HashSet<>(4096);


    public void couponRedeem() {
        Example possExample = new Example(CouponPossess.class);
        possExample.createCriteria().andEqualTo("source", PossessSourceEnum.OLDUSER_REDEEM.getCode());
        int oldPossCount = possessMapper.selectCountByExample(possExample);
        if (oldPossCount > 0) {
            log.error("Duplicated CouponRedeem task denied !");
            return;
        }
        log.info("couponRedeem execute");

        redeem(1);
        redeem(2);
        redeem(3);
        redeem(4);
        redeem(5);
    }

    private void redeem(int redeemType) {
        Example example = new Example(TempCouponRedeem.class);
        example.createCriteria().andEqualTo("redeemType", redeemType);
        long total = couponRedeemMapper.selectCountByExample(example);
        BiFunction<Integer, Integer, List<String>> biProducer = (page, pageSize) -> {
            List<TempCouponRedeem> redeems = PageContext.selectList(() -> couponRedeemMapper.selectByExample(example), page, pageSize, "id asc");
            List<String> collect = redeems.stream().map(TempCouponRedeem::getUid).distinct().collect(Collectors.toList());
            if (redeemType == 1 || redeemType == 5) {
                return collect.stream().filter(uid -> !APPLY_SCENE3.contains(uid)).collect(Collectors.toList());
            }
            else if (redeemType == 2) {
                return collect.stream().filter(uid -> !APPLY_SCENE1.contains(uid)).collect(Collectors.toList());
            }
            else if (redeemType == 3) {
                return collect.stream().filter(uid -> !APPLY_SCENE2.contains(uid)).collect(Collectors.toList());
            }
            else {
                return collect.stream().filter(uid -> !APPLY_SCENE3.contains(uid)).collect(Collectors.toList());
            }
        };
        Function<List<String>, Integer> consumer =
                uids -> couponService.receiveMultiUser(uids, redeemCouponMap().get(redeemType), null, PossessSourceEnum.OLDUSER_REDEEM, false).size();
        executor.serial(total, redeemCouponMap().get(redeemType).size(), biProducer, Function.identity(), consumer);
    }

    private Map<Integer, List<Long>> redeemCouponMap() {
        return Maps.of(
                1, Arrays.stream(StringUtils.split(type1, ",")).map(Long::parseLong).collect(Collectors.toList()),
                2, Arrays.stream(StringUtils.split(type2, ",")).map(Long::parseLong).collect(Collectors.toList()),
                3, Arrays.stream(StringUtils.split(type3, ",")).map(Long::parseLong).collect(Collectors.toList()),
                4, Arrays.stream(StringUtils.split(type4, ",")).map(Long::parseLong).collect(Collectors.toList()),
                5, Arrays.stream(StringUtils.split(type5, ",")).map(Long::parseLong).collect(Collectors.toList())
        );
    }

    @XxlJob("couponDiscard")
    public void couponDiscard() {
        String jobParam = XxlJobHelper.getJobParam();
        if (StringUtils.equals(jobParam, "discard")) {
            log.info("couponDiscard execute");
            discard(1);
            discard(2);
            discard(3);
            Collection<String> scene123 = CollectionUtil.intersection(APPLY_SCENE1, APPLY_SCENE2, APPLY_SCENE3);
            APPLY_SCENE123.addAll(scene123);
        }
        if (StringUtils.equals(jobParam, "redeem")) {
            couponRedeem();
        }
    }

    public void discard(int applyScene) {
        List<Long> toExprs = APPLY_SCENED_COUPONS.get(applyScene);
        List<Long> toGrants = applyScene == 1 ? Lists.newArrayList(135L) : (applyScene == 2 ? Lists.newArrayList(136L) : Lists.newArrayList(137L));

        Example possExample = new Example(CouponPossess.class);
        possExample.selectProperties("uid");
        possExample.createCriteria()
                   .andIn("couponId", toExprs)
                   .andIn("businessStage", Lists.newArrayList(0));
        int total = possessMapper.selectCountByExample(possExample);

        BiFunction<Integer, Integer, List<String>> biProducer = (page, pageSize) -> {
            List<CouponPossess> possList = PageContext.selectList(() -> possessMapper.selectByExample(possExample), page, pageSize, "id asc");
            List<String> collect = possList.stream().map(CouponPossess::getUid).distinct().collect(Collectors.toList());
            if (applyScene == 1)
                APPLY_SCENE1.addAll(collect);
            else if (applyScene == 2)
                APPLY_SCENE2.addAll(collect);
            else
                APPLY_SCENE3.addAll(collect);
            return collect;
        };
        Function<List<String>, Integer> consumer =
                uids -> couponService.receiveMultiUser(uids, toGrants, null, PossessSourceEnum.SERVICE_UPGRADE, false).size();
        executor.serial(total, toGrants.size(), biProducer, Function.identity(), consumer);

        CouponPossess toUpdate = new CouponPossess()
                .setBusinessStage(PossessBusinesStageEnum.EXPIRED.getCode())
                .setPossessStage(PossessStageEnum.DISABLE.getCode());
        int update = possessMapper.updateByExampleSelective(toUpdate, possExample);
        log.info("Update expire size = {}", update);
        System.out.println();
    }
}
