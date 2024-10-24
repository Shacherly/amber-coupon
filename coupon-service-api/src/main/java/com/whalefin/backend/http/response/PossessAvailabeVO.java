package com.trading.backend.http.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.domain.base.CouponCoinRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PossessAvailabeVO extends BasalExportPossessBO {
    private static final long serialVersionUID = 2303520509223373277L;

    @ApiModelProperty(value = "券的使用规则")
    private CouponCoinRule couponRule;

}
