package com.trading.backend.config;


import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author ~~ trading.s
 * @date 16:06 10/15/21
 */
@Slf4j
public class DisposableUniqueScheduler {

    private final ThreadPoolTaskScheduler scheduler;

    private final BlockingQueue<String> TASK_QUEUE = new LinkedBlockingQueue<>(ExecutorConfigurer.MAX_POOL_SIZE);
    private final ConcurrentSkipListSet<String> RUNNABLES = new ConcurrentSkipListSet<>();
    private final ConcurrentHashMap<String, Integer> DELAY_MAP = new ConcurrentHashMap<>(ExecutorConfigurer.MAX_POOL_SIZE);

    // private final Set<String> recycleTasks = new ConcurrentSkipListSet<>();

    public DisposableUniqueScheduler(ThreadPoolTaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void schedule(Runnable runnable, String taskId, Instant atfuture, boolean disposable) {
        if (RUNNABLES.add(taskId) || !disposable) {
            log.info("ScheduledDisposableTask_id={}, disposable={}, ExpectedInstant={}, RUNNABLES_SIZE={}, RUNNABLES={}",
                    taskId, disposable, atfuture.atZone(GlobalSystemProperty.DEAFAULT_ZONE_ID), RUNNABLES.size(), RUNNABLES);
            Instant now = Instant.now();
            scheduler.schedule(() -> {
                try {
                    TASK_QUEUE.put(taskId);
                    runnable.run();
                    if (disposable)
                        combineTake(taskId);
                } catch (Exception exp) {
                    log.error("DisposableUniqueSchedulerError {}", exp.getMessage(), exp);
                    if (!(exp instanceof BusinessException)) {
                        throw new BusinessException(exp, ExceptionEnum.SCHEDULE_ERROR);
                    }
                    compensate(exp, runnable, taskId);
                }
            }, atfuture.isBefore(now) ? now : atfuture);
        }
    }

    public void remove(String taskId) {
        combineTake(taskId);
    }

    private void compensate(Exception exp, Runnable task, String taskId) {
        // boolean isBussExp = exp instanceof BusinessException;
        // if (!isBussExp) {
        //     log.error(exp.getMessage(), exp);
        //     throw new BusinessException(ExceptionEnum.SCHEDULE_ERROR, exp.getMessage());
        // }
        //
        // log.error(exp.getMessage(), exp);
        if (ExceptionEnum.CASHRETURN_GRANT_CELLING == ((BusinessException) exp).getExceptionEnum()) {
            setDelayMap(taskId);
            Optional.ofNullable(delayMapGet(taskId))
                    .ifPresent(delay -> {
                        ZonedDateTime defaultZoneNow = TemporalUtil.defaultZoneNowTime();
                        Instant instant = TemporalUtil.offSetDaysStart(defaultZoneNow, 1).toInstant().plusSeconds(delay);
                        log.info("CompensateTask_id = {}", taskId);
                        /**
                         * Delay task is supposed to execute repetitively
                         */
                        schedule(task, taskId, instant, false);
                    });
            return;
        }
        log.error("CompensateError, non CASHRETURN_GRANT_CELLING, error reason {}", exp.getMessage(), exp);
    }

    private void combineTake(String uniqueId) {
        try {
            TASK_QUEUE.take();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            RUNNABLES.remove(uniqueId);
        }
    }

    private synchronized void setDelayMap(String taskId) {
        int size = DELAY_MAP.size();
        DELAY_MAP.putIfAbsent(taskId, size);
    }

    private synchronized Integer delayMapGet(String taskId) {
        return DELAY_MAP.remove(taskId);
    }
}
