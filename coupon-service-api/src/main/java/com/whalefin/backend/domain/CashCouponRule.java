package com.trading.backend.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.base.CouponRuleBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CashCouponRule extends CouponRuleBase {

    private static final long serialVersionUID = -5137884553701952979L;

    @ApiModelProperty(value = "最低申购天数(对于申购激活的券)")
    @JSONField(name = "min_subscr_days", ordinal = 1)
    private Integer minSubscrDays;

    public boolean depositEligible(String coin, BigDecimal amount) {
        for (CommonCoinRule rule : getCoinRules()) {
            if (amount.compareTo(rule.getMinAmount()) >= 0
                    && StringUtils.equalsIgnoreCase(rule.getApplyCoin(), coin)) {
                return true;
            }
        }
        return false;
    }

    public boolean subscribeEligible(String coin, BigDecimal amount, Integer period) {
        if (period < minSubscrDays) return false;
        return depositEligible(coin, amount);
    }
}
