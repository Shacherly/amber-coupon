package com.trading.backend.kafka.message;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Getter @Setter
@Accessors(chain = true)
public class UserRegistryModel extends AbstractConsumerModel implements Serializable {

    private static final long serialVersionUID = 1567098261340724170L;

    private String uid;

    private Long registryTime;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserRegistryModel{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", registryTime=").append(registryTime);
        sb.append('}');
        return sb.toString();
    }
}
