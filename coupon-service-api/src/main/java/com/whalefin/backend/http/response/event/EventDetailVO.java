package com.trading.backend.http.response.event;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ~~ trading.s
 * @date 20:51 11/17/21
 */
@Getter @Setter @Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EventDetailVO implements Serializable {
    private static final long serialVersionUID = -3710758426421954563L;


    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "活动发放的优惠券id")
    private String couponIds;

    @ApiModelProperty(value = "活动类型 0审批发券，1运营人工发券，2专题活动发券")
    private Integer type;

    @ApiModelProperty(value = "活动名称")
    private String name;

    @ApiModelProperty(value = "活动描述")
    private String descr;

    @ApiModelProperty(value = "活动执行状态")
    private Integer eventStage;

    @ApiModelProperty(value = "是否审批流活动")
    private Boolean approvalEvent;

    @ApiModelProperty(value = "活动审批状态")
    private Integer approvalStage;

    @ApiModelProperty(value = "发放对象类型")
    private String objectType;

    @ApiModelProperty(value = "发放对象参数")
    private String objectAttaches;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "活动开始时间")
    private Long startTime;

    @ApiModelProperty(value = "活动创建时间")
    private Long ctime;

    @ApiModelProperty(value = "发放失败原因备注")
    private String resultPhase;

}
