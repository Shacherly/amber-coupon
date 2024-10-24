package com.trading.backend.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.trading.backend.client.IDualServiceApi;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.exception.VisibleException;
import com.trading.backend.util.RemoteCaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.groovy.util.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service @Slf4j
public class DualServiceApiImpl implements IDualServiceApi {

    @Autowired
    private RemoteCaller remoteCaller;
    @Value("${remote-call.dual-server.trial-allow}")
    private String trialAllow;
    @Value("${remote-call.domain}")
    private String domain;

    @Override
    public void trialAllow(String uid) {
        JSONObject jsonObject = remoteCaller.postForJSONObject(
                domain + trialAllow,
                Maps.of("uid", uid),
                null
        );
        if (jsonObject.getBoolean("allow")) return;
        throw new VisibleException(ExceptionEnum.ILLEGAL_REQUET, jsonObject.getString("msg"));
    }
}
