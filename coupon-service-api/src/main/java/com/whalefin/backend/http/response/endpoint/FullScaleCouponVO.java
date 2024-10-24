package com.trading.backend.http.response.endpoint;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FullScaleCouponVO implements Serializable {
    private static final long serialVersionUID = 5263724952564145648L;

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

    @ApiModelProperty(value = "券类型, 0加息券 1减息券 2收益增强券 3体验金券 5资产券")
    private Integer couponType;

    @ApiModelProperty(value = "券的抽象价值（加息率、减息率、返现金额）")
    private String worth;

    @ApiModelProperty(value = "价值币种")
    private String worthCoin;

    @ApiModelProperty(value = "有效期起")
    private Long exprAtBegin;

    @ApiModelProperty(value = "有效期止")
    private Long exprAtEnd;

    @ApiModelProperty(value = "是否已经领取")
    private Boolean received = false;

    @Override
    public String toString() {
        return "FullScaleCouponVO{" +
                "couponId=" + couponId +
                ", possesssId=" + possesssId +
                ", couponName='" + couponName + '\'' +
                ", couponTitle='" + couponTitle + '\'' +
                ", couponDescr='" + couponDescr + '\'' +
                ", couponType=" + couponType +
                ", worth='" + worth + '\'' +
                ", worthCoin='" + worthCoin + '\'' +
                ", exprAtBegin=" + exprAtBegin +
                ", exprAtEnd=" + exprAtEnd +
                ", received=" + received +
                '}';
    }
}
