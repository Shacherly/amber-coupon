package com.trading.backend.service;

import com.trading.backend.http.response.dual.DualMatchedResponse;

public interface ICouponDualService {


    DualMatchedResponse getMathCoupons(String uid);
}
