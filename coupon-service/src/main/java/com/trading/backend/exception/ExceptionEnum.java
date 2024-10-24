package com.trading.backend.exception;


import lombok.Getter;

/**
 * @author ~~ trading.s
 * @date 16:46 09/22/21
 * @desc 错误码长度8位：{2位模块错误码前缀，10-20预留，使用20-99}{6位业务自定义唯一，100xxx作为不同模块通用错误码}
 */
@Getter
public enum ExceptionEnum {

    ARGUMENT_UNVALID(32_999_999, "Unvalid args present"),

    ARGUMENT_NULL(32_999_998, "Null args [{0}] is Illegal"),

    KYC_DINED(32_999_997, "Uid of {0} kyc has not been certified"),

    SCHEDULE_ERROR(32_999_996, "Schedule task error, cause by {0}"),

    INNERNAL_ERROR(32_100_000, "Internal server error"),

    CASHRETURN_GRANT_CELLING(32_100_001, "Cashreturn will reach daily celling of {0} cause by possess_id {1}, paused reward unitl tomorrow"),

    COUPON_TOTAL_INVALID(32_100_002, "coupon total greater than issue"),

    INSERT_ERROR(32_200_001, "Insert database records error"),

    UPDATE_ERROR(32_200_002, "Update records error"),

    POSSESS_DISABLE(32_300_001, "Possess stage unusable"),

    COUPON_RUN_OUT(32_300_002, "Coupon id of {0} has been run out"),

    COUPON_DISABLE(32_300_003, "Coupon id of {0} are in status of disable so you can do no operation"),

    COUPON_NOT_PRESENT(32_300_004, "Coupon id of {0} are not present"),

    CLUB_GIFT_UNREPEATABLE(32_300_005, "Club gift can't be received repeatedly"),

    COUPON_PARTLY_RECEIVED(32_300_006, "Coupon partly received, cause id of {0} are not present"),

    POSSESS_EXCEED_LIMIT(32_300_007, "User {0} possess of coupon_id {1} has reached limit"),

    COUPON_REST_LACK(32_300_008, "Lack of remaining quantity of coupon id {0} for so many users"),

    COUPON_PRELOCK_LACK(32_300_012, "Lack of remaining quantity of coupon id {0} to prelock for approval process"),

    CASH_COUPON_UPDATE_ACTIVATE_FAILED(32_300_009, "Update cash coupon activate failed"),

    POSSESS_EXPIRED_CAUSEBY_REDEEM(32_300_010, "Possess expired cause by redeeming of pos_id {0}"),

    POSSESS_RESET_CAUSEBY_REDEEM(32_300_011, "Possess reset cause by redeeming of pos_id {0}"),

    REMOTE_SERVER_ERROR(32_400_001, "Remote server error, code: {0}, reason: {1}."),

    TASK_COUPON_IS_EMPTY(32_400_002, "task coupon is empty"),

    COUPON_NOT_SUPPORT_MODIFY(32_400_003, "coupon not support modify"),

    COUPON_IN_PROCESS_APPROVAL(32_400_005, "Coupon_id {0} have been in process of approvaling"),

    UUNSUPPORTED_COIN(32_400_006, "Coin {0} is not supported"),

    ILLEGAL_INTER_PERIOD(32_400_007, "Interest duration musn't be greater than subscribe days floor"),

    ILLEGAL_REQUET(32_400_008, "Illegal request, {0}"),

    IMPROPER_COUPON(32_400_009, "Improper type of coupon is denied!"),

    TEMPLATE_EXIST(32_500_001, "Coupon descr template of apply scene {0} has exist!"),
    ;


    private final int code;

    private final String reason;

    ExceptionEnum(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }
}
