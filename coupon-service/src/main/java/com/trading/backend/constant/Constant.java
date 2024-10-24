package com.trading.backend.constant;


import java.math.BigDecimal;

/**
 * @author ~~ trading.s
 * @date 17:05 09/22/21
 */
public interface Constant {

    String TRACE_ID = "traceId";

    String REQUEST_ID = "requestId";

    String HEADER_REQUEST_ID = "x-gw-requestid";

    String HEADER_USER = "x-gw-user";

    String HEADER_USER_ID = "user_id";

    String CURRENT_ENV = "currentEnv";

    String CLIENT_HEADER = "CLIENT_HEADER";

    String SRC = "src";
    String SRC_APP = "app";
    String SRC_PRO = "pro";

    String LANG_EN_US = "en-US";


    BigDecimal CASH_GRANT_CELLING_4000 = new BigDecimal("4000");

    BigDecimal CASH_GRANT_CELLING_2000 = new BigDecimal("4000");

}
