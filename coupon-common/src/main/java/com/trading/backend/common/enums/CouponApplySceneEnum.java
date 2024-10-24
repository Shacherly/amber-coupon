package com.trading.backend.common.enums;


import lombok.Getter;


/**
 * @author ~~ trading.s
 * @date 16:46 10/01/21
 */
@Getter
public enum CouponApplySceneEnum {


    EARN_INTEREST(1, "earn interest"),


    LOAN_DEDUCT(11, "deduct loan interest"),


    DUAL_PROFIT(21, "dual profit increase"),


    DUAL_TRIAL(31, "dual trial fund"),


    KYC_VERIFY_CASH(51, "kyc pass cash"),


    DEPOSIT_CASH(52, "deposit cash"),


    EARN_CASH(53, "earn cash"),


    ;

    private Integer code;

    private String descr;

    CouponApplySceneEnum(Integer code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    public static CouponApplySceneEnum getByCode(Integer code) {
        CouponApplySceneEnum[] values = CouponApplySceneEnum.values();
        for (CouponApplySceneEnum applyEnum : values) {
            if (applyEnum.code.equals(code)) {
                return applyEnum;
            }
        }
        throw new IllegalArgumentException("Illegal coupon type code of " + code);
    }
}
