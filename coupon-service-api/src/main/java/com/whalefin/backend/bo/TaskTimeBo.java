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
public class TaskTimeBo {

    @ApiModelProperty(value = "身份认证-任务过期时间")
    private Long kycTaskExpireTime;

    @ApiModelProperty(value = "入金-任务过期时间")
    private Long amountTaskExpireTime;

    @ApiModelProperty(value = "理财-任务过期时间")
    private Long earnTaskExpireTime;
}
