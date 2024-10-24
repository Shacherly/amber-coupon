package com.trading.backend.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.trading.backend.bo.WorthGrantBO;
import com.trading.backend.bo.WorthToGrantBO;
import com.trading.backend.client.IAssetServiceApi;
import com.trading.backend.client.IEarnServiceApi;
import com.trading.backend.client.ISymbolServiceApi;
import com.trading.backend.client.IUserServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.config.CouponAlarmProperty;
import com.trading.backend.config.CouponTaskProperty;
import com.trading.backend.config.NoviceProperty;
import com.trading.backend.constant.RedisKey;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.http.request.earn.opponent.CashEarnAcquireParam;
import com.trading.backend.http.response.CashGrantPopupVO;
import com.trading.backend.http.response.earn.CashEarnAcquireVO;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.pojo.WorthGrantDO;
import com.trading.backend.service.ItradingAlarm;
import com.trading.backend.service.IWorthGrantService;
import com.trading.backend.util.BeanMapper;
import com.trading.backend.util.Converter;
import com.trading.backend.util.NumberCriteria;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * @author ~~ trading.s
 * @date 17:54 10/15/21
 */
@Slf4j
@Service
public class WorthGrantServiceImpl implements IWorthGrantService {

    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private ISymbolServiceApi symbolService;
    @Autowired
    private RedisService redis;
    @Autowired
    private IUserServiceApi userServiceApi;
    @Autowired
    private IAssetServiceApi assetServiceApi;
    @Autowired
    private CouponTaskProperty couponTaskProperty;
    @Autowired
    @Qualifier("stringRedisTemplate")
    private StringRedisTemplate redisTemplate;
    @Autowired
    private IEarnServiceApi earnServiceApi;
    @Autowired
    private CouponPossessMapper possessMapper;
    @Autowired
    private ItradingAlarm tradingAlarm;
    @Autowired
    private CouponAlarmProperty alarmProperty;
    @Autowired
    private NoviceProperty noviceProperty;

    private static final ThreadLocal<DateTimeFormatter> DAILY_SUFFIX_FORMATTER = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy_MM_dd"));

    @Override
    public List<WorthToGrantBO> scanWorthGrants() {
        LocalDateTime now = LocalDateTime.now();
        List<WorthGrantDO> worthGrants = possessDao.getWorthGrants(
                CouponTypeEnum.CASHRETURN_TYPE.getCode(),
                now.plus(couponTaskProperty.getScanOffset() + 20_000, ChronoUnit.MILLIS)
        );
        return worthGrants.stream().map(BeanMapper::toGrantBo).collect(Collectors.toList());
    }

    @Override
    public void llegalInspect(WorthGrantBO grantBo, Consumer<String> consumer) {
        boolean passed = userServiceApi.clientKycPassed(grantBo.getUid());
        // 51、52、53都会检查KYC，但只有51的denied会被reset
        if (!passed) {
            if (grantBo.getApplyScene() == CouponApplySceneEnum.KYC_VERIFY_CASH) {
                CouponPossess toUpdate = new CouponPossess().setId(grantBo.getPossessId());
                toUpdate.born();
                possessMapper.updateByPrimaryKeySelective(toUpdate);
            }
            Optional.ofNullable(consumer).ifPresent(con -> con.accept(String.valueOf(grantBo.getPossessId())));
            throw new BusinessException(ExceptionEnum.KYC_DINED, grantBo.getUid());
        }
    }

