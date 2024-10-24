package com.trading.backend.client;

public interface IAssetServiceApi {

    boolean activityDeposit(String businessId, String uid, String amount, String coin);

}
