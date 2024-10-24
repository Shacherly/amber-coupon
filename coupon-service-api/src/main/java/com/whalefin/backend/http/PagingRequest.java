package com.trading.backend.http;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Getter @Setter

@Accessors(chain = true)
public class PagingRequest implements Serializable {
    private static final long serialVersionUID = -1146501441794855263L;


    @NotNull(message = "page can't be null")
    @Min(value = 1, message = "page can't be less than 1")
    @ApiModelProperty(value = "页码", required = true)
    private Integer page;

    @NotNull(message = "page_size can't be null")
    @Min(value = 1, message = "page_size can't be less than 1")
    @ApiModelProperty(value = "每页数量", required = true)
    private Integer page_size;

    public static String ORDER_BY = "CREATED_TIME DESC";

}
