package com.trading.backend.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ~~ trading.s
 * @date 13:57 10/27/21
 */
@Component @Getter @Setter
@ConfigurationProperties(prefix = "coupon-alarm")
public class CouponAlarmProperty {

    private String url;

    private AlarmBody quantityRemain;

    private AlarmBody grantCelling1;

    private AlarmBody grantCelling2;


    @Getter @Setter
    public static class AlarmBody{
        private String code;
        private String name;
        private String msg;
    }
}
