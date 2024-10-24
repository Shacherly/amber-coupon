package com.trading.backend.service;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trading.backend.losermapper.PfCouponActivityInfoMapper;
import com.trading.backend.losermapper.PfCouponDeliveryDetailMapper;
import com.trading.backend.losermapper.PfCouponRewardDetailMapper;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.config.ExecutorConfigurer;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.CommonCoinRule;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponEvent;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.domain.PfAssetCouponRule;
import com.trading.backend.domain.PfCoupon;
import com.trading.backend.domain.PfCouponActivityInfo;
import com.trading.backend.domain.PfCouponDeliveryDetail;
import com.trading.backend.domain.PfCouponRewardDetail;
import com.trading.backend.domain.PfDeductCouponRule;
import com.trading.backend.domain.PfInterestCouponRule;
import com.trading.backend.domain.RewardDetailWithRuleOrder;
import com.trading.backend.mapper.CouponEventMapper;
import com.trading.backend.mapper.CouponMapper;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.util.Builder;
import com.trading.backend.util.PageContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;


/**
 * 数据迁移
 */
@Slf4j
@Service
public class DataTransTask {

    @Autowired
    private OriginSourceService originSourceService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private ICouponAdminService couponAdminService;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired(required = false)
    private PfCouponDeliveryDetailMapper deliveryMapper;
    @Autowired(required = false)
    private PfCouponRewardDetailMapper rewardMapper;
    @Autowired
    private CouponPossessMapper possessMapper;
    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private ExecutorConfigurer executorConfig;
    @Autowired(required = false)
    private PfCouponActivityInfoMapper activityMapper;
    @Autowired
    private CouponEventMapper eventMapper;

    private Coupon fromOriginCoupon(PfCoupon origin) {

        Long total = origin.getTotal();
        Long limit = origin.getLimitPerPeople();

        Builder<Coupon> builder = Builder.of(Coupon::new);
        builder.with(Coupon::setId, origin.getId())
               .with(Coupon::setName, origin.getName())
               .with(Coupon::setTitle, origin.getTitle().replace("_", "-"))
               .with(Coupon::setStatus, Short.valueOf(String.valueOf(origin.getStatus())))
               .with(Coupon::setType, origin.getType() % 100)
               .with(Coupon::setDescr, origin.getDescription().replace("_", "-"))
               .with(Coupon::setOverlay, origin.getOverLay())
               .with(Coupon::setTotal, total == 0 ? 99999999999L : total)
               .with(Coupon::setPossessLimit, limit == 0 ? 9999 : limit.intValue())
               .with(Coupon::setExprInDays, origin.getExpireDays())
               .with(Coupon::setExprAtStart,
                       Optional.ofNullable(origin.getExpireAtStart())
                               .map(Instant::ofEpochMilli).map(DateUtil::toLocalDateTime).orElse(null))
               .with(Coupon::setExprAtEnd,
                       Optional.ofNullable(origin.getExpireAtEnd())
                               .map(Instant::ofEpochMilli).map(DateUtil::toLocalDateTime).orElse(null))
               .with(Coupon::setRedirectUrl, origin.getRedirectUrl())
               .with(Coupon::setCtime, origin.getCtime())
               .with(Coupon::setUtime, origin.getUtime())
               .with(Coupon::setRemark, origin.getRemark())
               // .with(Coupon::setRule, withJSONRule(param))
               // .with(Coupon::setWorthCoin, param.getWorthCoin())
               // .with(Coupon::setWorth, NumberCriteria.defaultScale(param.getWorth()))
               .with(Coupon::setApplyScene, origin.getType() * 10 + 1);// 暂时可以定为ApplyScene

        return builder.build();
    }

