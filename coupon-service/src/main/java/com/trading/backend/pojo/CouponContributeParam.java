package com.trading.backend.pojo;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.temporal.TemporalUnit;


@Getter @Setter @Accessors(chain = true)
public class CouponContributeParam {

    /**
     * 服务业务id
     */
    private String businessId;

    /**
     * 用券业务币种
     */
    private String businessCoin;

    /**
     * 券的抽象服务时间
     */
    private Long duration;

    /**
     * 服务时间单位
     */
    private TemporalUnit unit;

    public CouponContributeParam(String businessId, String businessCoin, Long duration, TemporalUnit unit) {
        this.businessId = businessId;
        this.businessCoin = businessCoin;
        this.duration = duration;
        this.unit = unit;
    }

    public CouponContributeParam() {
    }
}
