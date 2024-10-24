package com.trading.backend.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.google.common.collect.Lists;
import com.trading.backend.bo.WorthGrantBO;
import com.trading.backend.client.IEarnServiceApi;
import com.trading.backend.client.ISymbolServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.common.enums.PossessBusinesStageEnum;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.config.CouponActivateProperty;
import com.trading.backend.config.CouponTaskProperty;
import com.trading.backend.config.GlobalSystemProperty;
import com.trading.backend.config.NoviceProperty;
import com.trading.backend.constant.RedisKey;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.http.request.earn.opponent.CashEarnAcquireParam;
import com.trading.backend.http.response.earn.CashEarnAcquireVO;
import com.trading.backend.kafka.message.DepositInfoModel;
import com.trading.backend.kafka.message.EarnConsumeModel;
import com.trading.backend.kafka.message.KycStageModel;
import com.trading.backend.kafka.message.UserRegistryModel;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.pojo.CashActivDO;
import com.trading.backend.pojo.CouponContributeParam;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.service.ICashCouponService;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.template.ActivateStrategyManager;
import com.trading.backend.template.IActivateStrategy;
import com.trading.backend.util.Converter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;


/**
 * @author ~~ trading.s
 * @date 09:02 10/27/21
 */
@Slf4j
@Service
public class CashCouponServiceImpl implements ICashCouponService {


    @Autowired
    private IPossesService possesService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private CouponActivateProperty couponProperty;
    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private IEarnServiceApi earnServiceApi;
    @Autowired
    private RedisService redis;
    @Autowired
    private CouponTaskProperty couponTaskProperty;
    @Autowired
    private NoviceProperty noviceProperty;
    @Autowired
    private CouponPossessMapper possessMapper;
    @Autowired
    private GlobalSystemProperty systemProperty;
    @Autowired
    private ISymbolServiceApi symbolService;

    static public final Comparator<PossessDO> ACTIVATE_COMPARATOR = Comparator.comparing(PossessDO::getWorth).reversed().thenComparing(PossessDO::getAvailableEnd);

    public Comparator<PossessDO> getComparator(Map<String, BigDecimal> prices) {
        Comparator<PossessDO> comparator = (p1, p2) -> {
            String pair1 = StringUtils.upperCase(p1.getWorthCoin() + "_USD");
            BigDecimal worth1 = NumberUtil.mul(prices.get(pair1), p1.getWorth());
            String pair2 = StringUtils.upperCase(p2.getWorthCoin() + "_USD");
            BigDecimal worth2 = NumberUtil.mul(prices.get(pair2), p2.getWorth());
            return worth2.compareTo(worth1);
        };
        return comparator.thenComparing(PossessDO::getAvailableEnd);
    }

    @Override
    public void grantOnRegistry(UserRegistryModel model) {
        ZonedDateTime registryTime = TemporalUtil.ofDefaultZoneMilli(model.getRegistryTime());
        int novicePhase = noviceProperty.getNovicePhase();
        List<Long> registCoupon = null;
        ZonedDateTime start = null;
        ZonedDateTime end = null;
        if (novicePhase == 2) {
            registCoupon = noviceProperty.getOldRegistCoupon();
            start = Instant.now().atZone(systemProperty.getZoneId()).minusDays(10);
            end = Instant.now().atZone(systemProperty.getZoneId()).plusDays(10);
        }
        else {
            registCoupon = noviceProperty.getRegistCouponV2();
            start = noviceProperty.getStart();
            end = noviceProperty.getEnd();
        }
        if (registryTime.isBefore(start) || registryTime.isAfter(end)) {
            log.info("Out of novice-3 duration");
            return;
        }
        try {
            couponService.receive(model.getUid(), registCoupon, null, PossessSourceEnum.NEW_REGISTER);
        } catch (Exception e) {
            log.error("grantOnRegistryError, {}", e.getMessage(), e);
        }
    }

