package com.trading.backend.http.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author ~~ trading.s
 * @date 17:25 06/17/21
 */
@Getter
@Setter
@ApiModel(value = "券列表")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponListVO implements Serializable {

    private static final long serialVersionUID = -1952645394449770230L;

    @ApiModelProperty(value = "券ID")
    private Long id;

    @ApiModelProperty(value = "券名称")
    private String couponName;

    @ApiModelProperty(value = "券标题")
    private String couponTitle;

    @ApiModelProperty(value = "券类型 0加息 1减息  5资产")
    private Integer couponType;

    @ApiModelProperty(value = "过期类型")
    private Integer expireType;

    @ApiModelProperty(value = "固定天数过期")
    private Integer expireDays;

    @ApiModelProperty(value = "固定起止日期过期-起始")
    private Long expireAtStart;

    @ApiModelProperty(value = "固定起止日期过期-终止")
    private Long expireAtEnd;

    @ApiModelProperty(value = "总量")
    private String total;

    @ApiModelProperty(value = "已发放量")
    private Long released;

    @ApiModelProperty(value = "未发放量")
    private String notReleased;

    @ApiModelProperty(value = "已使用量")
    private Long used;

    @ApiModelProperty(value = "未使用量")
    private Long notUsed;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "每人限领")
    private Long limitPerPeople;

    @ApiModelProperty(value = "剩余量")
    private String remain;
}
