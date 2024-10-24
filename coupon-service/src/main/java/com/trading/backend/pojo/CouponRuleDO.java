package com.trading.backend.pojo;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author ~~ trading.s
 * @date 13:30 10/09/21
 */
@Setter @Getter
@Accessors(chain = true)
public class CouponRuleDO {


    private Long couponId;

    private Integer applyScene;

    private JSONObject couponRule;


}
