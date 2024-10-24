package com.trading.backend.http.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * @author ~~ trading.s
 * @date 17:30 09/22/21
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponDetailParam {

    private static final long serialVersionUID = -9083979621479907307L;

    @NotEmpty
    @ApiModelProperty(value = "券id，传数组可批量查询", required = true)
    private List<Long> couponIds;

    @Override
    public String toString() {
        return "CouponDetailParam{" +
                "coupon_id=" + couponIds +
                '}';
    }

    public CouponDetailParam(List<Long> couponIds) {
        this.couponIds = couponIds;
    }

    public CouponDetailParam() {
    }
}
