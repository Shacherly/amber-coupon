package com.trading.backend.domain.base;

import com.alibaba.fastjson.JSONObject;
import com.trading.backend.common.enums.PossessBusinesStageEnum;
import com.trading.backend.common.enums.PossessStageEnum;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.kafka.message.DepositInfoModel;
import com.trading.backend.kafka.message.EarnConsumeModel;

import java.math.BigDecimal;


/**
 * @author ~~ trading.s
 * @date 11:43 10/21/21
 */
public interface PossessInspectable {

    Integer getPossessStage();

    Integer getBusinessStage();

    JSONObject getCouponRule();

    Integer getApplyScene();

    Integer getCouponType();


    default void usableInspect() {
        if (PossessStageEnum.ENABLE.getCode().equals(getPossessStage())
                && PossessBusinesStageEnum.DEFAULT_UNUSED.getCode().equals(getBusinessStage())) {
            return;
        }
        throw new BusinessException(ExceptionEnum.POSSESS_DISABLE);
    }

    default boolean depositInspect(DepositInfoModel depositModel, CashCouponRule rule) {
        return rule.depositEligible(depositModel.getDepositCoin(), new BigDecimal(depositModel.getArrivedAmount()));
    }

    default boolean subscribeInspect(EarnConsumeModel earnModel, CashCouponRule rule) {
        return rule.subscribeEligible(earnModel.getHoldingCoin(), new BigDecimal(earnModel.getHoldingSize()), earnModel.getSubscrPeriod());
    }
}
