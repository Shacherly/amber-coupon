package com.trading.backend.http.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;


@Getter @Setter
@Accessors(chain = true)
public class ReddotReadParam extends ExternalHeaderUid implements Serializable {
    private static final long serialVersionUID = 1375258430133646246L;

    @ApiModelProperty(value = "优惠券id数组")
    private List<Long> couponIds;

    @Override
    public String toString() {
        return "ReddotReadParam{" +
                "couponIds=" + couponIds +
                "} " + super.toString();
    }
}
