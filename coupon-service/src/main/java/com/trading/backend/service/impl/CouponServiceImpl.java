package com.trading.backend.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.google.common.cache.LoadingCache;
import com.trading.backend.annotation.Handler;
import com.trading.backend.annotation.PostHandle;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.client.ISymbolServiceApi;
import com.trading.backend.client.IUserServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.common.enums.CouponStatusEnum;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.enums.EventStageEnum;
import com.trading.backend.common.enums.EventTypeEnum;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.enums.NewUserTaskStatusEnum;
import com.trading.backend.common.http.PageResult;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.config.CouponAlarmProperty;
import com.trading.backend.config.ExecutorConfigurer;
import com.trading.backend.config.NoviceProperty;
import com.trading.backend.constant.RedisKey;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponEvent;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.domain.ProfitCouponRule;
import com.trading.backend.domain.TrialCouponRule;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.exception.VisibleException;
import com.trading.backend.http.request.CouponConsumeParam;
import com.trading.backend.http.request.ExternalHeaderUid;
import com.trading.backend.http.request.ReddotReadParam;
import com.trading.backend.http.request.TypedParam;
import com.trading.backend.http.request.dual.DualConsumeParam;
import com.trading.backend.http.request.loan.LoanConsumeParam;
import com.trading.backend.coupon.http.response.*;
import com.trading.backend.http.response.endpoint.FullScaleCouponVO;
import com.trading.backend.kafka.message.KycStageModel;
import com.trading.backend.mapper.CouponEventMapper;
import com.trading.backend.mapper.CouponMapper;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.pojo.CouponContributeParam;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.service.ICashCouponService;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.service.ItradingAlarm;
import com.trading.backend.util.ArrayUtil;
import com.trading.backend.util.BeanMapper;
import com.trading.backend.util.ContextHolder;
import com.trading.backend.util.Converter;
import com.trading.backend.common.util.Functions;
import com.trading.backend.common.util.Predicator;
import com.trading.backend.util.PageContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author ~~ trading.s
 * @date 16:21 09/21/21
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "buffered:coupon:")
public class CouponServiceImpl implements ICouponService {

    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private CouponPossessMapper couponPossessMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private CouponPossessMapper possessMapper;
    @Autowired
    private IPossesService possesService;
    @Autowired
    private RedisService redis;
    @Autowired
    private ISymbolServiceApi symbolServiceApi;
    @Autowired @Qualifier("serviceWarnCache")
    LoadingCache<String, String> warnCache;
    @Autowired
    private NoviceProperty noviceProperty;
    @Autowired
    private IUserServiceApi userServiceApi;
    @Autowired
    private ICashCouponService iCashCoupService;
    @Autowired
    private CouponAlarmProperty alarmProperty;
    @Autowired
    private ItradingAlarm tradingAlarm;
    @Autowired
    private CouponEventMapper eventMapper;
    @Value("${dual-trial.id}")
    private String dualTrialId;
    @Autowired
    private ExecutorConfigurer exeConf;

    private static final CouponConsumeParam PLACE_HOLDER = new CouponConsumeParam();


    @Override
    public List<Coupon> getCoupons(List<Long> ids) {

        if (CollectionUtil.isEmpty(ids)) return Collections.emptyList();
        List<Object> cacheList = redis.getMultiCacheMapValue(
                RedisKey.BUFFERED_COUPONS, Functions.toObjects(ids, String::valueOf));
        if (cacheList.size() == ids.size()) {
            return Functions.mapper(cacheList, Coupon.class);
        }
        if (ids.size() == 1) {
            Coupon coupon = couponMapper.selectByPrimaryKey(ids.get(0));
            if (Objects.isNull(coupon)) return Collections.emptyList();
            redis.setCacheMapValue(RedisKey.BUFFERED_COUPONS, coupon.getId(), coupon);
            return Collections.singletonList(coupon);
        }

        Example example = new Example(Coupon.class);
        example.createCriteria().andIn("id", ids);
        List<Coupon> coupons = couponMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(coupons)) return Collections.emptyList();

