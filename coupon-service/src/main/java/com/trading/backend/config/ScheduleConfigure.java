package com.trading.backend.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ~~ trading.s
 * @date 13:43 10/01/21
 */
@Slf4j
@Configuration
public class ScheduleConfigure implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setTaskScheduler(taskScheduler());
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(4);
        taskScheduler.setErrorHandler(e -> log.error("Scheduled Task Error cause by [{}]", e.getMessage(), e));
        taskScheduler.setThreadNamePrefix("Scheduled-Task");
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return taskScheduler;
    }

    /**
     * 资产券奖励充值task
     * @param scheduler
     * @return
     */
    @Bean
    public DisposableUniqueScheduler grantScheduler(ThreadPoolTaskScheduler scheduler) {
        return new DisposableUniqueScheduler(scheduler);
    }
}
