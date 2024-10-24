package com.trading.backend.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.base.CouponRuleBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DeductCouponRule extends CouponRuleBase {

    private static final long serialVersionUID = 5898205752642114487L;

    @ApiModelProperty(value = "减息方式1折扣减免 2利率减免")
    @JSONField(name = "deduct_way", ordinal = 1)
    private Integer deductWay;

    @ApiModelProperty(value = "减息天数")
    @JSONField(name = "deduct_days", ordinal = 2)
    private Integer deductDays;

    @ApiModelProperty(value = "最高借贷天数")
    @JSONField(name = "max_loan_days", ordinal = 3)
    private Integer maxLoanDays;

    @ApiModelProperty(value = "最低借贷天数")
    @JSONField(name = "min_loan_days", ordinal = 4)
    private Integer minLoanDays;

    @ApiModelProperty(value = "借贷类型1所有类型 2活期资产抵押 3定期资产抵押")
    @JSONField(name = "loan_type", ordinal = 5)
    private Integer loanType;

    public boolean typeUsable(Integer loanType) {
        return Objects.equals(this.loanType, loanType);
    }

    public boolean typesUsable(List<Integer> loanTypes) {
        return new HashSet<>(loanTypes).contains(this.loanType);
    }

    public boolean periodUsable(Integer loanPeriod) {
        return loanPeriod >= minLoanDays && loanPeriod <= maxLoanDays;
    }

}
