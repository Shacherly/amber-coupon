package com.trading.backend.domain.vo;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.ExportableCoinRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter @Setter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponPolymericRuleVO {

    private static final long serialVersionUID = 1695347339180973089L;

    @ApiModelProperty(value = "使用规则配置")
    @JSONField(name = "coin_rules", ordinal = 0)
    private List<ExportableCoinRule> coinRules;

    // 加息券
    @ApiModelProperty(value = "加息天数")
    @JSONField(name = "inter_days", ordinal = 1)
    private Integer interDays;

    @ApiModelProperty(value = "最低申购天数(加息券、申购资产券)")
    @JSONField(name = "min_subscr_days", ordinal = 2)
    private Integer minSubscrDays;

    @ApiModelProperty(value = "最高申购天数")
    @JSONField(name = "max_subscr_days", ordinal = 3)
    private Integer maxSubscrDays;

    // 减息券
    @ApiModelProperty(value = "减息方式1折扣减免 2利率减免")
    @JSONField(name = "deduct_way", ordinal = 4)
    private Integer deductWay;

    @ApiModelProperty(value = "减息天数")
    @JSONField(name = "deduct_days", ordinal = 5)
    private Integer deductDays;

    @ApiModelProperty(value = "最高借贷天数")
    @JSONField(name = "max_loan_days", ordinal = 6)
    private Integer maxLoanDays;

    @ApiModelProperty(value = "最低借贷天数")
    @JSONField(name = "min_loan_days", ordinal = 7)
    private Integer minLoanDays;

    @ApiModelProperty(value = "借贷类型1所有类型 2活期资产抵押 3定期资产抵押")
    @JSONField(name = "loan_type", ordinal = 8)
    private Integer loanType;


}
