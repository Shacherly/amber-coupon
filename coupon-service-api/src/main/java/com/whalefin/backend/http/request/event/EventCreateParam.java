package com.trading.backend.http.request.event;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
// import io.renren.tag.client.dto.dto.FilterCondition;
import io.renren.tag.client.dto.dto.FilterCondition;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


/**
 * @author ~~ trading.s
 * @date 15:34 11/03/21
 */
@Setter
@Getter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EventCreateParam implements Serializable {
    private static final long serialVersionUID = -5000089865488962754L;


    @NotNull
    @ApiModelProperty(value = "活动类型(0：审批发券，1：运营人工发券，2：专题活动发券，3：BWC审批发券)", required = true)
    private Integer eventType;

    // @NotNull
    // @ApiModelProperty(value = "是否审批活动", required = true)
    // private Boolean approvalNeed;

    @NotBlank
    @ApiModelProperty(value = "活动名称（限制字符长度32）", required = true)
    private String eventName;

    @NotBlank
    @ApiModelProperty(value = "活动说明（限制字符长度512）")
    private String eventDescr;

    @NotEmpty
    @ApiModelProperty(value = "活动发券id集合")
    private List<Long> couponIds;

    // 对应 object_type
    @NotBlank
    @ApiModelProperty(value = "发放客户对象类型ALL SELECTED_USER IMPORTED_USER USER_LABEL SINGLE_USER")
    private String userRangeType;

    // 对应 object_attaches
    @ApiModelProperty(value = "用户关联的查询参数")
    private String userRangeParam;

    @ApiModelProperty(value = "多标签筛选条件")
    private FilterCondition multiTagCondition;

    // @ApiModelProperty(value = "用户对象id集合")
    // private List<String> uids;

    // @ApiModelProperty(value = "0未开始，1进行中，2部分用户发放成功，3全部发放成功，4全部发放失败<br>" +
    //         "这个不用传，新创建的默认为0")
    // private Integer eventStage;
    //
    // @ApiModelProperty(value = "0审批未提交，1审批中，2审批拒绝，3审批通过")
    // private Integer approvalStage;

    // @NotNull
    @ApiModelProperty(value = "活动开始时间，可不传，默认为当前时间开始")
    private Long startTime;

    // @NotNull
    @ApiModelProperty(value = "活动结束时间")
    private Long endTime;

    @ApiModelProperty(value = "备注（限制字符长度512）")
    private String remark;
}
