package com.trading.backend.http.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.StringJoiner;


@EqualsAndHashCode(callSuper = true) @Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FullScaleCouponParam extends InternalParamUid {
    private static final long serialVersionUID = -262197786257536608L;

    @ApiModelProperty(value = "优惠券id")
    private List<Long> couponIds;


    @Override
    public String toString() {
        return new StringJoiner(", ", FullScaleCouponParam.class.getSimpleName() + "[", "]")
                .add("couponIds=" + couponIds)
                .toString();
    }
}
