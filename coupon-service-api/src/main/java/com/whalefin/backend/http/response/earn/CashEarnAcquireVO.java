package com.trading.backend.http.response.earn;


import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;


/**
 * @author ~~ trading.s
 * @date 12:16 10/16/21
 */
@Data @Accessors(chain = true)
public class CashEarnAcquireVO implements Serializable {

    private static final long serialVersionUID = -6945099988971104096L;

    private String position_id;

    private Long create_time;

    public CashEarnAcquireVO(String position_id, Long create_time) {
        this.position_id = position_id;
        this.create_time = create_time;
    }

    public CashEarnAcquireVO() {
    }

    public static CashEarnAcquireVO mock() {
        return new CashEarnAcquireVO("19000", Instant.now().toEpochMilli());
    }
}
