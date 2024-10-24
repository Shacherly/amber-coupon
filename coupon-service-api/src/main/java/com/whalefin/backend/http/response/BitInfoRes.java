package com.trading.backend.http.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ：trading mu
 * @date ：Created in 2021/10/27 11:20
 * @description：BTC币价和涨幅返回实体
 * @modified By：
 */

@Data
@ApiModel(value = "BTC币价和涨幅返回实体")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BitInfoRes {

    @ApiModelProperty(value = "币价")
    private BigDecimal coinPrice;

    @ApiModelProperty(value = "涨幅")
    private Integer increase;

}
