package com.trading.backend.http.response.dual;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.domain.vo.ProfitCouponRuleVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DualPossessVO extends BasalExportPossessBO {

    private static final long serialVersionUID = 2808170311045323016L;

    @ApiModelProperty(value = "收益增强券规则配置")
    private ProfitCouponRuleVO couponRule;

}
