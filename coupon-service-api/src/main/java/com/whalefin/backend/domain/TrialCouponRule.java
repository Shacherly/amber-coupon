package com.trading.backend.domain;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.base.CouponRuleBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter @Setter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TrialCouponRule extends CouponRuleBase {
    private static final long serialVersionUID = 2105180634641785835L;


    @ApiModelProperty(value = "最低投资天数")
    @JSONField(name = "min_period", ordinal = 1)
    private Integer minPeriod;

    @ApiModelProperty(value = "天数")
    @JSONField(name = "max_period", ordinal = 2)
    private Integer maxPeriod;

    @ApiModelProperty(value = "适用币对，BTC_USDC")
    @JSONField(name = "apply_pair", ordinal = 3)
    private List<String> applyPair;
}
