package com.trading.backend.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.base.CouponRuleBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author ~~ trading.s
 * @date 11:09 09/23/21
 */
@Getter
@Setter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class InterCouponRule extends CouponRuleBase {

    private static final long serialVersionUID = -5651851567044245098L;

    @ApiModelProperty(value = "加息天数")
    @JSONField(name = "inter_days", ordinal = 1)
    private Integer interDays;

    @ApiModelProperty(value = "最低申购天数")
    @JSONField(name = "min_subscr_days", ordinal = 2)
    private Integer minSubscrDays;

    @ApiModelProperty(value = "最高申购天数")
    @JSONField(name = "max_subscr_days", ordinal = 3)
    private Integer maxSubscrDays;

    public boolean periodUsable(Integer earnPeriod) {
        return earnPeriod >= minSubscrDays && earnPeriod <= maxSubscrDays;
    }
}
