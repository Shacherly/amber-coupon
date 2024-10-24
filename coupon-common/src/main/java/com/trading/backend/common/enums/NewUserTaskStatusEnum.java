package com.trading.backend.common.enums;

/**
 * 新手任务状态枚举
 * @author ~~ trading.s
 * @date 14:16 09/26/21
 */
public enum NewUserTaskStatusEnum {

    //-1:不存在 1:待解锁 2:进行中 3:已完成 4:已失效
    NOT_EXITS(-1, "不存在"),

    STAY_LOCK(1, "待解锁"),

    UNDER_WAY(2, "进行中"),

    COMPLETED(3, "已完成"),

    INVALID(4, "已失效"),

    ;
    private Integer code;

    private String status;

    NewUserTaskStatusEnum(Integer code, String descr) {
        this.code = code;
        this.status = descr;
    }

    public Integer getCode() {
        return code;
    }
}
