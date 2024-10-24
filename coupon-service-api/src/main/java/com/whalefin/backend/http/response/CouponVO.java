package com.trading.backend.http.response;


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
public class CouponVO implements Serializable {

    private static final long serialVersionUID = 7646599965186500526L;

    @ApiModelProperty(value = "id")
    @JsonProperty(index = 0)
    private Long couponId;

    @ApiModelProperty(value = "名称")
    @JsonProperty(index = 1)
    private String name;

    @ApiModelProperty(value = "标题")
    @JsonProperty(index = 2)
    private String title;

    @ApiModelProperty(value = "优惠券类型(0加息券，1减息券，5资产券)")
    @JsonProperty(index = 3)
    private Integer type;

    @ApiModelProperty(value = "优惠券状态(1可以，0不可用，-1过期，-2全部发放无可用数量)")
    @JsonProperty(index = 4)
    private Integer status;

    @ApiModelProperty(value = "描述")
    @JsonProperty(index = 5)
    private String descr;

    @ApiModelProperty(value = "是否支持叠加使用")
    @JsonProperty(index = 6)
    private Boolean overlay;

    @ApiModelProperty(value = "跳转链接")
    @JsonProperty(index = 9)
    private String redirectUrl;

    @ApiModelProperty(value = "券价值币种")
    private String worthCoin;

    @ApiModelProperty(value = "券价值、面额")
    private String worth;

    @ApiModelProperty(value = "备注")
    @JsonProperty(index = 10)
    private String remark;

    /** short detail above **/
    // @ApiModelProperty(value = "券适用场景")
    // private Integer applyScene;

    @ApiModelProperty(value = "过期天数")
    private Integer exprInDays;

    @ApiModelProperty(value = "有效开始时刻")
    private Long exprAtStart;

    @ApiModelProperty(value = "有效结束时刻")
    private Long exprAtEnd;

    @ApiModelProperty(value = "券总量")
    @JsonProperty(index = 7)
    private Long total;

    @ApiModelProperty(value = "可用总量")
    @JsonProperty(index = 8)
    private Long availableNum;

    @ApiModelProperty(value = "每人限领")
    @JsonProperty(index = 8)
    private Integer possessLimit;
    // private List<CommonCoinRule> coinRules;
}
