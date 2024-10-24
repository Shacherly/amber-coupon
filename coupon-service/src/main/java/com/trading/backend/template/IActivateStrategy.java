package com.trading.backend.template;


import com.trading.backend.config.CouponActivateProperty;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.pojo.CouponContributeParam;


/**
 * @author ~~ trading.s
 * @date 11:58 10/15/21
 */
@FunctionalInterface
public interface IActivateStrategy {

    CouponContributeParam buildActivateParam(CashCouponRule cashRule, CouponActivateProperty couponProperty);

}
