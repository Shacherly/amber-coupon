package com.trading.backend.kafka.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Getter @Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class KycStageModel extends AbstractConsumerModel implements Serializable {
    private static final long serialVersionUID = 8897576774191708794L;

    private String uid;

    // -1kyc拒绝  1kyc提交 2kyc通过
    private Integer stage;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KycStageModel{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", stage=").append(stage);
        sb.append('}');
        return sb.toString();
    }

    public static KycStageModel kycPassMock(String uid) {
        return new KycStageModel(uid, 2);
    }
}
