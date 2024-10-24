package com.trading.backend.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.LoadingCache;
import com.trading.backend.client.ICoinServiceApi;
import com.trading.backend.config.RemoteServerProperty;
import com.trading.backend.util.RemoteCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;


/**
 * @author ~~ trading.s
 * @date 12:50 11/17/21
 */
@Slf4j
@Service
public class CoinServiceApiImpl implements ICoinServiceApi {

    @Autowired
    private RemoteCaller caller;
    @Autowired
    private RemoteServerProperty remoteProperty;
    @Autowired
    private LoadingCache<String, Set<String>> coinsCache;

    @Override
    public Set<String> cacheSupportCoins() {
        JSONObject jsonObject = caller.get4JSONObject(
                remoteProperty.getDomain() + remoteProperty.getCommonConfig().getDividedCoin(),
                Collections.singletonMap("origin_channel", "BACKEND"),
                Collections.singletonMap("type", "common")
        );
        Set<String> coins = jsonObject.keySet();
        coinsCache.put("SUPPORTED_COINS", coins);
        return coins;
    }
}
