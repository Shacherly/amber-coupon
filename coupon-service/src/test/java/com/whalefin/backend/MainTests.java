package com.trading.backend;


import cn.hutool.core.map.MapBuilder;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.trading.backend.http.request.CouponCreateParam;
import com.trading.backend.domain.CommonCoinRule;
import com.trading.backend.domain.InterCouponRule;

import java.util.HashMap;

public class MainTests {

    public static void main(String[] args) {
        MapBuilder<String, String> builder = new MapBuilder<>(new HashMap<>());
        builder.put("zh_CN", "加息券呢呢").put("en_US", "Interest Coupon66");

        MapBuilder<String, String> builder1 = new MapBuilder<>(new HashMap<>());
        builder1.put("zh_CN", "我擦").put("en_US", "I like");

        CommonCoinRule coinRule1 = new CommonCoinRule();
        coinRule1.setApplyCoin("btc").setMaxAmount("100").setMinAmount("10");
        CommonCoinRule coinRule2 = new CommonCoinRule();
        coinRule2.setApplyCoin("usd").setMaxAmount("100").setMinAmount("10");
        CommonCoinRule coinRule3 = new CommonCoinRule();
        coinRule3.setApplyCoin("eth").setMaxAmount("100").setMinAmount("10");
        InterCouponRule interCouponRule = new InterCouponRule();
        interCouponRule.setInterDays(10).setMinSubscrDays(10)
                       .setMaxSubscrDays(15).setCoinRules(Lists.newArrayList(coinRule1, coinRule2, coinRule3));

        CouponCreateParam param = new CouponCreateParam();
        param.setCouponName("test2")
             .setMultiLanTitle(builder.build())
             .setMultiLanDesc(builder1.build())
             .setType(0)
             .setStatus((short) 0)
             .setOverlay(false)
             .setTotal(100L)
             .setPossessLimit(1)
             .setRedirectUrl("com.baidu.www")
             .setExprInDays(10)
             .setExprAtStart(null)
             .setExprAtEnd(null)
             .setWorthCoin(null)
             .setWorth("0.001")
             .setApplyScene(0)
             .setInterCouponRule(interCouponRule);
             // .setCouponRule(JSON.toJSONString(interCouponRule));
        System.out.println("=======");
        System.out.println("=======");
        System.out.println("=======");
        System.out.println(JSONObject.toJSONString(param, true));
        // System.out.println(JSONObject.toJSONString(param, true));
        System.out.println("=======");
        System.out.println("=======");
        System.out.println("=======");

        System.out.println(100_000_000 == 100000000);

        String sss = "{\"6073ae91fd90f269704dda4b\":\"67.02\",\"60efefa70556b7325e615174\":\"8.31\",\"60a1ade9fb546e75de5d9598\":\"0.01\",\"5ebb5978b4b4e935593c84cf\":\"0.00\",\"609ed1674fcdcc59cfe37e50\":\"2.13\",\"5ea7eb451c3b8438328b6966\":\"3.49\",\"602df1f2c1e268296ed88eb5\":\"317.46\",\"60e39fdc3a56c62765561d08\":\"2.13\",\"60cfd8bcd818e660ca4eebd4\":\"0.94\",\"5ed28050ef461d3faba48526\":\"0.27\",\"612cf9eb6b54f2ad19a55f40\":\"0.11\",\"60f528906f4f4046820be22e\":\"18.17\",\"6110086841975d74d6df36cb\":\"71.48\",\"60a710385111733e58f19078\":\"15.44\"}";

    }

    public static void test() {
        String s = "{\"inter_days\":10,\"min_subscr_days\":10,\"max_subscr_days\":15,\"coin_rules\":[{\"apply_coin\":\"btc\",\"min_amount\":10.0000000000000000,\"max_amount\":100.0000000000000000},{\"apply_coin\":\"usd\",\"min_amount\":10.0000000000000000,\"max_amount\":100.0000000000000000},{\"apply_coin\":\"eth\",\"min_amount\":10.0000000000000000,\"max_amount\":100.0000000000000000}]}";

    }
}
