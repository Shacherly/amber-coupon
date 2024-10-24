package com.trading.backend.http.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author ~~ trading.s
 * @date 17:07 09/23/21
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponConsumeParam extends InternalParamUid implements Serializable {
    private static final long serialVersionUID = -519289097353431210L;

    @NotNull
    @ApiModelProperty(value = "持券id", required = true)
    private Long possessId;

    @NotBlank
    @ApiModelProperty(value = "用券业务id（理财加息/借贷减息 都传订单id）", required = true)
    private String businessId;

    @NotBlank
    @ApiModelProperty(value = "用券关联的订单币种", required = true)
    private String coin;

    @NotBlank
    @ApiModelProperty(value = "用券关联的订单本金", required = true)
    private String amount;

    @NotNull
    @ApiModelProperty(value = "双币天数", required = true)
    private Integer duration;

}
