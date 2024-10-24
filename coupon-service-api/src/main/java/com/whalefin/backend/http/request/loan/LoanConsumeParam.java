package com.trading.backend.http.request.loan;


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
 * @date 10:36 10/25/21
 */
@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanConsumeParam implements Serializable {
    private static final long serialVersionUID = -6808719758725071645L;

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

    @ApiModelProperty(value = "减息借贷天数")
    private Integer duration;

    @NotBlank
    @ApiModelProperty(value = "借贷使用减息券传递originApr", required = true)
    private String originApr;
}
