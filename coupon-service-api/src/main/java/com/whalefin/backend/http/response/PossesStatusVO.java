package com.trading.backend.http.response;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PossesStatusVO implements Serializable {

    @ApiModelProperty(value = "券规则配置，返回券类型对应的配置")
    @JsonProperty(index = -1)
    @JSONField(name = "coupon_rule")
    private JSONObject couponRule;

    @ApiModelProperty(value = "id")
    @JsonProperty(index = 0)
    @JSONField(name = "coupon_id")
    private Long couponId;

    @ApiModelProperty(value = "名称")
    @JsonProperty(index = 1)
    @JSONField(name = "coupon_name")
    private String couponName;

    @ApiModelProperty(value = "标题")
    @JsonProperty(index = 2)
    @JSONField(name = "title")
    private String couponTitle;

    @ApiModelProperty(value = "优惠券类型(0加息券，1减息券，5资产券)")
    @JsonProperty(index = 3)
    @JSONField(name = "type")
    private Integer couponType;

    @ApiModelProperty(value = "优惠券状态(1可以，0不可用，-1过期，-2全部发放无可用数量)")
    @JsonProperty(index = 4)
    @JSONField(name = "status")
    private Short status;

    @ApiModelProperty(value = "描述")
    @JsonProperty(index = 5)
    @JSONField(name = "coupon_desrc")
    private String couponDesrc;

    @ApiModelProperty(value = "是否支持叠加使用")
    @JsonProperty(index = 6)
    @JSONField(name = "overlay")
    private Boolean overlay;

    // @ApiModelProperty(value = "每人限领")
    // @JsonProperty(index = 8)
    // private Integer possessLimit;

    @ApiModelProperty(value = "跳转链接")
    @JsonProperty(index = 9)
    @JSONField(name = "redirect_url")
    private String redirectUrl;

    @ApiModelProperty(value = "券价值币种")
    @JsonProperty(index = 10)
    @JSONField(name = "worth_coin")
    private String worthCoin;

    @ApiModelProperty(value = "券加息减息率、面额")
    @JsonProperty(index = 11)
    @JSONField(name = "worth")
    private String worth;

    // @ApiModelProperty(value = "券适用场景")
    // @JsonProperty(index = 12)
    // private Integer applyScene;

    @ApiModelProperty(value = "备注")
    @JsonProperty(index = 13)
    @JSONField(name = "remark")
    private String remark;

    private static final long serialVersionUID = -7339115042904240832L;
}
