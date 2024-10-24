package com.trading.backend.http.response.loan;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.domain.vo.DeductCouponRuleVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanPossessVO extends BasalExportPossessBO {

    private static final long serialVersionUID = 2808170311045323016L;

    @ApiModelProperty(value = "减息券规则配置")
    private DeductCouponRuleVO couponRule;

}
