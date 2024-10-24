package com.trading.backend.domain.base;


import com.trading.backend.common.enums.CouponStatusEnum;

/**
 * @author ~~ trading.s
 * @date 11:43 10/21/21
 */
public interface CouponInspectable {

    Short getStatus();

    Long getTotal();

    Long getIssue();

    Long getPreLock();


    default boolean available() {
        return CouponStatusEnum.ENABLED.getStatus().equals(getStatus());
    }

    default boolean sufficient(Long delta) {
        return getTotal() - getIssue() - getPreLock() - delta >= 0;
    }

    default boolean unSufficient(Long delta) {
        return !sufficient(delta);
    }
}
