package com.trading.backend.http.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * @author ~~ trading.s
 * @date 17:20 10/15/21
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "查询资产券是否已充值进钱包返回结果")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CashGrantPopupVO implements Serializable {
    private static final long serialVersionUID = -1742068099075275888L;

    @ApiModelProperty(value = "到账的金额")
    private String amout;

    @ApiModelProperty(value = "到账币种大写")
    private String coin;


    public static List<CashGrantPopupVO> mock() {
        CashGrantPopupVO response1 = new CashGrantPopupVO().setAmout("12.3").setCoin("USD");
        CashGrantPopupVO response2 = new CashGrantPopupVO().setAmout("15.1").setCoin("USDT");
        return Arrays.asList(response1, response2);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CashGrantPopupVO{");
        sb.append("amout='").append(amout).append('\'');
        sb.append(", coin='").append(coin).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
