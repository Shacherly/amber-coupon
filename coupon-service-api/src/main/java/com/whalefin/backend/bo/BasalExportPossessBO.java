package com.trading.backend.bo;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * Basal Possess BO with time filed of Long millis, and amount field of plain String,
 * without differentiated coupon rules
 * @author ~~ trading.s
 * @date 13:54 10/03/21
 */
@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BasalExportPossessBO implements Serializable {

    private static final long serialVersionUID = -5870694995486743142L;

    @JsonIgnore
    private String uid;

    @ApiModelProperty(value = "券id", required = true)
    private Long couponId;

    @ApiModelProperty(value = "持券id")
    private Long possesssId;

    @ApiModelProperty(value = "券名称")
    private String couponName;

    @ApiModelProperty(value = "多语言化券标题")
    private String couponTitle;

    @ApiModelProperty(value = "多语言化券描述")
    private String couponDescr;
    @ApiModelProperty(value = "多语言化券描述（临时补充字段）")
    private String descr;

    @ApiModelProperty(value = "券类型, 0加息券 1减息券 2收益增强券 3体验金券 5资产券")
    private Integer couponType;

    // @ApiModelProperty(value = "持券状态0待使用1过期2被回收3被禁用4已使用",)
    // private Integer possessStage;

    @ApiModelProperty(value = "具体券的业务进行状态（对应按钮状态）：初始状态默认0是未使用的状态</br>" +
            "->加息券(0使用1已过期3已禁用6已使用)；<br>" +
            "->资产券(0激活1已过期2待发放3已禁用4发放失败6已发放)；<br>" +
            "->减息券(0使用1已过期3已禁用6已使用)")
    private Integer businessStage;

    @ApiModelProperty(value = "有效起点时刻")
    private Long availableStart;

    @ApiModelProperty(value = "有效结束时刻")
    private Long availableEnd;

    @ApiModelProperty(value = "是否可叠加使用")
    private Boolean overlay;

    @ApiModelProperty(value = "券的抽象价值（加息率、减息率、返现金额）")
    private String worth;

    @ApiModelProperty(value = "价值币种")
    private String worthCoin;

    @ApiModelProperty(value = "领券时间")
    private Long receiveTime;

    @ApiModelProperty(value = "跳转连接")
    private String redirectUrl;

    @JsonIgnore
    private JSONObject rule;

}
