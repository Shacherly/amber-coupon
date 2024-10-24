package com.trading.backend.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author ~~ trading.s
 * @date 13:57 10/27/21
 */
@Getter @Setter
@Component
@ConfigurationProperties(prefix = "coupon-task-property")
public class CouponTaskProperty {

    private String grantsScan;

    private String grantsTake;

    private String grantsCoin;

    private String grantsDailyLimit1;

    private String grantsDailyLimit2;

    private String exprsScan;

    public long getScanOffset() {
        return Long.parseLong(grantsScan);
    }

    public long getGrantOffset() {
        return Long.parseLong(grantsTake);
    }

    public BigDecimal getGrantsLimit1() {
        return Optional.ofNullable(grantsDailyLimit1)
                       .map(BigDecimal::new)
                       .orElse(BigDecimal.ONE);
    }

    public BigDecimal getGrantsLimit2() {
        return Optional.ofNullable(grantsDailyLimit2)
                       .map(BigDecimal::new)
                       .orElse(BigDecimal.valueOf(2));
    }

    public long getExprsIntvl() {
        return Long.parseLong(exprsScan);
    }
}
