package com.trading.backend.http.request.event;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.annotation.Condition;
import com.trading.backend.annotation.CoulumnCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author ~~ trading.s
 * @date 15:34 11/03/21
 */
@Setter
@Getter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EventListParam implements Serializable {
    private static final long serialVersionUID = -5000089865488962754L;


    @CoulumnCondition(property = "type")
    @ApiModelProperty(value = "活动类型(0审批发券，1运营人工发券，2专题活动发券)")
    private Integer event_type;

    @CoulumnCondition(property = "approvalEvent")
    @ApiModelProperty(value = "是否审批活动")
    private Boolean approval_need;

    @CoulumnCondition(property = "name", condition = Condition.LIKE)
    @ApiModelProperty(value = "活动名称（限制字符长度32）")
    private String event_name;

    @CoulumnCondition(property = "objectType")
    @ApiModelProperty(value = "发放客户对象类型 ALL SELECTED_USER IMPORTED_USER USER_LABEL SINGLE_USER")
    private String object_type;

    @CoulumnCondition(property = "eventStage")
    @ApiModelProperty(value = "0未开始，1进行中，2部分用户发放成功，3全部发放成功，4全部发放失败<br>")
    private Integer event_stage;

    @CoulumnCondition(property = "approvalStage")
    @ApiModelProperty(value = "0审批未提交，1审批中，2审批拒绝，3审批通过")
    private Integer approval_stage;

    @CoulumnCondition(property = "ctime", condition = Condition.BETWEEN)
    @ApiModelProperty(value = "活动开始时间1", dataType = "long")
    private LocalDateTime start_time1;

    @CoulumnCondition(property = "ctime", condition = Condition.BETWEEN)
    @ApiModelProperty(value = "活动开始时间2", dataType = "long")
    private LocalDateTime start_time2;
}
