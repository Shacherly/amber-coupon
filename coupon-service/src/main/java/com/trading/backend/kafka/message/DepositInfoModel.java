package com.trading.backend.kafka.message;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter @Setter
@Accessors(chain = true)
public class DepositInfoModel extends AbstractConsumerModel implements Serializable {

    private static final long serialVersionUID = -7493672868864250573L;

    private String uid;

    private String depositCoin;

    private String arrivedAmount;

    /**
     * 充值类型
     * OnChain站外链上充值:     1,
     * BankWire站外法币充值:    2,
     *
     * Internal内部用户转账:    0,
     * 站内FinancialOfficer:  3,
     * 站内Manual:            4，
     * 线上买币               5
     */
    private Integer transactionType;

    private LocalDateTime sendTime;

    public boolean external() {
        return transactionType == 1 || transactionType == 2 || transactionType == 5;
    }

    public boolean internal() {
        return !external();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DepositInfoModel{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", depositCoin='").append(depositCoin).append('\'');
        sb.append(", arrivedAmount='").append(arrivedAmount).append('\'');
        sb.append(", transactionType=").append(transactionType);
        sb.append(", sendTime=").append(sendTime);
        sb.append('}');
        return sb.toString();
    }

    public static DepositInfoModel depositMock(String uid) {
        DepositInfoModel model = new DepositInfoModel().setUid(uid);
        model.setDepositCoin("USD");
        model.setArrivedAmount("1000");
        model.setTransactionType(1);
        return model;
    }
}
