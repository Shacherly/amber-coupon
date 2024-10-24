package com.trading.backend.template;


import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.kafka.message.AbstractConsumerModel;
import com.trading.backend.kafka.message.EarnConsumeModel;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


/**
 * @author ~~ trading.s
 * @date 18:15 10/09/21
 */
public class ActivateStrategyManager {

    private static Map<CouponApplySceneEnum, Function<AbstractConsumerModel, IActivateStrategy>> STRATEGY_MAP;

    static {
        STRATEGY_MAP = new HashMap<>(16);
        STRATEGY_MAP.put(CouponApplySceneEnum.KYC_VERIFY_CASH, nullObj -> new KycAuthStrategy());
        STRATEGY_MAP.put(CouponApplySceneEnum.DEPOSIT_CASH, nullObj -> new FullDepositStrategy());
        STRATEGY_MAP.put(CouponApplySceneEnum.EARN_CASH, earnModel -> new EligibleEarnStrategy((EarnConsumeModel) earnModel));
    }

    public static Function<AbstractConsumerModel, IActivateStrategy> getStrategyFunction(CouponApplySceneEnum keyEnum) {
        return STRATEGY_MAP.get(keyEnum);
    }
}
