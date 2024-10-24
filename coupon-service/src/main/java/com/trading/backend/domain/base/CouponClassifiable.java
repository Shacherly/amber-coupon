package com.trading.backend.domain.base;


import com.alibaba.fastjson.JSONObject;

/**
 * @author ~~ trading.s
 * @date 11:32 10/06/21
 */
public interface CouponClassifiable {

    JSONObject getRule();

    Integer getType();

    // default InterCouponRule getCouponRule(CouponTypeEnum.Interest type) {
    //     CouponTypeEnum typeEnum = CouponTypeEnum.getByCode(getType());
    //
    //
    // }
    //
    // default DeductCouponRule getCouponRule(CouponTypeEnum.Deduction type) {
    //     getCouponRule(CouponTypeEnum.Interest.IDENTITY)
    // }
    //
    // default CashCouponRule getCouponRule(CouponTypeEnum.CashReturn type) {
    //
    // }
}