    @Override
    public void dailyCellingInspect(WorthGrantBO grantBo) {
        if (!StringUtils.equalsIgnoreCase(grantBo.getGrantCoin(), couponTaskProperty.getGrantsCoin())) {
            log.warn("There is no need to inspect non-BTC grantObject {} for daily celling!", grantBo);
            return;
        }
        BigDecimal grantAmount = new BigDecimal(grantBo.getGrantSize());
        // if (!StringUtils.equalsIgnoreCase(grantBo.getGrantCoin(), couponTaskProperty.getGrantsCoin())) {
        //     grantAmount = symbolService.exchange(grantBo.getGrantCoin().toLowerCase(), new BigDecimal(grantBo.getGrantSize()));
        // }
        // else {
        //     grantAmount = new BigDecimal(grantBo.getGrantSize());
        // }

        LocalDate nowDate = TemporalUtil.defaultZoneNowDate();
        String dailySuffix = DAILY_SUFFIX_FORMATTER.get().format(nowDate);
        String dailyTotalKey = RedisKey.GRANTED_TOTAL_DAILY_PREFIX + dailySuffix;
        BigDecimal cachedTotal = Optional.ofNullable(redis.getCacheObject(dailyTotalKey))
                                         .map(String::valueOf)
                                         .map(BigDecimal::new)
                                         .orElseGet(() -> {
                                             redis.setCacheObject(dailyTotalKey, BigDecimal.ZERO);
                                             return BigDecimal.ZERO;
                                         });
        BigDecimal total = cachedTotal.add(grantAmount);
        String totalString = NumberCriteria.stripTrailing(total);
        boolean paused = false;

        BigDecimal grantsLimit2 = couponTaskProperty.getGrantsLimit2();
        BigDecimal grantsLimit1 = couponTaskProperty.getGrantsLimit1();
        if (total.compareTo(grantsLimit2) >= 0) {
            paused = true;
            boolean alarmed = Optional.ofNullable(redis.getCacheObject(RedisKey.GRANTED_CELLING2_ALARMED + dailySuffix))
                                      .map(Objects::toString)
                                      .map(Boolean::parseBoolean)
                                      .orElse(false);
            if (!alarmed) {
                redis.setCacheObject(RedisKey.GRANTED_CELLING2_ALARMED + dailySuffix, true);
                redis.deleteObject(RedisKey.GRANTED_CELLING2_ALARMED + DAILY_SUFFIX_FORMATTER.get().format(nowDate.minusDays(1)));
                CouponAlarmProperty.AlarmBody alarmBody = alarmProperty.getGrantCelling2();
                alarmBody.setMsg(MessageFormat.format(alarmBody.getMsg(), totalString));
                tradingAlarm.alarm(alarmBody);
            }
            throw new BusinessException(
                    ExceptionEnum.CASHRETURN_GRANT_CELLING,
                    NumberCriteria.stripTrailing(grantsLimit2) + couponTaskProperty.getGrantsCoin(),
                    String.valueOf(grantBo.getPossessId())
            );
            //  暂停该笔 第二天重新发起不用变更状态，直接在任务调度了
        }
        if (total.compareTo(grantsLimit1) >= 0) {
            boolean alarmed = Optional.ofNullable(redis.getCacheObject(RedisKey.GRANTED_CELLING1_ALARMED + dailySuffix))
                                      .map(Objects::toString)
                                      .map(Boolean::parseBoolean)
                                      .orElse(false);
            if (!alarmed) {
                redis.setCacheObject(RedisKey.GRANTED_CELLING1_ALARMED + dailySuffix, true);
                redis.deleteObject(RedisKey.GRANTED_CELLING1_ALARMED + DAILY_SUFFIX_FORMATTER.get().format(nowDate.minusDays(1)));
                CouponAlarmProperty.AlarmBody alarmBody = alarmProperty.getGrantCelling1();
                alarmBody.setMsg(MessageFormat.format(alarmBody.getMsg(), totalString));
                tradingAlarm.alarm(alarmBody);
            }
        }

    }

