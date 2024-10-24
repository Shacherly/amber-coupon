package com.trading.backend.http.request.dual;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.http.request.InternalParamUid;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author ~~ tradingmu
 * @date 16:05 02/28/22
 */
@Setter @Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DualConsumeParam extends InternalParamUid implements Serializable {
    private static final long serialVersionUID = -2778128901847986557L;

    @NotNull
    @ApiModelProperty(value = "持券id", required = true)
    private Long possessId;

    @NotBlank
    @ApiModelProperty(value = "用券业务id（理财加息/借贷减息/收益增强 都传订单id）", required = true)
    private String businessId;

    @NotBlank
    @ApiModelProperty(value = "用券关联的订单币种", required = true)
    private String coin;

    @NotBlank
    @ApiModelProperty(value = "用券关联的订单本金", required = true)
    private String amount;

    @NotNull
    @ApiModelProperty(value = "申购天数", required = true)
    private Integer duration;

}
