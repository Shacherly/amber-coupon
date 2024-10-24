package com.trading.backend.common.enums;


import lombok.Getter;


/**
 * @author ~~ trading.s
 * @date 12:04 10/16/21
 */
@Getter
public enum CouponStatusEnum {

    NOT_ENOUGH((short) -2, "UNAVAILABLE CAUSE NOT QUANTITY ENOUGH"),

    EAPIRED((short) -1, "EAPIRED"),

    DISABLED((short) 0, "DISABLED"),

    ENABLED((short) 1, "ENABLED"),

    ;
    private Short status;

    private String descr;


    CouponStatusEnum(Short status, String descr) {
        this.status = status;
        this.descr = descr;
    }

    public static CouponStatusEnum getByCode(Short code) {
        CouponStatusEnum[] values = CouponStatusEnum.values();
        for (CouponStatusEnum applyEnum : values) {
            if (applyEnum.status.equals(code)) {
                return applyEnum;
            }
        }
        throw new IllegalArgumentException("Illegal coupon status code of " + code);
    }
}