    @XxlJob("transferCoupon")
    public void transferCoupon() {
        List<PfCoupon> pfCoupons = originSourceService.getOriginCoupons();
        log.info("transferCoupon size = {}", pfCoupons.size());
        List<Coupon> collect = pfCoupons.stream().map(this::fromOriginCoupon).collect(Collectors.toList());
        // couponAdminService.absoluteSave();
        log.info("transferCoupon des = {}", collect);
        try {
            couponMapper.batchInsert(collect);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        transferCashRule();
        transferDeduRule();
        transferInteRule();
    }

    // @XxlJob("transferCashRule")
    public void transferCashRule() {
        List<PfAssetCouponRule> assetRules = originSourceService.getOriginAssetRules();
        List<Long> couponIds = assetRules.stream().map(PfAssetCouponRule::getCouponId).distinct().collect(Collectors.toList());

        Map<Long, List<PfAssetCouponRule>> collect = assetRules.stream().collect(Collectors.groupingBy(PfAssetCouponRule::getCouponId));
        List<Coupon> toUpdate = new ArrayList<>();
        collect.forEach((couponId, rules) -> {
            PfAssetCouponRule general = rules.get(0);
            CashCouponRule newRule = new CashCouponRule();
            Coupon newCoupon = new Coupon()
                    .setId(general.getCouponId()).setWorth(general.getAmount())
                    .setWorthCoin(StringUtils.upperCase(general.getAssetCoin())).setApplyScene(general.getActivateCondition() + 50);
            if (general.getActivateCondition() == 2) {
                List<CommonCoinRule> collect1 = rules.stream().map(r -> {
                    CommonCoinRule commonCoinRule = new CommonCoinRule()
                            .setApplyCoin(StringUtils.upperCase(r.getCoin()))
                            .setMinAmount(r.getMinRequiredAmount());
                    return commonCoinRule;
                }).collect(Collectors.toList());
                newRule.setCoinRules(collect1);
                newCoupon.setRule((JSONObject) JSONObject.toJSON(newRule)).setApplyScene(52);
            }
            else if (general.getActivateCondition() == 3) {
                List<CommonCoinRule> collect1 = rules.stream().map(r -> {
                    CommonCoinRule commonCoinRule = new CommonCoinRule()
                            .setApplyCoin(StringUtils.upperCase(r.getCoin()))
                            .setMinAmount(r.getMinRequiredAmount());
                    return commonCoinRule;
                }).collect(Collectors.toList());
                newRule.setMinSubscrDays(general.getMinSubscribeDays());
                newRule.setCoinRules(collect1);
                newCoupon.setRule((JSONObject) JSONObject.toJSON(newRule)).setApplyScene(53);
            }
            toUpdate.add(newCoupon);
        });

        System.out.println(couponMapper.batchUpdate(toUpdate));
    }

    // @XxlJob("transferDeduRule")
    public void transferDeduRule() {
        List<PfDeductCouponRule> deductRules = originSourceService.getOriginDeductRules();
        Map<Long, List<PfDeductCouponRule>> collect = deductRules.stream().collect(Collectors.groupingBy(PfDeductCouponRule::getCouponId));
        List<Coupon> toUpdate = new ArrayList<>();
        collect.forEach((couponId, rules) -> {
            PfDeductCouponRule general = rules.get(0);
            DeductCouponRule newRule = new DeductCouponRule();
            Coupon newCoupon = new Coupon()
                    .setId(general.getCouponId()).setWorth(general.getDeductRate()).setApplyScene(LOAN_DEDUCT.getCode());
            List<CommonCoinRule> collect1 = rules.stream()
                                                 .map(r -> {
                                                     return new CommonCoinRule()
                                                             .setApplyCoin(StringUtils.upperCase(r.getApplyCoin())).setMinAmount(r.getMinLoanAmount())
                                                             .setMaxAmount(r.getMaxLoanAmount());
                                                 }).collect(Collectors.toList());
            newRule.setCoinRules(collect1);
            newRule.setDeductDays(general.getDeductDays()).setDeductWay(general.getDeductWay()).setLoanType(general.getLoanType())
                   .setMinLoanDays(general.getMinLoanDays()).setMaxLoanDays(general.getMaxLoanDays());
            newCoupon.setRule((JSONObject) JSONObject.toJSON(newRule));
            toUpdate.add(newCoupon);
        });
        couponMapper.batchUpdate(toUpdate);
    }

    // @XxlJob("transferInteRule")
    public void transferInteRule() {
        List<PfInterestCouponRule> interRules = originSourceService.getOriginInteresRules();
        Map<Long, List<PfInterestCouponRule>> collect = interRules.stream().collect(Collectors.groupingBy(PfInterestCouponRule::getCouponId));
        List<Coupon> toUpdate = new ArrayList<>();
        collect.forEach((couponId, rules) -> {
            PfInterestCouponRule general = rules.get(0);
            InterCouponRule newRule = new InterCouponRule();
            Coupon newCoupon = new Coupon()
                    .setId(general.getCouponId()).setWorth(general.getInterestRate()).setApplyScene(EARN_INTEREST.getCode());
            List<CommonCoinRule> collect1 = rules.stream().map(r -> {
                return new CommonCoinRule().setApplyCoin(StringUtils.upperCase(r.getCoin())).setMaxAmount(r.getMaxSubscribeAmount()).setMinAmount(r.getMinSubscribeAmount());
            }).collect(Collectors.toList());
            newRule.setCoinRules(collect1);
            newRule.setInterDays(general.getInterestDays()).setMinSubscrDays(general.getMinSubscribeDays()).setMaxSubscrDays(general.getMaxSubscribeDays());
            newCoupon.setRule((JSONObject) JSONObject.toJSON(newRule));
            toUpdate.add(newCoupon);
        });
        couponMapper.batchUpdate(toUpdate);
    }

    @XxlJob("transferDelivery")
    public void transferDelivery(Integer couponType) {
        String param = XxlJobHelper.getJobParam();
        if (StringUtils.isBlank(param)) return;
        couponType = Integer.parseInt(param);
        log.info("transferDelivery, param = {}", couponType);
        Example example = new Example(PfCouponDeliveryDetail.class);
        example.createCriteria().andEqualTo("couponType", couponType);
        int rows = deliveryMapper.selectCountByExample(example);
        // Instant s1 = Instant.now();
        // List<PfCouponDeliveryDetail> delivs = deliveryMapper.selectByExample(example);
        // Instant s2 = Instant.now();
        // log.debug("Query spend = {} ms", Duration.between(s1, s2).toMillis());// 6569 ms
        // List<CouponPossess> possesses = delivs.stream().map(this::possessAdapter).collect(Collectors.toList());
        // Instant s3 = Instant.now();
        // log.debug("Query spend = {} ms", Duration.between(s2, s3).toMillis());// 73 ms

        // Function<List<CouponPossess>, ?> function = poss -> possessDao.batchInsert(poss);
        // executorConfig.submit(possesses, poss -> possessDao.batchInsert(poss));
        BiFunction<Integer, Integer, List<PfCouponDeliveryDetail>> biConsumer =
                (page, pageSize) -> PageContext.selectList(() -> deliveryMapper.selectByExample(example), page, pageSize, "id asc");
        executorConfig.serial(rows, biConsumer, this::possessAdapter, poss -> possessDao.batchInsert(poss));
        // possessDao.batchInsert(possesses);
        System.out.println();
    }

    @XxlJob("transferDelivReward")
    public void transferDelivReward(Integer couponType) {
        // String param = XxlJobHelper.getJobParam();
        // if (StringUtils.isBlank(param)) return;
        // couponType = Integer.parseInt(param);
        log.info("transferDelivReward, param = {}", couponType);
        Example example = new Example(PfCouponRewardDetail.class);
        example.createCriteria().andEqualTo("couponType", couponType);
        int rows = rewardMapper.selectCountByExample(example);
        // List<CouponPossess> possesses = possessMapper.selectByExample(example);
        // List<PfCouponDeliveryDetail> delivs = deliveryMapper.selectByExample(example);
        // List<Long> possessIds = possesses.stream().map(CouponPossess::getId).collect(Collectors.toList());
        // Example example1 = new Example(PfCouponRewardDetail.class);
        // example1.createCriteria().andIn("relateCouponDeliveryId", possessIds);
        // List<PfCouponRewardDetail> rewards = rewardMapper.selectByExample(example1);

        // List<RewardDetailWithRuleOrder> list = rewardMapper.selectJoinReward(couponType);
        // List<CouponPossess> collect = list.stream().map(this::rewardAdapter).collect(Collectors.toList());
        //
        // executorConfig.submit(collect, poss -> possessDao.batchUpdate(poss));
        BiFunction<Integer, Integer, List<PfCouponRewardDetail>> biConsumer =
                (page, pageSize) -> PageContext.selectList(() -> rewardMapper.selectByExample(example), page, pageSize, "id asc");
        executorConfig.serial(rows, biConsumer, this::rewardAdapter, poss -> possessDao.batchUpdate(poss));
    }


    @XxlJob("transferActivityData")
    public void transferActivityData() {
        log.info("transferActivityData");
        Example example = new Example(PfCouponActivityInfo.class);
        int rows = activityMapper.selectCountByExample(example);
        BiFunction<Integer, Integer, List<PfCouponActivityInfo>> biConsumer =
                (page, pageSize) -> PageContext.selectList(() -> activityMapper.selectByExample(example), page, pageSize, "id asc");
        executorConfig.serial(rows, biConsumer, this::activity2Event, events -> eventMapper.batchInsert(events));
    }

    private CouponEvent activity2Event(PfCouponActivityInfo activity) {
        Builder<CouponEvent> builder = Builder.of(CouponEvent::new);
        builder.with(CouponEvent::setId, activity.getId())
               .with(CouponEvent::setCouponIds, StringUtils.isNotBlank(activity.getCouponId()) ? activity.getCouponId() : "-1")
               .with(CouponEvent::setType, activityType(activity.getType()))
               .with(CouponEvent::setName, activity.getName())
               .with(CouponEvent::setDescr, activity.getDescription())
               .with(CouponEvent::setEventStage, activityStage(activity.getStatus()))
               .with(CouponEvent::setApprovalEvent, activity.getType() == 2)
               .with(CouponEvent::setApprovalStage, activity.getType() == 2 ? approvalStage(activity.getStatus()) : 0)
               .with(CouponEvent::setStartTime, activity.getCtime())
               .with(CouponEvent::setEndTime, activity.getCtime())
               .with(CouponEvent::setCtime, activity.getCtime())
               .with(CouponEvent::setUtime, activity.getUtime())
               .with(CouponEvent::setRemark, activity.getRemark())
               .with(CouponEvent::setObjectType, objectType(activity.getReceiverType()));
        return builder.build();
    }

    private int activityType(int type) {
        if (type == 0) return 1;
        if (type == 1) return 2;
        return 0;
    }

    private int activityStage(int status) {
        if (status == 0) return 0;
        if (status == 1) return 1;
        if (status == 2) return 3;
        if (status == 3) return 2;
        if (status == 5) return 4;
        return 3;
    }

    private int approvalStage(int status) {
        if (status == -2) return 0;
        if (status == -1) return 1;
        if (status == 1) return 3;
        if (status == 6) return 2;
        return 0;
    }

    private String objectType(int objectType) {
        if (objectType == 0) return "ALL";
        if (objectType == 1) return "SELECTED_USER";
        if (objectType == 6) return "USER_LABEL";
        if (objectType == 7) return "IMPORTED_USER";
        return "SELECTED_USER";
    }

    /**
     * 原始状态 待使用0  [待生效1]  已过期2  已使用3  已禁用4  已删除5
     * 细化持券状态 0待使用1过期2被回收3被禁用4已使用
     * @param originStatus
     * @return
     */
    private Integer possessStageAdapter(Integer originStatus) {
        if (originStatus == 0 || originStatus == 1) return 0;
        // if (originStatus == 2) return 1;
        // if (originStatus == 3) return 4;
        // if (originStatus == 4) return 3;
        // if (originStatus == 5) return 2;
        return 1;
    }

    private Integer sourceAdapter(PfCouponDeliveryDetail originDeliv) {
        if (originDeliv.getCouponType() == 5) return 0;
        else return 1;
    }

    private CouponPossess possessAdapter(PfCouponDeliveryDetail originDeliv) {
        Builder<CouponPossess> builder = Builder.of(CouponPossess::new);
        builder.with(CouponPossess::setId, originDeliv.getId())
               .with(CouponPossess::setUid, originDeliv.getUid())
               .with(CouponPossess::setCouponId, originDeliv.getRelateCouponId())
               .with(CouponPossess::setCouponType, originDeliv.getCouponType())
               // setApplyScene 后面写sql脚本更新更快
               .with(CouponPossess::setApplyScene, originDeliv.getCouponType())
               .with(CouponPossess::setPossessStage, possessStageAdapter(originDeliv.getStatus()))
               .with(CouponPossess::setBusinessStage, originDeliv.getStatus() == 2 ? 1 : 0)
               .with(CouponPossess::setCtime, originDeliv.getCtime())
               .with(CouponPossess::setUtime, originDeliv.getUtime())
               .with(CouponPossess::setHasRead, originDeliv.getHasRead())
               .with(CouponPossess::setSource, 1)
               .with(CouponPossess::setSourceId, originDeliv.getRelateActivityId())
               .with(CouponPossess::setUsableTime, TemporalUtil.ofMilli(originDeliv.getEffectiveTime()))
               .with(CouponPossess::setExprTime,
                       Optional.ofNullable(originDeliv.getExpireTime())
                               .map(Instant::ofEpochMilli).map(DateUtil::toLocalDateTime).orElse(null));
        // setConsumeTime hbmmzlgl
        //  .with(CouponPossess::setConsumeTime, null)
        //  .with(CouponPossess::setBusinessId, null)
        //  .with(CouponPossess::setBusinessStage, null)
        //  .with(CouponPossess::effectEndTime)
        CouponPossess toSave = builder.build();
        return toSave;
    }

    private CouponPossess rewardAdapter(PfCouponRewardDetail reward) {
        Builder<CouponPossess> builder = Builder.of(CouponPossess::new);
        builder.with(CouponPossess::setId, reward.getRelateCouponDeliveryId())
               .with(CouponPossess::setConsumeTime, reward.getCtime())
               // 资产券之前存的是transf_record id
               .with(CouponPossess::setBusinessId,
                       Optional.ofNullable(reward.getOrderId())
                               .map(String::valueOf).orElse(null))
               .with(CouponPossess::setBusinessCoin, StringUtils.upperCase(reward.getCoin()))
               .with(CouponPossess::setExptDiscount, reward.getAmount())
               .with(CouponPossess::setBusinessStage, businesStageAdapter(reward))
               .with(CouponPossess::setExptEndTime, Optional.ofNullable(reward.getArrivedTime()).map(TemporalUtil::ofMilli).orElse(null));
        return builder.build();
    }

    private Integer businesStageAdapter(PfCouponRewardDetail reward) {
        if (reward.getCouponType() == 0) {// -1 1 2
            if (reward.getStatus() == -1) return BREAK_OFF.getCode();
            if (reward.getStatus() == 1) return CONTRIBUTING.getCode();
            if (reward.getStatus() == 2) return COMPLETE.getCode();
        }
        if (reward.getCouponType() == 5) {// -1 0 1
            if (reward.getStatus() == -1) return BREAK_OFF.getCode();
            if (reward.getStatus() == 0) return CONTRIBUTING.getCode();
            if (reward.getStatus() == 1) return COMPLETE.getCode();
        }
        return DEFAULT_UNUSED.getCode();
    }

    private LocalDateTime getExptEndTime(RewardDetailWithRuleOrder reward) {

        LocalDateTime ctime = reward.getCtime();
        if (reward.getCouponType() == 0) {
            InterCouponRule couponRule = JSON.toJavaObject(reward.getRule(), InterCouponRule.class);
            return ctime.plusDays(couponRule.getInterDays());
        }
        if (reward.getCouponType() == 1) {
            DeductCouponRule couponRule = JSON.toJavaObject(reward.getRule(), DeductCouponRule.class);
            return ctime.plusDays(couponRule.getDeductDays());
        }
        if (reward.getCouponType() == 5) {
            if (Objects.equals(reward.getActivateCondition(), 1)
                    || Objects.equals(reward.getActivateCondition(), 2)) {
                return ctime.plusDays(1);
            }
            if (Objects.equals(reward.getActivateCondition(), 3)) {
                return reward.getExptEndTime();
            }
        }
        return null;
    }


    public static void main(String[] args) {
        System.out.println(DateUtil.toLocalDateTime(Instant.ofEpochMilli(0L)));
        System.out.println(LocalDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.of("UTC+0")));
    }
}
