package com.trading.backend.task;


import cn.hutool.core.collection.CollectionUtil;
import com.trading.backend.annotation.Traceable;
import com.trading.backend.client.IEarnServiceApi;
import com.trading.backend.bo.WorthGrantBO;
import com.trading.backend.bo.WorthToGrantBO;
import com.trading.backend.client.IUserServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.config.DisposableUniqueScheduler;
import com.trading.backend.config.CouponTaskProperty;
import com.trading.backend.config.NoviceProperty;
import com.trading.backend.constant.Constant;
import com.trading.backend.constant.RedisKey;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.service.ICashCouponService;
import com.trading.backend.service.INoviceService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.service.IWorthGrantService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;


/**
 * @author ~~ trading.s
 * @date 12:55 10/15/21
 */
@Slf4j
@Component
public class CouponWorthGrantTask {


    @Autowired
    private IEarnServiceApi earnService;
    @Autowired
    private IPossesService possesService;
    @Autowired
    private IWorthGrantService grantService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private DisposableUniqueScheduler scheduler;
    @Autowired
    private CouponTaskProperty couponTaskProperty;
    @Autowired
    private ICashCouponService iCashCouponService;
    @Autowired
    private INoviceService noviceService;
    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private IEarnServiceApi earnServiceApi;
    @Autowired
    private IUserServiceApi userServiceApi;
    @Autowired
    private NoviceProperty noviceProperty;

    private final AtomicInteger COUNTER1 = new AtomicInteger(-1);
    private final AtomicInteger COUNTER2 = new AtomicInteger(-1);

    /**
     * 定时获取即将发放奖励的possess 放入Redis
     * 间隔 10min
     */
    @XxlJob("putToGrants") @Traceable
    // @Scheduled(fixedRateString = "${coupon-task-property.grants-scan}" /*zone = "Asia/Shanghai"*/)
    public void putToGrants() {
        log.info("putToGrants");

        List<WorthToGrantBO> grants = grantService.scanWorthGrants();
        Set<ZSetOperations.TypedTuple<Object>> collect = grants.stream().map(this::mapper).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(collect))
            redisService.setSortedSet(RedisKey.TO_GRANT_CASH_POOL, collect);
    }


    /**
     * 间隔1min
     */
    @XxlJob("takeToGrants") @Traceable
    // @Scheduled(fixedDelayString = "${coupon-task-property.grants-take}")
    public void takeToGrants() {
        log.info("takeToGrants");

        long nowMilli = Instant.now().toEpochMilli();
        double max = (double) nowMilli + couponTaskProperty.getGrantOffset();
        Set<ZSetOperations.TypedTuple<Object>> tupleSet = redisService.getSortedSet(RedisKey.TO_GRANT_CASH_POOL, 0, max);
        if (CollectionUtil.isEmpty(tupleSet)) return;
        log.info("XXLJOBGet_Cached_Grants_Size={}", tupleSet.size());

        for (ZSetOperations.TypedTuple<Object> tuple : tupleSet) {
            Double score = tuple.getScore();
            String value = String.valueOf(tuple.getValue());
            if (score == null || value == null) continue;
            Instant instant = Instant.ofEpochMilli(score.longValue());
            String[] split = value.split(":");
            long possessId = Long.parseLong(split[0]);
            // if (possessId != 64298) continue;
            String uid = split[1];

            PossessDO possessDo = possesService.getByPossessId(possessId);
            Optional.ofNullable(possessDo)
                    .ifPresent(possDo -> {
                        Long couponId = possDo.getCouponId();
                        WorthGrantBO grantBo = new WorthGrantBO(
                                uid, possDo.getWorthCoin(), possDo.getWorth().toPlainString(),
                                CouponApplySceneEnum.getByCode(possessDo.getApplyScene()), possessId, couponId);

                        Runnable task = () -> {
                            MDC.put(Constant.TRACE_ID, UUID.randomUUID().toString());
                            log.info("Execute_GrantTask_ForTaskId={}, expected_time={}", possDo.getPossessId(), possDo.getExptEndTime());
                            // 发放前的KYC检查，51券kyc 未pass需要恢复初始状态，52、53处于pending
                            grantService.llegalInspect(grantBo, taskId -> scheduler.remove(taskId));
                            // 证明是新手任务的奖励任务 ->> 需要检查每日上限
                            if (noviceProperty.getSyncretiCoupons().contains(couponId)) {
                                grantService.dailyCellingInspect(grantBo);
                            }
                            grantService.doCashGrant(grantBo, possessDo);
                            iCashCouponService.accomplish(grantBo);
                            redisService.removeZSetByValue(RedisKey.TO_GRANT_CASH_POOL, value);
                            userServiceApi.kafkaNotify(
                                    grantBo.getUid(),
                                    Collections.singletonList("MSG07_000016"),
                                    null, Collections.singletonList(3),
                                    null
                            );
                            noviceService.updateNoviceProgress(uid);
                            MDC.remove(Constant.TRACE_ID);
                        };
                        scheduler.schedule(task, String.valueOf(possessId), instant, true);
                    });
        }
    }


    private ZSetOperations.TypedTuple<Object> mapper(WorthToGrantBO bo) {
        StringJoiner joiner = new StringJoiner(":")
                .add(String.valueOf(bo.getPossessId())).add(bo.getUid());
        ZSetOperations.TypedTuple<Object> tups = new DefaultTypedTuple<>(joiner.toString(), (double) bo.getExptEndTime());
        return tups;
    }

    private BiConsumer<String, LocalDateTime> postback() {
        return (value, score) -> {
            ZSetOperations.TypedTuple<Object> tups = new DefaultTypedTuple<>(value, TemporalUtil.toEpochMilli(score).doubleValue());
            redisService.setSortedSet(RedisKey.TO_GRANT_CASH_POOL, tups);
        };
    }
}
