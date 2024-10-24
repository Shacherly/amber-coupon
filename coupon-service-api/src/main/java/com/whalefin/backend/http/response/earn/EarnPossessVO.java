package com.trading.backend.http.response.earn;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.domain.vo.InterCouponRuleVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EarnPossessVO extends BasalExportPossessBO {

    private static final long serialVersionUID = -794845830556222761L;

    @ApiModelProperty(value = "加息券规则配置")
    private InterCouponRuleVO couponRule;
}
