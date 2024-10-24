package com.trading.backend.kafka.message;


import java.io.Serializable;

public abstract class AbstractConsumerModel implements Serializable {
    private static final long serialVersionUID = 8649110307747637912L;

    public abstract String getUid();
}
