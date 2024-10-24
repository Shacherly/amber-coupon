package com.trading.backend.service.impl;


import com.trading.backend.http.response.dual.DualMatchedResponse;
import com.trading.backend.service.ICouponDualService;
import com.trading.backend.service.IPossesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service @Slf4j
public class CouponDualServiceImpl implements ICouponDualService {

    @Autowired
    private IPossesService possesService;

    @Override
    public DualMatchedResponse getMathCoupons(String uid) {
        return new DualMatchedResponse(possesService.getDualMatchs(uid));
    }
}
