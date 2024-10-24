package com.trading.backend.common.enums;


import lombok.Getter;

/**
 * @author ~~ trading.s
 * @date 14:26 09/26/21
 */
@Getter
public enum PossessSourceEnum {

    NEW_REGISTER(0, "NEW_REGISTER"),

    EVENTS_GRANT(1, "EVENTS_GRANT"),

    CLUB_GIFT(2, "CLUB_GIFT"),

    REFERRAL_REWARD(3, "REFERRAL_REWARD"),

    OLDUSER_REDEEM(4, "OLDUSER_REDEEM"),

    SERVICE_UPGRADE(5, "SERVICE_UPGRADE"),

    DUAL_REWARD(6, "DUAL_REWARD"),

    BWC_GIFT(7, "BWC_GIFT"),

    BWC_APPROVAL_COUPON(8, "BWC_APPROVAL_COUPON"),

    EASTER_PRIZE(9, "EASTER_PRIZE"),

    INITIATIVE_RECEIVE(101, "INITIATIVE_RECEIVE"),
    ;


    private int code;

    private String descr;

    PossessSourceEnum(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }


}
