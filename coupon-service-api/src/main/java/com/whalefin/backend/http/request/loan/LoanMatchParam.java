package com.trading.backend.http.request.loan;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.http.request.InternalParamUid;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @author ~~ trading.s
 * @date 13:12 09/26/21
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class LoanMatchParam extends InternalParamUid {

    private static final long serialVersionUID = 3642140782011273476L;

    @ApiModelProperty(value = "适用币种")
    private String coin;

    @ApiModelProperty(value = "借贷期限")
    private Integer loanPeriod;

    @ApiModelProperty(value = "借贷金额")
    private String loanAmount;

    @ApiModelProperty(value = "借贷类型1所有类型 2活期资产抵押 3定期资产抵押，支持传入数组多条件查询")
    private List<Integer> loanTypes;

    @Override
    public String toString() {
        return "LoanMatchParam{" +
                "coin='" + coin + '\'' +
                ", loan_days=" + loanPeriod +
                ", loan_amount='" + loanAmount + '\'' +
                "} " + super.toString();
    }
}
