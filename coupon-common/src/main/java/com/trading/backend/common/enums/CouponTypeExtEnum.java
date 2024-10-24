package com.trading.backend.common.enums;


/**
 * @author ~~ trading.s
 * @date 16:58 08/20/21
 */
public enum CouponTypeExtEnum {

    /**
     * 优惠券扩展类型基数
     */
    EXT_BASE_DIVISOR(100),

    /**
     * 常规券
     */
    ROUTINE(0),

    /**
     * 审批券
     */
    APPROVAL(1),
    ;


    private Integer code;


    CouponTypeExtEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    /**
     * 获取扩展类型
     * @param typeCode
     * @return 0-常规券 1-审批券
     */
    public static Integer getCouponExtType(int typeCode) {
        return typeCode / EXT_BASE_DIVISOR.code;
    }

    /**
     * 获取基本类型
     * @param typeCode
     * @return CouponTypeEnum.code
     */
    public static Integer getCouponOriginType(int typeCode) {
        return typeCode % EXT_BASE_DIVISOR.code;
    }

    public static boolean isRoutineType(int typeCode) {
        return getCouponExtType(typeCode).equals(ROUTINE.code);
    }

    public static boolean isApprovalType(int typeCode) {
        return getCouponExtType(typeCode).equals(APPROVAL.code);
    }

    public static CouponTypeExtEnum getByCode(Integer code){
        CouponTypeExtEnum[] values = CouponTypeExtEnum.values();
        for (CouponTypeExtEnum typeEnum : values){
            if (typeEnum.code.equals(code)){
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("Illegal code : " + code);
    }
}
