package com.trading.backend.pojo;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.util.Converter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CashActivDO {

    @ApiModelProperty(value = "券id")
    @JSONField(name = "coupon_id")
    private Long couponId;

    @ApiModelProperty(value = "持券记录id")
    @JSONField(name = "possess_id")
    private Long possessId;

    @ApiModelProperty(value = "券规则配置")
    @JSONField(name = "coupon_rule")
    private JSONObject couponRule;

    @ApiModelProperty(value = "适用场景")
    @JSONField(name = "apply_scene")
    private Integer applyScene;

    @ApiModelProperty(value = "按钮状态")
    @JSONField(name = "business_stage")
    private Integer businessStage;

    @ApiModelProperty(value = "业务id（持仓id）")
    @JSONField(name = "business_id")
    private String businessId;

    @JSONField(name = "expr_time")
    private LocalDateTime exprTime;

    private CashCouponRule cashActivRule;

    public CashCouponRule getCashActivRule() {
        if (CollectionUtil.isEmpty(couponRule)) return null;
        return (CashCouponRule) Converter.deserialize(couponRule, CouponTypeEnum.CASHRETURN_TYPE.getCode());
    }

}
