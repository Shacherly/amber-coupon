package com.trading.backend.service;

import com.trading.backend.domain.Coupon;
import com.trading.backend.mapper.CouponMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService {

    @Autowired
    private CouponMapper couponMapper;

    public Coupon saveCoupon0(Coupon source) {
        couponMapper.insertSelective(source);
        return source;
    }
}
