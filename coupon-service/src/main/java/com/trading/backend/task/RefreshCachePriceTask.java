package com.trading.backend.task;

import com.trading.backend.annotation.Traceable;
import com.trading.backend.client.ISymbolServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.constant.RedisKey;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author ：trading mu
 * @date ：Created in 2021/11/11 10:57
 * @description：刷新币价缓存任务
 * @modified By：
 */

@Slf4j
@Component
public class RefreshCachePriceTask {

    @Autowired
    private ISymbolServiceApi symbolServiceApi;

    @Autowired
    private RedisService redis;

    @XxlJob("refreshCachePrice") @Traceable
    public void refreshCachePrice() {

        BigDecimal btcPrice = symbolServiceApi.getIndexPrice("BTC");
        if (!Objects.isNull(btcPrice)){
            redis.setCacheObject(RedisKey.BUFFERED_INDEX_PRICE, btcPrice);
        }

    }

}
