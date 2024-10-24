package com.trading.backend.http.response.club;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClubPossessVO implements Serializable {
    private static final long serialVersionUID = -738766087054875024L;

    // @ApiModelProperty(value = "券规则配置，返回券类型对应的配置")
    // @JsonProperty(index = -1)
    // @JSONField(name = "coupon_rule")
    // private JSONObject couponRule;

    @ApiModelProperty(value = "券id")
    @JSONField(name = "coupon_id")
    private Long couponId;

    @ApiModelProperty(value = "多语言化券标题")
    private String couponTitle;

    @ApiModelProperty(value = "多语言化券描述")
    private String couponDescr;

    @ApiModelProperty(value = "持券id")
    @JSONField(name = "possess_id")
    private Long possessId;

    @ApiModelProperty(value = "优惠券类型(0加息券，1减息券，5资产券)")
    @JSONField(name = "couponType")
    private Integer couponType;

    @ApiModelProperty(value = "减息方式1折扣减免 2利率减免")
    @JSONField(name = "deduct_way")
    private Integer deductWay;

    @ApiModelProperty(value = "券价值币种")
    @JSONField(name = "worth_coin")
    private String worthCoin;

    @ApiModelProperty(value = "券加息减息率、面额")
    @JSONField(name = "worth")
    private String worth;


    @ApiModelProperty(value = "业务状态")
    @JSONField(name = "business_stage")
    private Integer businessStage;

    // @ApiModelProperty(value = "持券状态")
    // @JsonProperty(index = 13)
    // @JSONField(name = "possess_stage")
    // private Integer possessStage;

    @ApiModelProperty(value = "券过期时间")
    @JSONField(name = "expr_time")
    private Long exprTime;

    @ApiModelProperty(value = "未领取的券是否有效（过期、发放完了、禁用了为无效）")
    @JSONField(name = "coupon_valid")
    private Boolean couponValid = true;

}
