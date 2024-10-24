package com.trading.backend.config;


import com.google.common.collect.Sets;
import com.trading.backend.common.util.Functions;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;


/**
 * @author ~~ trading.s
 * @date 10:48 11/09/21
 */
@Getter @Setter
public final class tradingCoins {


    static final private Set<String> USDS = Sets.newHashSet("USDC", "PAX", "TUSD", "HUSD", "BUSD");

    static {
        Set<String> collect = Functions.toSet(new ArrayList<>(USDS), String::toLowerCase);
        USDS.addAll(collect);
    }

    public static Set<String> getUSD() {
        return Collections.unmodifiableSet(USDS);
    }

    public static String getCoin(String coin) {
        Objects.requireNonNull(coin);
        return USDS.contains(coin) ? "USD" : coin.toUpperCase();
    }
}
