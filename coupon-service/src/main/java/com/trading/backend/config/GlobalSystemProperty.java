package com.trading.backend.config;


import cn.hutool.core.collection.CollectionUtil;
import com.google.common.cache.LoadingCache;
import com.trading.backend.client.ICoinServiceApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;


/**
 * @author ~~ trading.s
 * @date 16:05 11/05/21
 */
@Slf4j
@Configuration
public class GlobalSystemProperty {

    @Autowired
    private LoadingCache<String, Set<String>> coinsCache;
    @Autowired
    private ICoinServiceApi coinServiceApi;

    public static final ZoneId DEAFAULT_ZONE_ID = ZoneId.of("UTC+8");

    public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.ofHours(8);

    public static final ThreadLocal<DateTimeFormatter> FORMATTER_1 = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    public DateTimeFormatter getDateFormat() {
        return FORMATTER_1.get();
    }

    public ZoneId getZoneId() {
        return DEAFAULT_ZONE_ID;
    }

    public ZoneOffset getZoneOffset() {
        return DEFAULT_ZONE_OFFSET;
    }

    public Set<String> getSupportCoins() {
        try {
            Set<String> coins = coinsCache.get("SUPPORTED_COINS");
            if (CollectionUtil.isNotEmpty(coins)) return coins;
            return coinServiceApi.cacheSupportCoins();
        } catch (ExecutionException e) {
            log.info(e.getMessage(), e);
        }
        return coinServiceApi.cacheSupportCoins();
    }

    public boolean support(String coin) {
        String input = Optional.ofNullable(coin).map(String::toUpperCase).orElse(null);
        Set<String> coins = getSupportCoins();
        return coins.contains(input);
    }
}
