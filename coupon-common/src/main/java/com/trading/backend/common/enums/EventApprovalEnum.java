package com.trading.backend.common.enums;


import lombok.Getter;

@Getter
public enum EventApprovalEnum {


    UNCOMMITTED(0),

    PENGIND(1),

    DENIED(2),

    APPROVALED(3),

    ;

    private Integer stage;

    EventApprovalEnum(Integer stage) {
        this.stage = stage;
    }


    public static EventApprovalEnum getByStage(Integer stage) {
        EventApprovalEnum[] values = EventApprovalEnum.values();
        for (EventApprovalEnum typeEnum : values) {
            if (typeEnum.stage.equals(stage)) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("Illegal EventApprovalEnum stage of " + stage);
    }
}
