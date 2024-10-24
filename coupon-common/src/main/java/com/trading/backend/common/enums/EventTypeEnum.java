package com.trading.backend.common.enums;


import lombok.Getter;

@Getter
public enum EventTypeEnum {

    APPROVAL(0),

    MANUAL(1),

    THEMATIC(2),

    BWC_APPROVAL(3),


    ;


    private Integer type;

    EventTypeEnum(Integer type) {
        this.type = type;
    }


    public static boolean approval(int typeCode) {
        return typeCode == APPROVAL.type || typeCode == BWC_APPROVAL.type;
    }
}
