package com.trading.backend.service;

import com.trading.backend.bo.WorthGrantBO;
import com.trading.backend.kafka.message.DepositInfoModel;
import com.trading.backend.kafka.message.EarnConsumeModel;
import com.trading.backend.kafka.message.KycStageModel;
import com.trading.backend.kafka.message.UserRegistryModel;

import java.time.LocalDateTime;
import java.util.function.BiConsumer;

public interface ICashCouponService {

    void grantOnRegistry(UserRegistryModel model);

    void activOnKycSubmit(KycStageModel model, BiConsumer<String, LocalDateTime> post);

    void activOnDepositSettle(DepositInfoModel model, BiConsumer<String, LocalDateTime> post);

    void activOnUserSubscribe(EarnConsumeModel model, BiConsumer<String, LocalDateTime> post);

    boolean onRedeemRefreshCashStage(EarnConsumeModel model, BiConsumer<String, LocalDateTime> post);


    void accomplish(WorthGrantBO grantBo);

    void accomplish1(WorthGrantBO grantBo);

    BiConsumer<String, LocalDateTime> postback();
}
