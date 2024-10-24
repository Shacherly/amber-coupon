package com.trading.backend.http.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.bo.TaskStatusBo;
import com.trading.backend.bo.TaskTimeBo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @author ：trading mu
 * @date ：Created in 2021/11/18 17:50
 * @description：新手任务状态时间返回实体
 * @modified By：
 */

@Data
@ApiModel(value = "新手任务状态时间返回实体")
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TaskProgressAggreRes {

    @ApiModelProperty(value = "任务状态")
    private TaskStatusBo taskStatus;

    @ApiModelProperty(value = "任务过期时间(任务不存在时为null)")
    private TaskTimeBo taskTime;

}
