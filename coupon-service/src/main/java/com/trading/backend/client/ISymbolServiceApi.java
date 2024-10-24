package com.trading.backend.client;

import java.math.BigDecimal;
import java.util.Map;

public interface ISymbolServiceApi {


    BigDecimal exchange(String originCoin, BigDecimal amount);

    BigDecimal exchange(String originCoin, String amount);

    Map<String, BigDecimal> getPrices();

    /**
     * 获取指定币种实时汇率
     * @return
     */
    BigDecimal getIndexPrice(String coin);

}
