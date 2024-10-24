package com.trading.backend.kafka.message;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author ~~ trading.s
 * @date 12:45 10/15/21
 */
@Getter @Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class EarnConsumeModel extends AbstractConsumerModel implements Serializable {

    private static final long serialVersionUID = -7817043892107749264L;


    private String uid;


    private String orderId;


    private String positionId;


    private String holdingCoin;


    private String holdingSize;


    private Integer subscrPeriod;

    // 1申购  2提前赎回  3到期赎回
    private Integer earnBusinessType;


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("EarnConsumeModel{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", orderId='").append(orderId).append('\'');
        sb.append(", positionId='").append(positionId).append('\'');
        sb.append(", holdingCoin='").append(holdingCoin).append('\'');
        sb.append(", holdingSize='").append(holdingSize).append('\'');
        sb.append(", subscrPeriod=").append(subscrPeriod);
        sb.append(", earnBusinessType=").append(earnBusinessType);
        sb.append('}');
        return sb.toString();
    }

    public static EarnConsumeModel mockRedeem(String uid, String positionId) {
        return new EarnConsumeModel(uid, null, positionId, "mock_redeem", "0", 0, 2);
    }
}
