package com.trading.backend.http.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.domain.ProfitCouponRule;
import com.trading.backend.domain.TrialCouponRule;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * @author ~~ trading.s
 * @date 15:38 06/10/21
 */
@Setter
@Getter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponCreateParam {

    @Size(max = 100, message = "Length of name is limit 100 !")
    @NotBlank(message = "coupon name can't be null.")
    @ApiModelProperty(value = "券名称", required = true)
    @JSONField(name = "coupon_name")
    private String couponName;

    @NotEmpty(message = "multi_lan_title must not be null !")
    @ApiModelProperty(value = "多语言标题", required = true)
    @JSONField(name = "multi_lan_title")
    private Map<String, String> multiLanTitle;

    @NotEmpty(message = "multi_lan_desc must not be null !")
    @ApiModelProperty(value = "多语言描述", required = true)
    @JSONField(name = "multi_lan_desc")
    private Map<String, String> multiLanDesc;

    @NotNull(message = "type can't be null.")
    @ApiModelProperty(value = "券类型(0:加息券 1：减息券 5：资产券)", required = true)
    private Integer type;

    @NotNull(message = "status can't be null.")
    @ApiModelProperty(value = "券状态（1启用，0禁用）", required = true)
    private Short status;

    @ApiModelProperty(value = "是否允许叠加使用")
    @NotNull(message = "overlay can't be null.")
    private Boolean overlay;

    @NotNull(message = "total can't be null.")
    @Min(value = 0, message = "coupon total can't be less than 0")
    @ApiModelProperty(value = "券总量", required = true)
    private Long total;

    @NotNull(message = "possess Limit can't be null.")
    @ApiModelProperty(value = "每人限领数量(9999表示无限制)", required = true)
    @JSONField(name = "possess_limit")
    private Integer possessLimit;

    @Size(max = 1024, message = "Length of redirectUrl is limit 1024 !")
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

    @NotBlank
    @ApiModelProperty(value = "券价值:资产券金额、加息率、减息率", required = true)
    private String worth;

    @Size(max = 100, message = "Length of remark is limit 100 !")
    @ApiModelProperty(value = "备注")
    private String remark;

    @NotNull
    @ApiModelProperty(value = "应用场景1理财加息，11借贷减息，51KYC返现，52入金返现，53理财返现", required = true)
    @JSONField(name = "apply_scene")
    private Integer applyScene;

    @ApiModelProperty(value = "是否审批券")
    @JSONField(name = "grant_approval")
    private Boolean grantApproval;

    // @ApiModelProperty(value = "券规则配置", required = true)
    // // @NotNull
    // @JSONField(name = "coupon_rule")
    // private String couponRule;

    @ApiModelProperty(value = "加息券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "inter_coupon_rule")
    private InterCouponRule interCouponRule;

    @ApiModelProperty(value = "资产券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "cash_coupon_rule")
    private CashCouponRule cashCouponRule;

    @ApiModelProperty(value = "减息券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "deduct_coupon_rule")
    private DeductCouponRule deductCouponRule;

    @ApiModelProperty(value = "体验金券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "trial_coupon_rule")
    private TrialCouponRule trialCouponRule;

    @ApiModelProperty(value = "收益增强券规则配置，根据券的类型传不同的配置字段，选其一")
    @JSONField(name = "profit_coupon_rule")
    private ProfitCouponRule profitCouponRule;
}