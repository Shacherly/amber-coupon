package com.trading.backend.common.enums;


import lombok.Getter;

/**
 * @author ~~ trading.s
 * @date 14:45 09/26/21
 */
@Getter
public enum PossessEventEnum {

    NEW_REGISTER(0),

    FIRST_DEPOSIT(1),

    FIRST_TRADE(2),

    FIRST_EARN(3),

    INVITE_USER(4),

    CLUB_GIFT(5),
    ;

    private int code;

    private String descr;


    PossessEventEnum(int code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    PossessEventEnum(int code) {
        this.code = code;
    }
}