        Map<String, Coupon> collect = Functions.toMap(coupons, Coupon::getId);
        redis.setCacheMap(RedisKey.BUFFERED_COUPONS, collect);
        return coupons;
    }

    @Override
    public void clubRecevCheck(String uid, Long couponId) {
        Example example = new Example(CouponPossess.class);
        example.selectProperties("id");
        example.createCriteria().andEqualTo("uid", uid).andEqualTo("couponId", couponId)
                .andBetween("ctime", TemporalUtil.thisMonthStart(), TemporalUtil.thisMonthEnd());
        CouponPossess possess = couponPossessMapper.selectOneByExample(example);
        if (possess == null) return;
        throw new BusinessException(ExceptionEnum.CLUB_GIFT_UNREPEATABLE);
    }

    @Override
    public void possessLimitCheck(String uid, Coupon coupon) {
        if (!possessLimitPermit(uid, coupon))
            throw new BusinessException(ExceptionEnum.POSSESS_EXCEED_LIMIT, uid, coupon.getId());
    }

    @Override
    public boolean possessLimitPermit(String uid, Coupon coupon) {
        Example example = new Example(CouponPossess.class);
        example.selectProperties("id");
        example.createCriteria().andEqualTo("uid", uid).andEqualTo("couponId", coupon.getId());
        List<CouponPossess> possesses = couponPossessMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(possesses)) return true;
        long count = possesses.stream().filter(Predicator.isNotEqual(CouponPossess::getId, null)).count();
        if (count >= coupon.getPossessLimit()) {
            String message = MessageFormat.format(ExceptionEnum.POSSESS_EXCEED_LIMIT.getReason(), uid, coupon.getId());
            log.error(message);
            List<String> warn = ContextHolder.getWarn();
            warn.add(message);
            return false;
        }
        return true;
    }

    @Override
    public List<String> possessLimitReduce(List<String> uids, Coupon coupon) {
        Example example = new Example(CouponPossess.class);
        example.selectProperties("id", "uid");
        example.createCriteria().andIn("uid", uids).andEqualTo("couponId", coupon.getId());
        List<CouponPossess> possesses = couponPossessMapper.selectByExample(example);
        if (CollectionUtil.isEmpty(possesses)) return uids;
        Map<String, List<CouponPossess>> possessMap = Functions.groupingBy(possesses, CouponPossess::getUid);
        Integer limit = coupon.getPossessLimit();
        Set<String> exceedUids = new HashSet<>();
        possessMap.forEach((kUid, vPossList) -> {
            if (vPossList.size() >= limit) exceedUids.add(kUid);
        });
        if (CollectionUtil.isNotEmpty(exceedUids)) {
            if (exceedUids.size() > 3) {
                List<String> sub = CollectionUtil.sub(exceedUids, 0, 3);
                sub.add("... and so on");
                throw new BusinessException(ExceptionEnum.POSSESS_EXCEED_LIMIT, sub, coupon.getId());
            }
            else
                throw new BusinessException(ExceptionEnum.POSSESS_EXCEED_LIMIT, exceedUids, coupon.getId());
        }
        return uids;
    }

    @Override
    public void couponTotalCheck(long delta, List<Coupon> coupons) {
        // Predicate<Coupon> insufficient = coupon -> coupon.getTotal() - coupon.getIssue() - coupon.getPreLock() < delta;
        List<Long> notEnough = coupons.stream().filter(val -> val.unSufficient(delta)).map(Coupon::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(notEnough))
            throw new BusinessException(ExceptionEnum.COUPON_REST_LACK, notEnough);
    }

    @Override
    public void statusCheck(List<Coupon> coupons) {
        List<Long> notAvailable = coupons.stream().filter(cou -> !cou.available()).map(Coupon::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(notAvailable))
            throw new BusinessException(ExceptionEnum.COUPON_DISABLE, notAvailable);
    }


    /**
     * 单人单张领券
     * @param uid
     * @param couponId
     * @param sourceId
     * @param sourceEnum
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PostHandle(handler = Handler.COUPON_ACTIVATE)
    public List<BasalExportPossessBO> receive(String uid, Long couponId, Long sourceId, PossessSourceEnum sourceEnum) {
        if (StringUtils.isBlank(uid))
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "uid");
        if (Objects.isNull(couponId))
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "couponId");

        List<Coupon> coupons = getAndPresentCheck(Collections.singletonList(couponId));
        statusCheck(coupons);
        couponTotalCheck(1, coupons);
        Coupon coupon = coupons.get(0);

        possessLimitCheck(uid, coupon);

        CouponPossess possess = Converter.fromCoupon(uid, coupon, sourceId, sourceEnum, false);
        try {
            couponPossessMapper.insertSelective(possess);
            List<BasalExportPossessBO> exports = Collections.singletonList(Converter.fromNewAcquired(coupon, possess));
            issuesIncrease(Collections.singletonList(couponId));

            exeConf.execute(() -> {
                try {
                    checkRemainingAlert(coupon);
                    possesService.issueNotify(exports, sourceEnum);
                } catch (Exception e) {
                    log.error("SingleUserReceive_NotifyError={}", e.getMessage(), e);
                }
            }, true);

            return exports;
        } catch (Exception e) {
            log.error("SingleUserReceiveError={}", e.getMessage(), e);
            throw new BusinessException(ExceptionEnum.INSERT_ERROR);
        }
    }


    /**
     * 单人批量领券，如果有部分全领取达到上限只记录错误，不终止其他券的领取
     * @param uid
     * @param couponIds
     * @param sourceId
     * @param sourceEnum
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PostHandle(handler = Handler.COUPON_ACTIVATE)
    public List<BasalExportPossessBO> receive(String uid, List<Long> couponIds, Long sourceId, PossessSourceEnum sourceEnum) {
        if (StringUtils.isBlank(uid))
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "uid");
        if (CollectionUtil.isEmpty(couponIds))
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "couponIds");

        List<Coupon> coupons = getAndPresentCheck(couponIds);
        statusCheck(coupons);
        couponTotalCheck(1, coupons);

        List<BasalExportPossessBO> exports = new ArrayList<>();
        //
        List<CouponPossess> possesses = coupons.stream().filter(val -> possessLimitPermit(uid, val))
                .map(coupon -> {
                    CouponPossess possess = Converter.fromCoupon(uid, coupon, sourceId , sourceEnum, false);
                    exports.add(Converter.fromNewAcquired(coupon, possess));
                    return possess;
                }).collect(Collectors.toList());
        try {
            possessDao.batchInsertWithoutId(possesses);
            issuesIncrease(couponIds);

            exeConf.execute(() -> {
                try {
                    coupons.forEach(this::checkRemainingAlert);
                    possesService.issueNotify(exports, sourceEnum);
                } catch (Exception e) {
                    log.error("SingleUser_MultiReceive_NotifyError={}", e.getMessage(), e);
                }
            }, true);

            return exports;
        } catch (Exception e) {
            log.error("SingleUser_MultiReceiveError={}", e.getMessage(), e);
            throw new BusinessException(e, ExceptionEnum.INSERT_ERROR);
        }
    }


    /**
     * 多用户批量领券（如果全量用户额外再写方法）
     * @param uids
     * @param couponIds
     * @param sourceId
     * @param sourceEnum
     * @param preGrant
     * @return
     */
    @Override
    @PostHandle(handler = Handler.COUPON_ACTIVATE)
    @Transactional(rollbackFor = Exception.class)
    public List<BasalExportPossessBO> receiveMultiUser(List<String> uids, List<Long> couponIds, Long sourceId, PossessSourceEnum sourceEnum, boolean preGrant) {
        if (CollectionUtil.isEmpty(uids))
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "uids");
        if (CollectionUtil.isEmpty(couponIds))
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "couponIds");

        List<Coupon> coupons = getAndPresentCheck(couponIds);
        statusCheck(coupons);
        couponTotalCheck(uids.size(), coupons);

        List<BasalExportPossessBO> exports = new ArrayList<>();
        List<CouponPossess> toInserts = new ArrayList<>(uids.size() * couponIds.size() * 2);
        for (Coupon coupon : coupons) {
            List<String> toGrantUids = possessLimitReduce(uids, coupon);
            List<CouponPossess> collect = toGrantUids.stream().map(uid -> {
                CouponPossess possess = Converter.fromCoupon(uid, coupon, sourceId , sourceEnum, preGrant);
                exports.add(Converter.fromNewAcquired(coupon, possess));
                return possess;
            }).collect(Collectors.toList());
            toInserts.addAll(collect);
        }

        try {
            possessDao.batchInsertWithoutId(toInserts);
            if (!preGrant)
                // 非审批发放才需要更新已发放数量
                issuesIncrease(couponIds, uids.size());
            if (preGrant)
                // 审批发放需要更新预锁定数量
                couponPreLock(couponIds, (long) uids.size());

            exeConf.execute(() -> {
                try {
                    log.info("Nothing");
                    coupons.forEach(this::checkRemainingAlert);
                    possesService.issueNotify(exports, sourceEnum);
                } catch (Exception e) {
                    log.error("MultiUser_MultiReceive_NotifyError={}", e.getMessage(), e);
                }
            }, true);

            return exports;
        } catch (Exception e) {
            log.error("MultiUser_MultiReceiveError={}", e.getMessage(), e);
            throw new BusinessException(ExceptionEnum.INSERT_ERROR);
        }


    }


    /**
     * event批量发券，券的合规状态已经提前检查，这里直接批量发放
     * @param uids
     * @param coupons
     * @param sourceId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @PostHandle(handler = Handler.COUPON_ACTIVATE)
    public List<BasalExportPossessBO> eventCouponIssue(List<String> uids, List<Coupon> coupons, Long sourceId) {

        List<BasalExportPossessBO> exports = new ArrayList<>();
        List<CouponPossess> toInserts = new ArrayList<>(uids.size() * coupons.size() * 2);
        for (Coupon coupon : coupons) {
            List<String> toGrantUids = possessLimitReduce(uids, coupon);
            List<CouponPossess> collect = toGrantUids.stream().map(uid -> {
                CouponPossess possess = Converter.fromCoupon(uid, coupon, sourceId, PossessSourceEnum.EVENTS_GRANT, false);
                exports.add(Converter.fromNewAcquired(coupon, possess));
                return possess;
            }).collect(Collectors.toList());
            toInserts.addAll(collect);
        }

        try {
            possessDao.batchInsertWithoutId(toInserts);
            issuesIncrease(Functions.toList(coupons, Coupon::getId), uids.size());

            exeConf.execute(() -> {
                try {
                    coupons.forEach(this::checkRemainingAlert);
                    possesService.issueNotify(exports, PossessSourceEnum.EVENTS_GRANT);
                } catch (Exception e) {
                    log.error("EventCouponIssue_NotifyError={}", e.getMessage(), e);
                }
            }, true);

        } catch (Exception e) {
            log.error("EventCouponIssueError={}", e.getMessage(), e);
            throw new BusinessException(e.getMessage(), ExceptionEnum.INSERT_ERROR);
        }
        return exports;
    }

    @Override
    public int cashKycRemedy(List<CouponPossess> sources) {
        log.info("CashKycRemedyPossess={}", sources);
        List<CouponPossess> kycPossess = Functions.filter(sources, Predicator.isEqual(CouponPossess::getApplyScene, CouponApplySceneEnum.KYC_VERIFY_CASH.getCode()));
        List<String> uids = Functions.toList(kycPossess, CouponPossess::getUid);
        log.info("CashKycRemedyUids={}", uids);
        List<String> kycPassUids = Functions.filter(uids, Predicator.isEqual(userServiceApi::clientKycPassed, true));
        log.info("CashKycRemedyInCouponGrants={}", kycPassUids);
        kycPassUids.forEach(uid -> iCashCoupService.activOnKycSubmit(KycStageModel.kycPassMock(uid), iCashCoupService.postback()));
        return kycPassUids.size();
    }

    @Override
    public int cashKycReactiv(List<String> possUids) {
        if (CollectionUtil.isEmpty(possUids)) {
            log.error("NullCashKycReactivUids");
            return 0;
        }
        // uid去重
        List<String> distinctUids = possUids.stream().distinct().collect(Collectors.toList());

        Set<String> passedUids = userServiceApi.getMultiPassedUser(distinctUids);
        log.info("cashKycReactiv_UIDS={}, passed_UIDS={}", distinctUids, passedUids);
        passedUids.forEach(uid -> iCashCoupService.activOnKycSubmit(KycStageModel.kycPassMock(uid), iCashCoupService.postback()));
        return passedUids.size();
    }

    /**
     * 批量更新券的发放数量，步长统一固定为 1
     * @param couponIds
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issuesIncrease(List<Long> couponIds) {
        if (CollectionUtil.isEmpty(couponIds) ) return;
        redis.deleteCacheMap(RedisKey.BUFFERED_COUPONS, Functions.toObjects(couponIds, String::valueOf));
        int effects = couponMapper.issuesUpdate(couponIds, 1);
        if (effects != couponIds.size()) {
            log.error("UPDATE_ERROR for Some coupons in {} are lack of remaining quantity", couponIds);
            throw new BusinessException(ExceptionEnum.UPDATE_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issuesIncrease(List<Long> couponIds, int increase) {
        if (CollectionUtil.isEmpty(couponIds) || increase < 1) return;
        redis.deleteCacheMap(RedisKey.BUFFERED_COUPONS, Functions.toObjects(couponIds, String::valueOf));
        int effects = couponMapper.issuesUpdate(couponIds, increase);
        if (effects != couponIds.size()) {
            log.error("UPDATE_ERROR for Some coupons in {} are lack of remaining quantity", couponIds);
            throw new BusinessException(ExceptionEnum.COUPON_REST_LACK, couponIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrIssueReleasePrelock(List<Long> couponIds, int increase) {
        if (CollectionUtil.isEmpty(couponIds) || increase < 1) return;
        redis.deleteCacheMap(RedisKey.BUFFERED_COUPONS, Functions.toObjects(couponIds, String::valueOf));
        int effects = couponMapper.issuesPrelokcUpdate(couponIds, increase);
        if (effects != couponIds.size()) {
            log.error("UPDATE_ERROR for Some coupons in {} are lack of remaining quantity", couponIds);
            throw new BusinessException(ExceptionEnum.COUPON_REST_LACK, couponIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releasePrelock(List<Long> couponIds, int increase) {
        if (CollectionUtil.isEmpty(couponIds) || increase < 1) return;
        redis.deleteCacheMap(RedisKey.BUFFERED_COUPONS, Functions.toObjects(couponIds, String::valueOf));
        int effects = couponMapper.releasePrelock(couponIds, increase);
        if (effects != couponIds.size()) {
            log.error("RELEASE_PRELOCK_UPDATE_ERROR for coupons in {} prelock is less than increase", couponIds);
            throw new BusinessException(ExceptionEnum.COUPON_REST_LACK, couponIds);
        }
    }

    /**
     * 返回的是券发放数量的占比  80% 返回 80  90% 返回 90
     * @param coupon
     * @return
     */
    @Override
    public Long checkRemainingAlert(Coupon coupon) {
        if (coupon.getTotal() == 0L) return null;
        long rate = coupon.getIssue() * 100 / coupon.getTotal();
        if (((rate == 80) || (rate > 80 && ((rate - 80) % 20 == 0)))) {
            CouponAlarmProperty.AlarmBody alarmBody = alarmProperty.getQuantityRemain();
            alarmBody.setMsg(MessageFormat.format(alarmBody.getMsg(), coupon.getName(), rate));
            tradingAlarm.alarm(alarmBody);
            return rate;
            // TODO: 10/18/21 消息触达
        }
        return null;
    }

    @Override
    public List<Coupon> getAndPresentCheck(List<Long> couponIds) {
        List<Coupon> coupons = this.getCoupons(couponIds);

        Set<Long> presentIds = Functions.toSet(coupons, Coupon::getId);
        List<Long> notPresentIds = couponIds.stream().filter(Predicator.existIn(presentIds).negate()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(notPresentIds))
            throw new BusinessException(ExceptionEnum.COUPON_NOT_PRESENT, notPresentIds);
        return coupons;
    }

    @Override
    public void remainCheck4Event(List<Coupon> coupons, long delta) {
        List<Long> insufficients = Functions.filter(coupons, cou -> cou.getTotal() - cou.getIssue() - cou.getPreLock() < delta, Coupon::getId);
        if (CollectionUtil.isNotEmpty(insufficients))
            throw new BusinessException(ExceptionEnum.COUPON_REST_LACK, insufficients);
    }

    @Override
    public List<PossesStatusVO> getCouponDetails(List<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) return Collections.emptyList();
        Example example = new Example(Coupon.class);
        example.createCriteria().andIn("id", ids);
        List<Coupon> coupons = couponMapper.selectByExample(example);
        return coupons.stream().map(BeanMapper::toShortDetail).collect(Collectors.toList());
    }

    @Override
    public PossesStatusVO getSingleDetail(Long id) {
        return Optional.ofNullable(id)
                .map(val -> couponMapper.selectByPrimaryKey(val))
                .map(BeanMapper::toShortDetail).orElse(null);
    }

    @Override
    public void checkPossessUsable(CouponPossess possess) {
        // possess.usableInspect();
    }


    @Override
    public void interCouponUsage(CouponConsumeParam param) {
        PossessDO possessDo = possesService.getByPossessId(param.getPossessId());
        possessDo.usableInspect();

        InterCouponRule rule = (InterCouponRule) Converter.deserialize(possessDo.getCouponRule(), possessDo.getCouponType());
        CouponContributeParam usageParam = new CouponContributeParam(param.getBusinessId(), param.getCoin(), Long.valueOf(rule.getInterDays()), ChronoUnit.DAYS);

        CouponPossess toUpdate = new CouponPossess().setCouponType(possessDo.getCouponType());
        Integer duration = Optional.ofNullable(rule.getInterDays()).orElseGet(param::getDuration);
        BigDecimal discount = new BigDecimal(param.getAmount()).multiply(BigDecimal.valueOf(duration)).multiply(possessDo.getWorth()).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        toUpdate.contribute(usageParam, discount);
        possesService.possesStageContributing(possessDo.getPossessId(), toUpdate);
    }

    @Override
    public void deductCouponUsage(LoanConsumeParam param) {
        PossessDO possessDo = possesService.getByPossessId(param.getPossessId());
        possessDo.usableInspect();

        DeductCouponRule rule = (DeductCouponRule) Converter.deserialize(possessDo.getCouponRule(), possessDo.getCouponType());
        CouponContributeParam usageParam = new CouponContributeParam(param.getBusinessId(), param.getCoin(), Long.valueOf(rule.getDeductDays()), ChronoUnit.DAYS);

        CouponPossess toUpdate = new CouponPossess().setCouponType(possessDo.getCouponType());
        Integer duration = Optional.ofNullable(rule.getDeductDays()).orElseGet(param::getDuration);
        BigDecimal deductRate;
        if (rule.getDeductWay() == 1) {
            deductRate = new BigDecimal(param.getOriginApr()).multiply(possessDo.getWorth());
        }
        else {
            deductRate = new BigDecimal(param.getOriginApr()).min(possessDo.getWorth());
        }
        BigDecimal discount = new BigDecimal(param.getAmount()).multiply(BigDecimal.valueOf(duration)).multiply(deductRate).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        toUpdate.contribute(usageParam, discount);
        possesService.possesStageContributing(possessDo.getPossessId(), toUpdate);
    }


    @Override
    public void dualProfitCouponUsage(DualConsumeParam param) {
        PossessDO possessDo = possesService.getByPossessId(param.getPossessId());
        possessDo.usableInspect();
        ProfitCouponRule rule = (ProfitCouponRule) Converter.deserialize(possessDo.getCouponRule(), possessDo.getCouponType());
        CouponContributeParam usageParam = new CouponContributeParam(param.getBusinessId(), param.getCoin(), Long.valueOf(param.getDuration()), ChronoUnit.DAYS);

        CouponPossess toUpdate = new CouponPossess().setCouponType(possessDo.getCouponType());
        Integer duration = param.getDuration();
        toUpdate.contribute(usageParam, null);
        possesService.possesStageContributing(possessDo.getPossessId(), toUpdate);

    }

    @Override
    public void dualTrialCouponUsage(DualConsumeParam param) {
        PossessDO possessDo = possesService.getByPossessId(param.getPossessId());
        possessDo.usableInspect();
        TrialCouponRule rule = (TrialCouponRule) Converter.deserialize(possessDo.getCouponRule(), possessDo.getCouponType());
        CouponContributeParam usageParam = new CouponContributeParam(param.getBusinessId(), param.getCoin(), Long.valueOf(param.getDuration()), ChronoUnit.DAYS);

        CouponPossess toUpdate = new CouponPossess().setCouponType(possessDo.getCouponType());
        Integer duration = param.getDuration();
        toUpdate.contribute(usageParam, null);
        possesService.possesStageContributing(possessDo.getPossessId(), toUpdate);
    }

    @Override
    public void cashCouponActivate(CouponContributeParam param, PossessDO possessDo) {
        possessDo.usableInspect();
        CouponPossess toUpdate = new CouponPossess().setCouponType(possessDo.getCouponType());
        toUpdate.contribute(param, null);
        possessDo.setExptEndTime(toUpdate.getExptEndTime());
        possesService.possesStageContributing(possessDo.getPossessId(), toUpdate);
    }

    @Override
    public void reddotRead(ReddotReadParam param) {

        CouponPossess toUpdate = new CouponPossess();
        toUpdate.setHasRead(true).setUid(param.getHeaderUid());

        Example example = new Example(CouponPossess.class);
        example.createCriteria()
                .andEqualTo("uid", param.getHeaderUid())
                .andEqualTo("hasRead", false);
        possessMapper.updateByExampleSelective(toUpdate, example);
    }

    @Override
    public List<CashGrantPopupVO> getCashGrantPopup(ExternalHeaderUid param) {
        return redis.getCacheMapValue(RedisKey.POPUP_CASH, param.getHeaderUid());
    }

    @Override
    public boolean cashPopupRead(ExternalHeaderUid param) {
        List<Object> uidKey = Collections.singletonList(param.getHeaderUid());
        redis.deleteCacheMap(RedisKey.POPUP_CASH, uidKey);
        return true;
    }

    @Override
    public TaskProgressRes taskProgress(String uid) {

        List<Long> registCoupon = noviceProperty.getRegistCoupon();
        //三张新资产券id(身份认证、入金、理财)
        long kycId = registCoupon.get(0);
        long amountId = registCoupon.get(1);
        long earnId = registCoupon.get(2);

        Long extraKycId = noviceProperty.getKycCouponExtra();

        List<Long> oldRegistCoupon = noviceProperty.getOldRegistCoupon();
        //三张旧版本资产券id(身份认证、入金、理财)
        long oldKycId = oldRegistCoupon.get(0);
        long oldAmountId = oldRegistCoupon.get(1);
        long oldEarnId = oldRegistCoupon.get(2);

        TaskProgressRes result = new TaskProgressRes(NewUserTaskStatusEnum.NOT_EXITS.getCode(),NewUserTaskStatusEnum.NOT_EXITS.getCode(),NewUserTaskStatusEnum.NOT_EXITS.getCode());
        if (Objects.isNull(uid)){
            return result;
        }

        //查询资产券（如果一张券持有多张，展示最新的那张）
        Example example = new Example(CouponPossess.class);
        example.createCriteria().andEqualTo("uid",uid)
                .andIn("couponId", noviceProperty.getSyncretiCoupons())
                .andGreaterThanOrEqualTo("possessStage",0);
        example.setOrderByClause(" ctime desc ");
        List<CouponPossess> possesses = possessMapper.selectByExample(example);
        Map<Long, CouponPossess> map = possesses.stream().collect(Collectors.toMap(CouponPossess::getCouponId, couponPossess -> couponPossess, (v1, v2) -> v1));

        //1、处理kyc任务
        CouponPossess kycPossess = map.getOrDefault(kycId, map.getOrDefault(extraKycId, map.get(oldKycId)));
        if (!Objects.isNull(kycPossess)){
            Integer kycTaskstatus = getStatusByCouponPossess(kycPossess);
            result.setKycTaskStatus(kycTaskstatus);
            result.setKycTaskExpireTime(TemporalUtil.toEpochMilli(kycPossess.getExprTime()));
        }

        //2、处理入金任务
        CouponPossess amountPossess = map.getOrDefault(amountId, map.get(oldAmountId));
        if (!Objects.isNull(amountPossess)){
            Integer amountTaskstatus = getStatusByCouponPossess(amountPossess);
            result.setAmountTaskStatus(amountTaskstatus);
            result.setAmountTaskExpireTime(TemporalUtil.toEpochMilli(amountPossess.getExprTime()));
        }

        //3、处理理财任务
        CouponPossess earnPossess =map.getOrDefault(earnId, map.get(oldEarnId));
        if (!Objects.isNull(earnPossess)){
            Integer earnTaskstatus = getStatusByCouponPossess(earnPossess);
            result.setEarnTaskStatus(earnTaskstatus);
            result.setEarnTaskExpireTime(TemporalUtil.toEpochMilli(earnPossess.getExprTime()));
        }

        return result;
    }


    @Override
    public List<CouponDetailVO> getCouponsDetail(List<Long> couponIds) {
        List<Coupon> coupons = this.getCoupons(couponIds);
        if (CollectionUtil.isEmpty(coupons)) return Collections.emptyList();

        List<CouponDetailVO> result = new ArrayList<>();
        for (Coupon coupon : coupons) {
            CouponDetailVO vo = new CouponDetailVO();
            BeanUtils.copyProperties(coupon, vo);
            vo.setCouponId(coupon.getId());
            vo.setAvailableNum(coupon.getTotal() - coupon.getPreLock());
            if (coupon.getType().equals(CouponTypeEnum.INTEREST_TYPE.getCode()))
                vo.setInterRuleConfig((InterCouponRule) Converter.deserialize(coupon.getRule(), coupon.getType()));
            else if (coupon.getType().equals(CouponTypeEnum.DEDUCTION_TYPE.getCode()))
                vo.setDeductRuleConfig((DeductCouponRule) Converter.deserialize(coupon.getRule(), coupon.getType()));
            else if (coupon.getType().equals(CouponTypeEnum.CASHRETURN_TYPE.getCode()))
                vo.setCashRuleConfig((CashCouponRule) Converter.deserialize(coupon.getRule(), coupon.getType()));

            result.add(vo);
        }
        return result;
    }

    @Override
    public BitInfoRes bitInfo() {
        BitInfoRes res = new BitInfoRes();
        //获取比特币最新市价
        res.setCoinPrice(getCacheIndexPrice());
        //涨幅计算公式 (最新币价 - 初始币价) / 初始币价
        BigDecimal firstPrice = new BigDecimal("612.51");
        Integer increase = (res.getCoinPrice().subtract(firstPrice)).divide(firstPrice,0,BigDecimal.ROUND_HALF_UP).intValue();
        res.setIncrease(increase);
        return res;
    }

    /**
     * 获取币价接口为非高频调用接口，redis加一层缓存 通过定时任务刷新
     * @return
     */
    private BigDecimal getCacheIndexPrice() {
        Optional<Object> cachePrice = Optional.ofNullable(redis.getCacheObject(RedisKey.BUFFERED_INDEX_PRICE));
        if (cachePrice.isPresent()){
            return new BigDecimal(cachePrice.get().toString());
        }else {
            BigDecimal btcPrice = symbolServiceApi.getIndexPrice("BTC");
            if (!Objects.isNull(btcPrice)){
                redis.setCacheObject(RedisKey.BUFFERED_INDEX_PRICE, btcPrice);
            }
            return btcPrice;
        }
    }

    @Override
    public boolean registAfterActivity(String uid) {
        return false;
    }

    @Override
    public synchronized void couponPreLock(List<Long> couponIds, Long preLock) {
        if (CollectionUtil.isEmpty(couponIds) || preLock == 0) {return;}

        List<Coupon> sources = this.selectExample(() -> {
            Example example = new Example(Coupon.class);
            example.createCriteria().andIn("id", couponIds)
                   .andEqualTo("status", CouponStatusEnum.ENABLED.getStatus());
            return example;
        });
        List<Long> unSufficentCoupons = Functions.filter(sources, val -> val.unSufficient(preLock), Coupon::getId);
        if (CollectionUtil.isNotEmpty(unSufficentCoupons))
            throw new VisibleException(ExceptionEnum.COUPON_PRELOCK_LACK, unSufficentCoupons);

        redis.deleteCacheMap(RedisKey.BUFFERED_COUPONS, Functions.toObjects(couponIds, String::valueOf));
        couponMapper.preLockUpdate(couponIds, preLock.intValue());
    }

    @Override
    public List<Coupon> selectExample(Supplier<Example> supplier) {
        return couponMapper.selectByExample(supplier.get());
    }


    @Override
    public Collection<Long> getBlueCouponIds() {
        Example example = new Example(CouponEvent.class);
        example.createCriteria()
               .andEqualTo("approvalEvent", true)
               .andEqualTo("eventStage", EventStageEnum.ALL_GRANTED.getStage())
               .andEqualTo("type", EventTypeEnum.BWC_APPROVAL.getType());
        example.selectProperties("couponIds");
        List<CouponEvent> couponEvents = eventMapper.selectByExample(example);
        return Functions.union(
                couponEvents,
                events -> {
                    return Optional.ofNullable(events.getCouponIds())
                                   .filter(StringUtils::isNotBlank)
                                   .map(couponIds -> Arrays.stream(couponIds.split(",")).map(Long::parseLong).collect(Collectors.toList()))
                                   .orElseGet(Collections::emptyList);
                }
        );
    }


    @Override
    public PageResult<FullScaleCouponVO> typedPaging(TypedParam param) {
        Example example = new Example(Coupon.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", CouponStatusEnum.ENABLED.getStatus());
        if (StringUtils.contains(param.getTypes(), ","))
            criteria.andIn("type", ArrayUtil.toList(param.getTypes(), ",", Integer::parseInt));
        else
            criteria.andEqualTo("type", Integer.parseInt(param.getTypes()));

        PageResult<Coupon> pagingCoupons = PageContext.selectPage(
                () -> couponMapper.selectByExample(example), param.getPage(), param.getPage_size(), "CTIME DESC");
        PageResult<FullScaleCouponVO> fullScalePage = PageResult.fromAnother(pagingCoupons, BeanMapper::toCouponListVo);

        Example query = new Example(CouponPossess.class);
        query.createCriteria()
             .andEqualTo("uid", ContextHolder.get().getXGwUser())
             .andIn("couponId", Functions.toList(pagingCoupons.getItems(), Coupon::getId));
        List<CouponPossess> pagingPosses = possessMapper.selectByExample(query);
        Set<Long> recved = Functions.toSet(pagingPosses, CouponPossess::getCouponId);
        return PageResult.fromAnother(
                fullScalePage,
                Predicator.existIn(FullScaleCouponVO::getCouponId, recved),
                fullScale -> fullScale.setReceived(true),
                Function.identity()
        );
    }

    @Override
    public PageResult<FullScaleCouponVO> getDualTrial() {
        Example example = new Example(Coupon.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", Long.parseLong(dualTrialId));
        Coupon coupon = couponMapper.selectOneByExample(example);
        FullScaleCouponVO scaleCouponVo = BeanMapper.toCouponListVo(coupon);

        Example query = new Example(CouponPossess.class);
        query.createCriteria()
             .andEqualTo("uid", ContextHolder.get().getXGwUser())
             .andEqualTo("couponId", Long.parseLong(dualTrialId));
        CouponPossess possess = possessMapper.selectOneByExample(query);
        if (Objects.nonNull(possess)) {
            scaleCouponVo.setReceived(true);
            scaleCouponVo.setExprAtBegin(TemporalUtil.toEpochMilli(possess.getCtime()));
            scaleCouponVo.setExprAtEnd(TemporalUtil.toEpochMilli(possess.getExprTime()));
        }

        return PageResult.oneItemPage(scaleCouponVo);
    }

    @Override
    public void typePermit(Long couponId, Integer type) {
        Example example = new Example(Coupon.class);
        example.createCriteria()
               .andEqualTo("id", couponId)
               .andEqualTo("type", type);
        int i = couponMapper.selectCountByExample(example);
        if (i < 1)
            throw new BusinessException(ExceptionEnum.IMPROPER_COUPON);
    }

    /**
     * 根据券详情返回任务状态
     * @param couponPossess
     * @return
     */
    private Integer getStatusByCouponPossess(CouponPossess couponPossess) {
        //0待使用1已过期2待发放3已禁用4发放失败6已发放
        Integer businessStage = couponPossess.getBusinessStage();
        if (businessStage.equals(0)){
            //任务为待解锁
            return NewUserTaskStatusEnum.STAY_LOCK.getCode();
        }
        if (businessStage.equals(2)){
            //任务为进行中
            return NewUserTaskStatusEnum.UNDER_WAY.getCode();
        }
        if (businessStage.equals(6)){
            //任务为已完成
            return NewUserTaskStatusEnum.COMPLETED.getCode();
        }
        //如果是其他状态，返回已失效
        return NewUserTaskStatusEnum.INVALID.getCode();
    }

}
