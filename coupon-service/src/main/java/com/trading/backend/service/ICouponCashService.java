package com.trading.backend.service;

import com.trading.backend.kafka.message.EarnConsumeModel;

public interface ICouponCashService {


    void onRedeemRefreshCashStage(EarnConsumeModel model);

}
