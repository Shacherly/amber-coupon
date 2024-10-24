package com.trading.backend.template;


import com.trading.backend.config.CouponActivateProperty;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.kafka.message.EarnConsumeModel;
import com.trading.backend.pojo.CouponContributeParam;
import lombok.Getter;
import lombok.Setter;

import java.time.temporal.ChronoUnit;


/**
 * @author ~~ trading.s
 * @date 11:58 10/15/21
 */
@Getter @Setter
public class EligibleEarnStrategy implements IActivateStrategy {

    EarnConsumeModel earnModel;

    @Override
    public CouponContributeParam buildActivateParam(CashCouponRule cashRule, CouponActivateProperty couponProperty) {
        CouponContributeParam param = new CouponContributeParam();
        param.setBusinessId(earnModel.getPositionId());
        param.setDuration(Long.valueOf(cashRule.getMinSubscrDays()));
        param.setUnit(ChronoUnit.DAYS);
        // param.setDuration(couponProperty.getEarnOffset());
        // param.setUnit(ChronoUnit.SECONDS);
        return param;
    }

    public EligibleEarnStrategy(EarnConsumeModel earnModel) {
        this.earnModel = earnModel;
    }
}
