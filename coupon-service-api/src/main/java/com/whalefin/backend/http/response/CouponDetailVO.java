package com.trading.backend.http.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * @author ~~ trading.s
 * @date 15:21 09/23/21
 */
@Getter
@Setter
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponDetailVO extends CouponVO {

    private static final long serialVersionUID = -359485162315064877L;

    @ApiModelProperty(value = "加息券规则配置")
    private InterCouponRule interRuleConfig;

    @ApiModelProperty(value = "资产券规则配置")
    private CashCouponRule cashRuleConfig;

    @ApiModelProperty(value = "减息券规则配置")
    private DeductCouponRule deductRuleConfig;

}
