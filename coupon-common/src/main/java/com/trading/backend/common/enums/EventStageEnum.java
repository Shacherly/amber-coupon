package com.trading.backend.common.enums;


import lombok.Getter;

@Getter
public enum EventStageEnum {

    PRE_START(0),

    RUNNING(1),

    PARTLY_GRANTED(2),

    ALL_GRANTED(3),

    ALL_FAILED(4),

    ;


    private Integer stage;

    EventStageEnum(Integer stage) {
        this.stage = stage;
    }
}
