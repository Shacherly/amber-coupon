package com.trading.backend.domain.base;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.CommonCoinRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class CouponRuleBase implements Serializable {

    private static final long serialVersionUID = -3131531604050803308L;

    @ApiModelProperty(value = "使用规则配置")
    @JSONField(name = "coin_rules", ordinal = 6)
    private List<CommonCoinRule> coinRules;

    public Set<String> applyCoins() {
        if (coinRules == null) return new HashSet<>();
        return coinRules.stream().map(CommonCoinRule::getApplyCoin).collect(Collectors.toSet());
    }

    public boolean coinUsable(String coin) {
        return applyCoins().contains(coin.toLowerCase());
    }

    public boolean amountUsable(BigDecimal amount) {
        if (coinRules == null) return false;
        for (CommonCoinRule rule : coinRules) {
            if (amount.compareTo(rule.getMinAmount()) >= 0
                    && amount.compareTo(rule.getMaxAmount()) <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean anyCashRuleEligible(String coin, BigDecimal amount) {
        for (CommonCoinRule rule : getCoinRules()) {
            if (amount.compareTo(rule.getMinAmount()) >= 0
                    && StringUtils.equalsIgnoreCase(rule.getApplyCoin(), coin)) {
                return true;
            }
        }
        return false;
    }
}
