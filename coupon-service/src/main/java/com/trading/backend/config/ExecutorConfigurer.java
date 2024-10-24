package com.trading.backend.config;


import cn.hutool.core.collection.CollectionUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.trading.backend.common.util.Functions;
import com.trading.backend.constant.Constant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ~~ trading.s
 * @date 13:43 10/01/21
 */
@Slf4j
@Getter
@Configuration
public class ExecutorConfigurer {

    public static final int CORE_POOL_SIZE = 16;
    public static final int MAX_POOL_SIZE = 1024;

    private ExecutorService executor = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            3,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("Global-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public void execute(Runnable runnable, boolean traceable) {
        String trace = MDC.get(Constant.TRACE_ID);
        executor.execute(() -> {
            try {
                if (traceable)
                    MDC.put(Constant.TRACE_ID, trace);
                runnable.run();
            } catch (Exception e) {
                log.error("Global pool exception, cause {}", e.getMessage(), e);
                throw e;
            } finally {
                MDC.clear();
            }
        });
    }

    public <E> void submit(List<E> sources, Consumer<List<E>> function) {
        if (CollectionUtil.isEmpty(sources) || Objects.isNull(function)) return;
        int pageSize = 2000;
        long count = sources.size();
        AtomicInteger counter = new AtomicInteger();
        List<Future<Boolean>> futures = new ArrayList<>(4000);
        Instant start = Instant.now();
        for (long limit = 0L; limit * pageSize < count; limit++) {
            Future<Boolean> submit = executor.submit(() -> {
                int page = counter.getAndIncrement();
                log.debug("Parallel task index = {}", page);
                List<E> partition = CollectionUtil.page(page, pageSize, sources);
                try {
                    function.accept(partition);
                    return true;
                } catch (Exception e) {
                    log.error("Parallel task error = {}", e.getMessage(), e);
                    return false;
                }
            });
            futures.add(submit);
        }
        boolean future = true;
        try {
            for (Future<Boolean> part : futures) {
                future = future && part.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        Instant end = Instant.now();
        long spend = Duration.between(start, end).toMillis();
        log.info("ParallelSubmit spend = {} ms !", spend);
    }

    public <T, E> void submit(long count, BiFunction<Integer, Integer, List<T>> biConsumer, Function<T, E> mapping, Consumer<List<E>> function) {
        if (count == 0 || Objects.isNull(biConsumer)
                || Objects.isNull(mapping) || Objects.isNull(function)) return;
        int pageSize = 2000;
        // long count = sources.size();
        AtomicInteger counter = new AtomicInteger();
        List<Future<Boolean>> futures = new ArrayList<>(4000);
        Instant start = Instant.now();
        for (long limit = 0L; limit * pageSize < count; limit++) {
            Future<Boolean> submit = executor.submit(() -> {
                int page = counter.addAndGet(1);
                log.debug("Parallel task index = {}", page);
                List<E> partition = Functions.toList(biConsumer.apply(page, pageSize), mapping);
                        // CollectionUtil.page(page, pageSize, sources);
                try {
                    function.accept(partition);
                    return true;
                } catch (Exception e) {
                    log.error("Parallel task error = {}", e.getMessage(), e);
                    return false;
                }
            });
            futures.add(submit);
        }
        boolean future = true;
        try {
            for (Future<Boolean> part : futures) {
                future = future && part.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        Instant end = Instant.now();
        long spend = Duration.between(start, end).toMillis();
        log.info("ParallelSubmit spend = {} ms !", spend);
    }

    public <T, E> void serial(long count, BiFunction<Integer, Integer, List<T>> producer, Function<T, E> exchanger, Function<List<E>, Integer> consumer) {
        if (count == 0 || Objects.isNull(producer)
                || Objects.isNull(exchanger) || Objects.isNull(consumer)) return;
        int pageSize = 2000;
        AtomicInteger counter = new AtomicInteger();
        Instant start = Instant.now();
        for (long limit = 0L; limit * pageSize < count; limit++) {
            int page = counter.addAndGet(1);
            List<E> partition = Functions.toList(producer.apply(page, pageSize), exchanger);
            Integer rows = consumer.apply(partition);
            log.info("SerialSubmit page = {}, size = {}, total = {}, Effected rows = {}", page, partition.size(), String.valueOf(count), rows);
        }
        Instant end = Instant.now();
        long spend = Duration.between(start, end).toMillis();
        log.info("SerialSubmit spend = {} ms !", spend);
    }

    public <T, E> void serial(long count, int factor, BiFunction<Integer, Integer, List<T>> producer, Function<T, E> exchanger, Function<List<E>, Integer> consumer) {
        if (count == 0 || Objects.isNull(producer)
                || Objects.isNull(exchanger) || Objects.isNull(consumer)) return;
        int pageSize = 2000;
        int adaptorSize = pageSize / factor;
        AtomicInteger counter = new AtomicInteger();
        Instant start = Instant.now();
        for (long limit = 0L; limit * adaptorSize < count; limit++) {
            int page = counter.addAndGet(1);
            List<E> partition = Functions.toList(producer.apply(page, adaptorSize), exchanger);
            Integer rows = consumer.apply(partition);
            log.info("SerialSubmit page = {}, size = {}, total = {}, Effected rows = {}", page, partition.size(), String.valueOf(count), rows);
        }
        Instant end = Instant.now();
        long spend = Duration.between(start, end).toMillis();
        log.info("SerialSubmit spend = {} ms !", spend);
    }
}