    @Override
    public void positionInspect(PossessDO possessDo) {
        CashCouponRule cashActivRule = (CashCouponRule) Converter.deserialize(possessDo.getCouponRule(), CouponTypeEnum.CASHRETURN_TYPE.getCode());
        CashEarnAcquireParam cashEarnAcquireParam =
                CashEarnAcquireParam.fromCashCouponRule(possessDo.getUid(), cashActivRule);
        // rpc获取仓位信息
        CashEarnAcquireVO cashEarnAcquireVo = earnServiceApi.getCashEarnAcquireVO(cashEarnAcquireParam);
        // 没有符合的仓位
        if (Objects.isNull(cashEarnAcquireVo)) {
            LocalDateTime now = LocalDateTime.now();
            // 券已过期需要更新过期状态
            if (now.isAfter(possessDo.getAvailableEnd())) {
                CouponPossess toUpdate = new CouponPossess().setId(possessDo.getPossessId());
                toUpdate.expire();
                possessMapper.updateByPrimaryKeySelective(toUpdate);
                throw new BusinessException(ExceptionEnum.POSSESS_EXPIRED_CAUSEBY_REDEEM, possessDo.getBusinessId());
            }
            // 否则更新为初始状态
            CouponPossess toUpdate = new CouponPossess().setId(possessDo.getPossessId());
            toUpdate.born();
            possessMapper.updateByPrimaryKeySelective(toUpdate);
            throw new BusinessException(ExceptionEnum.POSSESS_RESET_CAUSEBY_REDEEM, possessDo.getBusinessId());
        }
    }

    @Override
    public void doCashGrant(WorthGrantBO grantBo, PossessDO possessDo) {
        if (grantBo.getApplyScene() == CouponApplySceneEnum.EARN_CASH) {
            // 兜底判断，如果赎回的kafka没收到，则这里的判断是有必要的，直接重新检查仓位
            // 无符合的该奖励不会发放，券会恢复初始状态或者过期状态
            positionInspect(possessDo);
        }

        boolean success = assetServiceApi.activityDeposit(String.valueOf(grantBo.getPossessId()), grantBo.getUid(), grantBo.getGrantSize(), grantBo.getGrantCoin());
        if (!success) {
            log.error("GrantTaskId {} has executed, duplicated deposit is forbidden", possessDo.getPossessId());
            return;
        }
        if (noviceProperty.getSyncretiCoupons().contains(grantBo.getCouponId())) {
            if (StringUtils.equalsIgnoreCase(grantBo.getGrantCoin(), couponTaskProperty.getGrantsCoin())) {
                BigDecimal grantAmount = new BigDecimal(grantBo.getGrantSize());
                log.info("CashGrantedObject={}, Novice3Task", grantBo);
                String todayTotalKey = RedisKey.GRANTED_TOTAL_DAILY_PREFIX + DAILY_SUFFIX_FORMATTER.get().format(LocalDate.now());
                // 更新日发放总量cache
                redis.incrment(todayTotalKey, grantAmount.doubleValue());
            }
            // BigDecimal grantAmount = BigDecimal.ZERO;
            // if (!StringUtils.equalsIgnoreCase(grantBo.getGrantCoin(), couponTaskProperty.getGrantsCoin())) {
            //     grantAmount = symbolService.exchange(grantBo.getGrantCoin().toLowerCase(), new BigDecimal(grantBo.getGrantSize())).setScale(8, RoundingMode.HALF_UP);
            // }
            // else {
            //     grantAmount = new BigDecimal(grantBo.getGrantSize());
            // }
            // log.info("CashGrantedObject={}", grantBo);
            // String todayTotalKey = RedisKey.GRANTED_TOTAL_DAILY_PREFIX + DAILY_SUFFIX_FORMATTER.get().format(LocalDate.now());
            //
            // // 更新日发放总量cache
            // redis.incrment(todayTotalKey, grantAmount.doubleValue());
        }
        List<CashGrantPopupVO> popupList = redis.getCacheMapValue(RedisKey.POPUP_CASH, grantBo.getUid());
        if (CollectionUtil.isEmpty(popupList)) popupList = new ArrayList<>();
        popupList.add(new CashGrantPopupVO().setAmout(grantBo.getGrantSize()).setCoin(grantBo.getGrantCoin().toLowerCase()));

        Map<String, List<CashGrantPopupVO>> popupMap = new HashMap<>();
        popupMap.put(grantBo.getUid(), popupList);

        redis.setCacheMap(RedisKey.POPUP_CASH, popupMap);
        log.info("CachedGrantedPopup={} to redis", popupMap);
    }
}
