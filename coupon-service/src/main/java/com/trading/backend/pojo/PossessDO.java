package com.trading.backend.pojo;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.trading.backend.domain.base.CouponAdaptable;
import com.trading.backend.domain.base.CouponMultiLocaliable;
import com.trading.backend.domain.base.PossessInspectable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * @author ~~ trading.s
 * @date 14:13 09/23/21
 */
@Getter
@Setter
@Accessors(chain = true)
public class PossessDO implements Serializable, CouponMultiLocaliable, CouponAdaptable, PossessInspectable {

    @ApiModelProperty(value = "用户id")
    @JSONField(name = "uid")
    private String uid;

    @ApiModelProperty(value = "券id")
    @JSONField(name = "coupon_id")
    private Long couponId;

    @ApiModelProperty(value = "持券记录id")
    @JSONField(name = "possess_id")
    private Long possessId;

    @ApiModelProperty(value = "标题")
    @JSONField(name = "coupon_title")
    private String couponTitle;

    @ApiModelProperty(value = "描述")
    @JSONField(name = "coupon_descr")
    private String couponDescr;

    @ApiModelProperty(value = "名称")
    @JSONField(name = "coupon_name")
    private String couponName;

    @ApiModelProperty(value = "券规则配置")
    @JSONField(name = "coupon_rule")
    private JSONObject couponRule;

    @ApiModelProperty(value = "券类型")
    @JSONField(name = "coupon_type")
    private Integer couponType;

    @ApiModelProperty(value = "适用场景")
    @JSONField(name = "apply_scene")
    private Integer applyScene;

    @ApiModelProperty(value = "价值")
    @JSONField(name = "worth")
    private BigDecimal worth;

    @ApiModelProperty(value = "价值币种")
    @JSONField(name = "worth_coin")
    private String worthCoin;

    @ApiModelProperty(value = "持券状态")
    @JSONField(name = "possess_stage")
    private Integer possessStage;

    @ApiModelProperty(value = "业务id")
    @JSONField(name = "business_id")
    private String businessId;

    @ApiModelProperty(value = "按钮状态")
    @JSONField(name = "business_stage")
    private Integer businessStage;

    @ApiModelProperty(value = "领取时间")
    @JSONField(name = "receive_time")
    private LocalDateTime receiveTime;

    @ApiModelProperty(value = "有效起点时刻")
    @JSONField(name = "available_start")
    private LocalDateTime availableStart;

    @ApiModelProperty(value = "有效结束时刻")
    @JSONField(name = "available_end")
    private LocalDateTime availableEnd;

    @ApiModelProperty(value = "使用时刻")
    @JSONField(name = "consume_time")
    private LocalDateTime consumeTime;

    @ApiModelProperty(value = "预计生效结束时间")
    @JSONField(name = "expt_end_time")
    private LocalDateTime exptEndTime;

    @ApiModelProperty(value = "是否支持叠加使用")
    @JSONField(name = "overlay")
    private Boolean overlay;

    @ApiModelProperty(value = "红点是否已读")
    @JSONField(name = "has_read")
    private Boolean hasRead;

    @ApiModelProperty(value = "跳转连接")
    @JSONField(name = "redirect_url")
    private String redirectUrl;

    private static final long serialVersionUID = 6860638447425627525L;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PossessDO{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", couponId=").append(couponId);
        sb.append(", possessId=").append(possessId);
        sb.append(", couponTitle='").append(couponTitle).append('\'');
        sb.append(", couponDescr='").append(couponDescr).append('\'');
        sb.append(", couponName='").append(couponName).append('\'');
        sb.append(", couponRule=").append(couponRule);
        sb.append(", couponType=").append(couponType);
        sb.append(", applyScene=").append(applyScene);
        sb.append(", worth=").append(worth);
        sb.append(", worthCoin='").append(worthCoin).append('\'');
        sb.append(", possessStage=").append(possessStage);
        sb.append(", businessId='").append(businessId).append('\'');
        sb.append(", businessStage=").append(businessStage);
        sb.append(", receiveTime=").append(receiveTime);
        sb.append(", availableStart=").append(availableStart);
        sb.append(", availableEnd=").append(availableEnd);
        sb.append(", consumeTime=").append(consumeTime);
        sb.append(", exptEndTime=").append(exptEndTime);
        sb.append(", overlay=").append(overlay);
        sb.append(", hasRead=").append(hasRead);
        sb.append(", redirectUrl='").append(redirectUrl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
