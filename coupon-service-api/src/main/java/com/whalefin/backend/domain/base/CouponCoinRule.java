package com.trading.backend.domain.base;

import com.trading.backend.domain.ExportableCoinRule;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface CouponCoinRule {

    List<ExportableCoinRule> getCoinRules();

    default Set<String> applyCoins() {
        if (getCoinRules() == null) return new HashSet<>();
        return getCoinRules().stream().map(ExportableCoinRule::getApplyCoin).map(String::toUpperCase).collect(Collectors.toSet());
    }

    default boolean coinUsable(String coin) {
        return applyCoins().contains(coin.toUpperCase());
    }

    default boolean amountUsable(BigDecimal amount) {
        if (getCoinRules() == null) return false;
        for (ExportableCoinRule rule : getCoinRules()) {
            if (amount.compareTo(new BigDecimal(rule.getMinAmount())) >= 0
                    && amount.compareTo(new BigDecimal(rule.getMaxAmount())) <= 0) {
                return true;
            }
        }
        return false;
    }

}
