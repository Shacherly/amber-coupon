package com.trading.backend.http.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponIds implements Serializable {
    private static final long serialVersionUID = -1923519526093587584L;

    @NotEmpty
    @ApiModelProperty(value = "券id，传数组可批量查询", required = true)
    private List<Long> couponIds;

    @Override
    public String toString() {
        return "CouponIds{" +
                "couponIds=" + couponIds +
                '}';
    }
}
