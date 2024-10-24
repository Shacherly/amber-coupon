package com.trading.backend.http.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * @author ~~ trading.s
 * @date 15:59 10/08/21
 */
@Setter @Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FullScalePossessParam extends ExternalHeaderUid {

    private static final long serialVersionUID = 6958974733566037237L;

    @ApiModelProperty(value = "优惠券类型 0加息券，1减息券，2收益增强，3体验金，5资产券（可传入数组查询多个类型）")
    private List<Integer> coupon_types;

    @ApiModelProperty(value = "true: 有效券列表  false: 失效券列表，获取失效券不用传type，默认获取近三个月内失效的所有类型券")
    private Boolean valid = true;

    @ApiModelProperty(value = "RECV_TIME_ASC、RECV_TIME_DESC、EXPR_TIME_ASC、EXPR_TIME_DESC")
    private String sort = "DEFAULT";

    // @ApiModelProperty(value = "获取时间排序ASC顺序、DESC逆序")
    // private String receiveTimeOrder;
    //
    // @ApiModelProperty(value = "失效时间排序ASC顺序、DESC逆序")
    // private String availableEndOrder;


    @Override
    public String toString() {
        return "FullScalePossessParam{" +
                "coupon_types=" + coupon_types +
                ", valid=" + valid +
                ", sort='" + sort + '\'' +
                "} " + super.toString();
    }
}
