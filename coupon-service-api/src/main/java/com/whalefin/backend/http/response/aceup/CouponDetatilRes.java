package com.trading.backend.http.response.aceup;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.domain.ProfitCouponRule;
import com.trading.backend.domain.TrialCouponRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.util.Map;

/**
 * @author ：trading mu
 * @date ：Created in 2021/10/16 16:17
 * @description：
 * @modified By：
 */
@Data
@ApiModel(value = "券详情")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponDetatilRes {

    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @ApiModelProperty(value = "券名称", required = true)
    @JSONField(name = "coupon_name")
    private String couponName;

    @ApiModelProperty(value = "多语言标题", required = true)
    @JSONField(name = "multi_lan_title")
    private Map<String, String> multiLanTitle;

    @ApiModelProperty(value = "多语言描述", required = true)
    @JSONField(name = "multi_lan_desc")
    private Map<String, String> multiLanDesc;

    @ApiModelProperty(value = "券类型", required = true)
    private Integer type;

    @ApiModelProperty(value = "券状态", required = true)
    private Short status;

    @ApiModelProperty(value = "是否可叠加使用", required = true)
    private Boolean overlay;

    @Min(value = 0, message = "coupon total can't be less than 0")
    @ApiModelProperty(value = "券总量", required = true)
    private Long total;

    @ApiModelProperty(value = "每人限领数量", required = true)
    @JSONField(name = "possess_limit")
    private Integer possessLimit;

    @ApiModelProperty(value = "跳转地址")
    @JSONField(name = "redirect_url")
    private String redirectUrl;

    @ApiModelProperty(value = "固定天数过期（测试阶段填这个，expr_at_start、expr_at_end不用管）")
    @JSONField(name = "expr_in_days")
    private Integer exprInDays;

    @ApiModelProperty(value = "固定起止日期过期-起始")
    @JSONField(name = "expr_at_start")
    private Long exprAtStart;

    @ApiModelProperty(value = "固定起止日期过期-结束")
    @JSONField(name = "expr_at_end")
    private Long exprAtEnd;

    @ApiModelProperty(value = "券价值币种（资产券有）")
    @JSONField(name = "worth_coin")
    private String worthCoin;

    @ApiModelProperty(value = "券价值:资产券金额、加息率、减息率", required = true)
    private String worth;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否审批券(true/false)")
    private Boolean grantApproval;

    @ApiModelProperty(value = "应用场景1理财加息，11借贷减息，51KYC返现，52入金返现，53理财返现", required = true)
    @JSONField(name = "apply_scene")
    private Integer applyScene;

    @ApiModelProperty(value = "加息券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "inter_coupon_rule")
    private InterCouponRule interCouponRule;

    @ApiModelProperty(value = "资产券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "cash_coupon_rule")
    private CashCouponRule cashCouponRule;

    @ApiModelProperty(value = "减息券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "deduct_coupon_rule")
    private DeductCouponRule deductCouponRule;

    @ApiModelProperty(value = "收益增强券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "profit_coupon_rule")
    private ProfitCouponRule profitCouponRule;

    @ApiModelProperty(value = "体验金券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "trial_coupon_rule")
    private TrialCouponRule trialCouponRule;
}
