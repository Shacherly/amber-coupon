package com.trading.backend.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.trading.backend.client.ISymbolServiceApi;
import com.trading.backend.config.RemoteServerProperty;
import com.trading.backend.http.Response;
import com.trading.backend.util.RemoteCaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.groovy.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * @author ~~ trading.s
 * @date 16:56 10/15/21
 */
@Service @Slf4j
public class SymbolServiceApiImpl implements ISymbolServiceApi {


    @Autowired
    private RemoteCaller caller;
    @Autowired
    private RemoteServerProperty serverProperty;
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public BigDecimal exchange(String originCoin, BigDecimal amount) {
        String coinPair = originCoin.toUpperCase() + "_USD";
        Map<String, List<String>> body = new HashMap<>();
        body.put("symbols", Lists.newArrayList(coinPair));
        String url = serverProperty.getDomain() + serverProperty.getCrexServer().getPrice();
        Response<Map<String, String>> response = caller.postForEntity(url, body, null, new TypeReference<Response<Map<String, String>>>() {});
        return new BigDecimal(response.getData().get(coinPair)).multiply(amount).setScale(8, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal exchange(String originCoin, String amount) {
        return exchange(originCoin, new BigDecimal(amount));
    }

    @Override
    public Map<String, BigDecimal> getPrices() {
        Map<String, List<String>> body = Maps.of("symbols", Collections.emptyList());
        String url = serverProperty.getDomain() + serverProperty.getCrexServer().getPrice();
        Response<Map<String, String>> mapResponse = caller.postForEntity(url, body, null, new TypeReference<Response<Map<String, String>>>() {});
        Map<String, String> data = mapResponse.getData();
        Map<String, BigDecimal> result = new HashMap<>(data.size() * 2);
        data.forEach((k, v) -> result.put(k, new BigDecimal(String.valueOf(v))));
        result.put("USD_USD", BigDecimal.ONE);
        return result;
    }

    @Override
    public BigDecimal getIndexPrice(String coin) {

        //返回结果
        String coinParam = String.format("%s_%s",coin.toUpperCase(),"USD");

        //构建参数
        Map<String,Object> param = new HashMap<>();
        Set<String> queryCoin = new HashSet<>();
        queryCoin.add(coinParam);
        param.put("symbols", queryCoin);

        ResponseEntity<String> entity = restTemplate.postForEntity(serverProperty.getDomain() + serverProperty.getCrexServer().getIndexPrice() , param, String.class);
        if(entity.getStatusCode().equals(HttpStatus.OK)){
            JSONObject reponse = JSONObject.parseObject(entity.getBody());
            if ("0".equals(reponse.get("code").toString())){
                JSONObject res = reponse.getJSONObject("data");
                if (res != null){
                    //遍历保存每个币种的汇率
                    if (res.get(coinParam) != null){
                        return new BigDecimal(res.getString(coinParam));
                    }
                }
            }
        }

        return BigDecimal.ZERO;
    }
}