    @Override
    public void activOnKycSubmit(KycStageModel model, BiConsumer<String, LocalDateTime> post) {

        if (!Objects.equals(model.getStage(), 2)) return;

        List<PossessDO> possesses = possesService.getAppyScenePossess(model.getUid(), CouponApplySceneEnum.KYC_VERIFY_CASH);
        log.info("Accessed_KycStageModel_possess = {}", possesses);
        if (CollectionUtil.isEmpty(possesses)) return;

        IActivateStrategy strategy = ActivateStrategyManager.getStrategyFunction(CouponApplySceneEnum.KYC_VERIFY_CASH).apply(model);
        try {
            boolean down = false;
            // 满足条件皆可激活
            for (PossessDO possess : possesses) {
                CouponContributeParam activateParam = strategy.buildActivateParam(null, couponProperty);
                couponService.cashCouponActivate(activateParam, possess);
                post.accept(StringUtils.join(possess.getPossessId(), ":", model.getUid()), possess.getExptEndTime());
                log.info("KycStageModel_{} has activate possess_{}", model, possess);
                down = true;
            }
            if (!down)
                log.error("{} can't activate anyone of cash poosess", model);
        } catch (Exception e) {
            log.error("activOnKycPassedError, {}", e.getMessage(), e);
        }


    }

    @Override
    public void activOnDepositSettle(DepositInfoModel model, BiConsumer<String, LocalDateTime> post) {
        List<PossessDO> possesses = possesService.getSortedScenedPossess(model.getUid(), CouponApplySceneEnum.DEPOSIT_CASH, getComparator(symbolService.getPrices()));
        log.info("Accessed_DepositInfoModel_possess = {}", possesses);
        if (CollectionUtil.isEmpty(possesses) || model.internal()) return;

        IActivateStrategy strategy = ActivateStrategyManager.getStrategyFunction(CouponApplySceneEnum.DEPOSIT_CASH).apply(model);
        try {
            boolean down = false;
            // 满足条件皆可激活
            for (PossessDO possess : possesses) {
                CashCouponRule rule = (CashCouponRule) Converter.deserialize(possess.getCouponRule(), possess.getCouponType());
                if (!possess.depositInspect(model, rule)) continue;
                CouponContributeParam activateParam = strategy.buildActivateParam(null, couponProperty);
                couponService.cashCouponActivate(activateParam, possess);
                post.accept(StringUtils.join(possess.getPossessId(), ":", model.getUid()), possess.getExptEndTime());
                log.info("DepositInfoModel_{} has activate possess_{}", model, possess);
                down = true;
                break;
            }
            if (!down)
                log.error("{} can't activate anyone of cash poosess", model);
        } catch (Exception e) {
            log.error("activOnDepositSettleError, {}", e.getMessage(), e);
        }
    }

    @Override
    public void activOnUserSubscribe(EarnConsumeModel model, BiConsumer<String, LocalDateTime> post) {

        if (!Objects.equals(model.getEarnBusinessType(), 1)) return;
        log.info("activOnUserSubscribe, {}", model);

        List<PossessDO> possesses = possesService.getSortedScenedPossess(model.getUid(), CouponApplySceneEnum.EARN_CASH, getComparator(symbolService.getPrices()));
        log.info("Accessed_EarnConsumeModel_possess = {}", possesses);
        if (CollectionUtil.isEmpty(possesses)) return;

        IActivateStrategy strategy = ActivateStrategyManager.getStrategyFunction(CouponApplySceneEnum.EARN_CASH).apply(model);
        try {
            boolean down = false;
            // 满足条件皆可激活
            for (PossessDO possess : possesses) {
                CashCouponRule rule = (CashCouponRule) Converter.deserialize(possess.getCouponRule(), possess.getCouponType());
                if (!possess.subscribeInspect(model, rule)) continue;
                CouponContributeParam activateParam = strategy.buildActivateParam(rule, couponProperty);
                couponService.cashCouponActivate(activateParam, possess);
                post.accept(StringUtils.join(possess.getPossessId(), ":", model.getUid()), possess.getExptEndTime());
                log.info("EarnConsumeModel_{} has activate possess_{}", model, possess);
                down = true;
                break;
            }
            if (!down)
                log.error("{} can't activate anyone of cash poosess", model);
        } catch (Exception e) {
            log.error("activOnUserSubscribeError={}", e.getMessage(), e);
        }

    }

