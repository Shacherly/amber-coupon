package com.trading.backend.bo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ：trading mu
 * @date ：Created in 2021/11/18 17:47
 * @description：任务状态
 * @modified By：
 */
@Data
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TaskStatusBo {

    @ApiModelProperty(value = "身份认证-任务状态 (-1:不存在 1:待解锁 2:进行中 3:已完成 4:已失效)")
    private Integer kycTaskStatus;

    @ApiModelProperty(value = "入金-任务状态 (-1:不存在 1:待解锁 2:进行中 3:已完成 4:已失效)")
    private Integer amountTaskStatus;

    @ApiModelProperty(value = "理财-任务状态 (-1:不存在 1:待解锁 2:进行中 3:已完成 4:已失效)")
    private Integer earnTaskStatus;

}
