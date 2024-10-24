package com.trading.backend.http.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @author ~~ trading.s
 * @date 17:30 09/22/21
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponPossessParam extends InternalParamUid {

    private static final long serialVersionUID = -9083979621479907307L;

    @ApiModelProperty(value = "优惠券类型 0加息券，1减息券，2收益增强，3体验金，5资产券（可传入数组查询多个类型）")
    private List<Integer> coupon_types;

    // @ApiModelProperty(value = "true: 默认有效券列表  false: 失效券列表")
    // private boolean valid = true;


    @Override
    public String toString() {
        return "CouponPossessParam{" +
                "coupon_types=" + coupon_types +
                "} " + super.toString();
    }
}
