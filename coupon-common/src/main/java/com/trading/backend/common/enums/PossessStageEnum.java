package com.trading.backend.common.enums;


import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Set;

/**
 * @author ~~ trading.s
 * @date 14:16 09/26/21
 */
@Getter
public enum PossessStageEnum {


    /**
     * 预发放持券，非有效持券，不可用、不可用
     */
    PRE_POSSESS(-2, "Possess status of pre grant"),

    /**
     * 错误持券，被撤回、禁用、删除的持券，不可用、不可见
     */
    DIRTY_POSSESS(-1, "Possess status of a mistake"),


    ENABLE(0, "Possess status of usable"),

    // EXPIRED(1, "Unused causes expiration"),

    // REVOCATORY(2, "Revocated by system admin"),

    DISABLE(1, "Possess status of unsable"),

    // USED(4, "Have been used"),

    ;


    private Integer code;

    private String descr;

    PossessStageEnum(Integer code, String descr) {
        this.code = code;
        this.descr = descr;
    }
}
