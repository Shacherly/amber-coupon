package com.trading.backend.http.request.event;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Accessors(chain = true) @Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EventApprovalParam implements Serializable {
    private static final long serialVersionUID = -4606600788806548997L;


    @NotNull
    @ApiModelProperty(value = "审批事件id")
    private Long eventId;

    @NotNull
    @ApiModelProperty(value = "1审批提交，2审批拒绝，3审批通过")
    private Integer result;
}
