package com.trading.backend.domain;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.util.NumberCriteria;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ExportableCoinRule implements Serializable {
    private static final long serialVersionUID = -1552520400810756984L;

    @ApiModelProperty(value = "适用币种")
    @JSONField(name = "apply_coin", ordinal = 1)
    private String applyCoin;

    @ApiModelProperty(value = "最低需求金额")
    @JSONField(name = "min_amount", ordinal = 2)
    private String minAmount;

    @ApiModelProperty(value = "最高需求金额")
    @JSONField(name = "max_amount", ordinal = 3)
    private String maxAmount;

    public ExportableCoinRule setMinAmount(BigDecimal minAmount) {
        this.minAmount = Optional.ofNullable(minAmount).map(NumberCriteria::stripTrailing).orElse(null);
        return this;
    }

    public ExportableCoinRule setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = Optional.ofNullable(maxAmount).map(NumberCriteria::stripTrailing).orElse(null);
        return this;
    }

    public ExportableCoinRule setApplyCoin(String applyCoin) {
        this.applyCoin = Optional.ofNullable(applyCoin).map(String::toUpperCase).orElse(null);
        return this;
    }
}
