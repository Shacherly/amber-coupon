package com.trading.backend.http.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ：trading mu
 * @date ：Created in 2021/10/19 16:27
 * @description：新手任务进度查询返回实体
 * @modified By：
 */
@Data
@ApiModel(value = "新手任务进度查询返回实体")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TaskProgressRes {

    @ApiModelProperty(value = "身份认证-任务状态 (-1:不存在 1:待解锁 2:进行中 3:已完成 4:已失效)")
    private Integer kycTaskStatus;

    @ApiModelProperty(value = "入金-任务状态 (-1:不存在 1:待解锁 2:进行中 3:已完成 4:已失效)")
    private Integer amountTaskStatus;

    @ApiModelProperty(value = "理财-任务状态 (-1:不存在 1:待解锁 2:进行中 3:已完成 4:已失效)")
    private Integer earnTaskStatus;

    @ApiModelProperty(value = "身份认证-任务过期时间")
    private Long kycTaskExpireTime;

    @ApiModelProperty(value = "入金-任务过期时间")
    private Long amountTaskExpireTime;

    @ApiModelProperty(value = "理财-任务过期时间")
    private Long earnTaskExpireTime;

    public TaskProgressRes(){}

    public TaskProgressRes(Integer kycTaskStatus, Integer amountTaskStatus, Integer earnTaskStatus) {
        this.kycTaskStatus = kycTaskStatus;
        this.amountTaskStatus = amountTaskStatus;
        this.earnTaskStatus = earnTaskStatus;
    }
}
