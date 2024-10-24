package com.trading.backend.common.enums;


import com.google.common.collect.Sets;
import lombok.Getter;

import java.util.Set;


/**
 * @author ~~ trading.s
 * @date 15:48 10/01/21
 * @desc 加息券的使用 0未使用 1订单申购成功 2加息正常结束 -1提前赎回加息结束
 */
@Getter
public enum PossessBusinesStageEnum {


    /**
     * 旧状态0未使用
     */
    DEFAULT_UNUSED(0, "Unused ref to PossessStageEnum.NEW_ACQUIRED"),

    /**
     * 未使用导致过期
     */
    EXPIRED(1, "Unused causes expiration"),

    /**
     * 旧状态1订单申购成功
     */
    CONTRIBUTING(2, "Working、Contributing、Running"),

    /**
     * 旧状态-1提前赎回加息结束
     */
    BREAK_OFF(4, "Early termination、Activate failed"),

    /**
     * 旧状态2加息正常结束
     */
    COMPLETE(6, "Ending、Mature、Arrival"),
    ;


    private Integer code;

    private String descr;

    PossessBusinesStageEnum(Integer code, String descr) {
        this.code = code;
        this.descr = descr;
    }

    public static Set<Integer> contributeStages() {
        return Sets.newHashSet(CONTRIBUTING.getCode(), BREAK_OFF.getCode(), COMPLETE.getCode());
    }
}
