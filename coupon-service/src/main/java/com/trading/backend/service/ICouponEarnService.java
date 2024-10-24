package com.trading.backend.service;


import com.trading.backend.http.request.earn.EarnMatchParam;
import com.trading.backend.http.response.earn.EarnMatchedResponse;

public interface ICouponEarnService {


    EarnMatchedResponse getMathCoupons(EarnMatchParam param);
}
