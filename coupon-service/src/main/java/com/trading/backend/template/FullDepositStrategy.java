package com.trading.backend.template;


import com.trading.backend.config.CouponActivateProperty;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.pojo.CouponContributeParam;

import java.time.temporal.ChronoUnit;


/**
 * @author ~~ trading.s
 * @date 11:58 10/15/21
 */
public class FullDepositStrategy implements IActivateStrategy {

    @Override
    public CouponContributeParam buildActivateParam(CashCouponRule cashRule, CouponActivateProperty couponProperty) {
        CouponContributeParam param = new CouponContributeParam();
        param.setBusinessId(null);
        param.setDuration(couponProperty.getOffset());
        param.setUnit(ChronoUnit.SECONDS);
        return param;
    }
}
