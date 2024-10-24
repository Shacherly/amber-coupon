package com.trading.backend.http.request.earn;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.http.request.InternalParamUid;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * @author ~~ trading.s
 * @date 16:46 09/23/21
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EarnMatchParam extends InternalParamUid {

    private static final long serialVersionUID = 9123373707324874806L;
    // @ApiModelProperty(value = "券类型", hidden = true)
    // private Integer coupon_type;

    @ApiModelProperty(value = "申购币种")
    private String coin;

    @ApiModelProperty(value = "申购金额")
    private String subscrAmount;

    @ApiModelProperty(value = "申购天数 -> [minDays, maxDays]")
    private Integer earnPeriod;

    // @ApiModelProperty(value = "是否匹配申购条件")
    // private boolean matched;


    @Override
    public String toString() {
        return "EarnMatchParam{" +
                "coin='" + coin + '\'' +
                ", subscrAmount='" + subscrAmount + '\'' +
                ", earnPeriod=" + earnPeriod +
                "} " + super.toString();
    }
}
