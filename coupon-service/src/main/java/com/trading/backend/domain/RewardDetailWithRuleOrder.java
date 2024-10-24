package com.trading.backend.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


@Getter @Setter
@Accessors(chain = true)
public class RewardDetailWithRuleOrder extends PfCouponRewardDetail {

    /**
     * 加息减息券需要查询加息减息天数规则配置
     */
    private JSONObject rule;



    private String extOrderId;

    /**
     * 资产券需要查询理财结束时间
     */
    private LocalDateTime exptEndTime;

}