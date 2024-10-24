package com.trading.backend.common.enums;


/**
 * @author ~~ trading.s
 * @date 16:59 09/24/21
 */
public enum CouponTypeEnum {

    /**
     * 加息券
     */
    INTEREST_TYPE("INTEREST COUPON", 0),

    /**
     * 减息券
     */
    DEDUCTION_TYPE("DEDUCTION COUPON", 1),

    /**
     * 收益增强券
     */
    PROFITINCRE_TYPE("PROFITINCRE COUPON", 2),

    /**
     * 体验金券
     */
    TRIALFUND_TYPE("TRIALFUND COUPON", 3),


    /**
     * 资产券（返现券）
     */
    CASHRETURN_TYPE("CASHRETURN COUPON", 5),


    ;


    private Integer code;

    private String descr;

    CouponTypeEnum(String descr, Integer code) {
        this.code = code;
        this.descr = descr;
    }

    public static CouponTypeEnum getByCode(Integer code) {
        CouponTypeEnum[] values = CouponTypeEnum.values();
        for (CouponTypeEnum typeEnum : values) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("Illegal coupon type code of " + code);
    }

    public Integer getCode() {
        return code;
    }

    public String getDescr() {
        return descr;
    }


    public static void main(String[] args) {
    }

}