    @Override
    public boolean onRedeemRefreshCashStage(EarnConsumeModel model, BiConsumer<String, LocalDateTime> post) {

        if (model.getEarnBusinessType() != 2) return false;
        log.info("refreshCashOnRedeem, {}", model);
        // 部分赎回 天数肯定是满足的，还要看看金额和币种
        CashActivDO cashActivDO = possessDao.getPendingCashActivDO(model.getUid(), model.getPositionId());
        CashCouponRule cashActivRule = cashActivDO.getCashActivRule();
        // 赎回后仍满足，
        if (cashActivRule.anyCashRuleEligible(model.getHoldingCoin(), new BigDecimal(model.getHoldingSize()))) {
            // 不管
            return true;
        }
        // 赎回后该笔已经不满足了，先判断券的过期状态  再查询其他的理财仓位
        else {
            LocalDateTime nowTime = LocalDateTime.now();
            // 激活不满足了，券已过期需要更新过期状态
            if (nowTime.isAfter(cashActivDO.getExprTime())) {
                CouponPossess toUpdate = new CouponPossess().setId(cashActivDO.getPossessId());
                toUpdate.expire();
                possessMapper.updateByPrimaryKeySelective(toUpdate);
                throw new BusinessException(ExceptionEnum.POSSESS_EXPIRED_CAUSEBY_REDEEM, model.getPositionId());
            }
            // 激活不满足了，券没过期 ，查询其他仓位
            CashEarnAcquireParam cashEarnAcquireParam =
                    CashEarnAcquireParam.fromCashCouponRule(model.getUid(), cashActivRule);
            // rpc获取仓位信息
            CashEarnAcquireVO cashEarnAcquireVo = earnServiceApi.getCashEarnAcquireVO(cashEarnAcquireParam);
            // 没有符合的仓位  更新为待激活状态
            if (Objects.isNull(cashEarnAcquireVo)) {
                CouponPossess toUpdate = new CouponPossess().setId(cashActivDO.getPossessId());
                toUpdate.born();
                possessMapper.updateByPrimaryKeySelective(toUpdate);
                throw new BusinessException(ExceptionEnum.POSSESS_RESET_CAUSEBY_REDEEM, model.getPositionId());
            }

            // 有满足的在持理财
            CouponPossess newPossess = new CouponPossess();
            // newPossess.setId(cashActivDO.getPossessId());
            newPossess.setBusinessId(cashEarnAcquireVo.getPosition_id());
            LocalDateTime exptEndTime = TemporalUtil.ofMilli(cashEarnAcquireVo.getCreate_time()).plusDays(cashActivRule.getMinSubscrDays());
            newPossess.setExptEndTime(exptEndTime);

            Example example = new Example(CouponPossess.class);
            example.createCriteria()
                   .andEqualTo("id", cashActivDO.getPossessId())
                   .andEqualTo("businessStage", PossessBusinesStageEnum.CONTRIBUTING.getCode());
            possesService.updatePossess(newPossess, example);
            post.accept(StringUtils.join(cashActivDO.getPossessId(), ":", model.getUid()), exptEndTime);
            return false;
        }
    }


    @Override
    public void accomplish(WorthGrantBO grantBo) {
        CouponPossess possess = new CouponPossess();
        possess.accomplish();

        Example example = new Example(CouponPossess.class);
        example.createCriteria()
               .andEqualTo("id", grantBo.getPossessId())
               .andEqualTo("uid", grantBo.getUid())
               .andEqualTo("possessStage", 0)
               .andEqualTo("businessStage", 2);

        possesService.updatePossess(possess, example);

    }

    @Override @Deprecated
    public void accomplish1(WorthGrantBO grantBo) {
        CouponPossess possess = new CouponPossess();
        possess.accomplish();

        Example example = new Example(CouponPossess.class);
        example.createCriteria()
               .andEqualTo("id", grantBo.getPossessId())
               .andEqualTo("uid", grantBo.getUid())
               .andIn("possessStage", Lists.newArrayList(0, 1))
               .andIn("businessStage", Lists.newArrayList(0, 1, 2));

        possesService.updatePossess(possess, example);
    }

    @Override
    public BiConsumer<String, LocalDateTime> postback() {
        return (value, score) -> {
            ZSetOperations.TypedTuple<Object> tups = new DefaultTypedTuple<>(value, TemporalUtil.toEpochMilli(score).doubleValue());
            redis.setSortedSet(RedisKey.TO_GRANT_CASH_POOL, tups);
        };
    }
}
