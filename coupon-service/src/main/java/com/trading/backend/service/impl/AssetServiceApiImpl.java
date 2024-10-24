package com.trading.backend.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.trading.backend.client.IAssetServiceApi;
import com.trading.backend.config.RemoteServerProperty;
import com.trading.backend.util.RemoteCaller;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


/**
 * @author ~~ trading.s
 * @date 16:17 10/16/21
 */
@Slf4j
@Service
public class AssetServiceApiImpl implements IAssetServiceApi {

    @Autowired
    private RemoteCaller remoteCaller;
    @Autowired
    private RemoteServerProperty serverProperty;

    @Override
    public boolean activityDeposit(String businessId, String uid, String amount, String coin) {
        Map<String, String> param = new HashMap<>();
        param.put("uid", uid);
        param.put("amount", amount);
        param.put("coin", coin);
        param.put("type", "coupon");
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("params", param);
        wrapper.put("req_id", businessId + "-" + uid);

        String url = serverProperty.getDomain() + serverProperty.getAssetServer().getActivityDeposit();
        JSONObject jsonObject = remoteCaller.post4OriginResponse(url, wrapper, null);
        String msg = jsonObject.getString("msg");
        if (StringUtils.equals(msg, "OK")) {
            log.info("CouponDeposit Success on {}", wrapper);
            return true;
        }
        log.error("CouponDeposit Failed for reason {}, on {}", msg, wrapper);
        return false;
    }
}
