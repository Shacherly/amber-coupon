package com.trading.backend.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CommonCoinRule implements Serializable {

    private static final long serialVersionUID = 1816421116481724768L;

    @ApiModelProperty(value = "适用币种")
    @JSONField(name = "apply_coin", ordinal = 1)
    private String applyCoin;

    @ApiModelProperty(value = "最低需求金额")
    @JSONField(name = "min_amount", ordinal = 2)
    private BigDecimal minAmount;

    @ApiModelProperty(value = "最高需求金额")
    @JSONField(name = "max_amount", ordinal = 3)
    private BigDecimal maxAmount;


    /**
     * 入库的时候设置scale，查询到JSONObject中时才是BigDecimal类型的
     * @param minAmount
     * @return
     */
    public CommonCoinRule setMinAmount(BigDecimal minAmount) {
        if (minAmount != null)
            this.minAmount = minAmount.setScale(16, RoundingMode.HALF_UP);
        return this;
    }

    public CommonCoinRule setMinAmount(String minAmount) {
        if (minAmount != null)
            this.minAmount = new BigDecimal(minAmount).setScale(16, RoundingMode.HALF_UP);
        return this;
    }

    public CommonCoinRule setMaxAmount(BigDecimal maxAmount) {
        if (maxAmount != null)
            this.maxAmount = maxAmount.setScale(16, RoundingMode.HALF_UP);
        return this;
    }

    public CommonCoinRule setMaxAmount(String maxAmount) {
        if (maxAmount != null)
            this.maxAmount = new BigDecimal(maxAmount).setScale(16, RoundingMode.HALF_UP);
        return this;
    }

    public CommonCoinRule setApplyCoin(String applyCoin) {
        if (applyCoin != null)
            this.applyCoin = applyCoin.toLowerCase();
        return this;
    }
}
