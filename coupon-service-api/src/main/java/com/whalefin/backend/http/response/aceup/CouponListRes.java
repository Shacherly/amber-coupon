package com.trading.backend.http.response.aceup;


import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author ~~ trading.s
 * @date 17:25 06/17/21
 */
@Data
@ApiModel(value = "券列表返回实体")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponListRes implements Serializable {

    private static final long serialVersionUID = -1952645394449770230L;

    @ApiModelProperty(value = "券ID")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "标题")
    private Map<String, String> title;

    @ApiModelProperty(value = "优惠券类型(0加息券，1减息券，5资产券)")
    private Integer type;

    @ApiModelProperty(value = "优惠券状态(1启用，0禁用，-1过期，-2全部发放无可用数量)")
    private Short status;

    @ApiModelProperty(value = "过期天数")
    private Integer exprInDays;

    @ApiModelProperty(value = "有效开始时刻")
    private Long exprAtStart;

    @ApiModelProperty(value = "有效结束时刻")
    private Long exprAtEnd;

    @ApiModelProperty(value = "券总量")
    private Long total;

    @ApiModelProperty(value = "已发放量")
    private Long released;

    @ApiModelProperty(value = "未发放量")
    private Long notReleased;

    @ApiModelProperty(value = "已使用量")
    private Long used;

    @ApiModelProperty(value = "未使用量")
    private Long notUsed;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "是否审批券(true/false)")
    private Boolean grantApproval;

    @ApiModelProperty(value = "每人限领数量")
    private Integer possessLimit;

}
