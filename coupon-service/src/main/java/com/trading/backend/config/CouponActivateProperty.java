package com.trading.backend.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * @author ~~ trading.s
 * @date 13:57 10/27/21
 */
@Getter @Setter
@Component
@ConfigurationProperties(prefix = "coupon-property")
public class CouponActivateProperty {

    private static final BigDecimal NOVICE_DEPOSIT_FLOOR = new BigDecimal("100");

    /**
     * 资产券到账间隔  seconds
     */
    private String arriveOffset;

    private String earnArriveOffset;




    public long getOffset() {
        return Long.parseLong(arriveOffset);
    }

    public LocalDateTime getOffsetTime() {
        return LocalDateTime.now().plusSeconds(getOffset());
    }

    public long getEarnOffset() {
        return Long.parseLong(earnArriveOffset);
    }

    public LocalDateTime getEarnOffsetTime() {
        return LocalDateTime.now().plusSeconds(getEarnOffset());
    }

    public BigDecimal getNoviceDepositFloor() {
        return NOVICE_DEPOSIT_FLOOR;
    }
}
