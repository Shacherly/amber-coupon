package com.trading.backend.http.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ~~ trading.s
 * @date 17:07 09/23/21
 */
@Getter
@Setter @NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponReceiptParam extends InternalParamUid implements Serializable {
    private static final long serialVersionUID = -519289097353431210L;

    @ApiModelProperty(value = "领取的券id", required = true)
    private Long couponId;

    @Override
    public String toString() {
        return "CouponReceiptParam{" +
                "couponId=" + couponId +
                "} " + super.toString();
    }

    public CouponReceiptParam(String uid, Long couponId) {
        super(uid);
        this.couponId = couponId;
    }
}
