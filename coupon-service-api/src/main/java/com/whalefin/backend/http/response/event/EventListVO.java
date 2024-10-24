package com.trading.backend.http.response.event;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;


/**
 * @author ~~ trading.s
 * @date 11:29 11/05/21
 */
@Getter @Setter @Slf4j
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EventListVO implements Serializable {
    private static final long serialVersionUID = 6243590443516337897L;

    private Long id;

    @ApiModelProperty(value = "活动类型(0-运营人工发券，1-专题活动发券)")
    private Integer eventType;

    @ApiModelProperty(value = "是否审批活动")
    private Boolean approvalNeed;

    @ApiModelProperty(value = "0未开始，1进行中，2部分用户发放成功，3全部发放成功，4全部发放失败<br>")
    private Integer eventStage;

    @ApiModelProperty(value = "0审批未提交，1审批中，2审批拒绝，3审批通过")
    private Integer approvalStage;

    @ApiModelProperty(value = "活动名称（限制字符长度32）")
    private String eventName;

    @ApiModelProperty(value = "活动说明（限制字符长度512）")
    private String eventDescr;

    @ApiModelProperty(value = "活动开始时间，可不传，默认为当前时间开始")
    private Long startTime;

    // @ApiModelProperty(value = "活动结束时间")
    // private Long endTime;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long updateTime;
}
