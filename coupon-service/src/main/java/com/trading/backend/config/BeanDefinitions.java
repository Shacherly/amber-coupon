package com.trading.backend.config;


import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author ~~ trading.s
 * @date 15:08 10/20/21
 */
@Configuration
public class BeanDefinitions {

    // @Bean
    // public FastJsonConfig fastJsonConfig() {
    //     FastJsonConfig fastJsonConfig = new FastJsonConfig();
    // }

    @Bean
    public LoadingCache<String, AtomicInteger> idempotentAccessCache() {
        LoadingCache<String, AtomicInteger> cache =
                CacheBuilder.newBuilder()
                            .expireAfterAccess(4, TimeUnit.SECONDS)
                            .expireAfterWrite(4, TimeUnit.SECONDS)
                            .build(new CacheLoader<String, AtomicInteger>() {
                                @Override
                                public AtomicInteger load(String key) throws Exception {
                                    return new AtomicInteger(1);
                                }
                            });
        return cache;
    }

    @Bean
    public LoadingCache<String, String> serviceWarnCache() {
        return CacheBuilder.newBuilder()
                           .expireAfterAccess(Duration.ofSeconds(1))
                           .build(new CacheLoader<String, String>() {
                               @Override
                               public String load(String key) throws Exception {
                                   return "";
                               }
                           });
    }

    @Bean
    public LoadingCache<String, Set<String>> coinsCache() {
        return CacheBuilder.newBuilder()
                           // .expireAfterAccess(Duration.ofSeconds(1))
                           .build(new CacheLoader<String, Set<String>>() {
                               @Override
                               public Set<String> load(String key) throws Exception {
                                   return Collections.emptySet();
                               }
                           });
    }
}
