package com.trading.backend.bo;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.base.CouponRuleBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
 * @author ~~ trading.s
 * @date 14:13 09/23/21
 */
@Getter
@Setter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PossessBO {

    private static final long serialVersionUID = 6860638447425627525L;

    @ApiModelProperty(value = "券id")
    private Long couponId;

    @ApiModelProperty(value = "持券记录id")
    private Long possessId;

    @ApiModelProperty(value = "标题")
    private String couponTitle;

    @ApiModelProperty(value = "描述")
    private String descr;

    @ApiModelProperty(value = "名称")
    private String couponName;

    @ApiModelProperty(value = "券规则配置")
    private CouponRuleBase couponRule;

    @ApiModelProperty(value = "券类型")
    private Integer couponType;

    @ApiModelProperty(value = "适用场景")
    private Integer applyScene;

    @ApiModelProperty(value = "持券状态")
    private Integer possessStage;

    @ApiModelProperty(value = "按钮状态")
    private Integer businessStage;

    @ApiModelProperty(value = "领取时间")
    private LocalDateTime receiveTime;

    @ApiModelProperty(value = "有效起点时刻")
    private LocalDateTime availableStart;

    @ApiModelProperty(value = "有效结束时刻")
    private LocalDateTime availableEnd;

    @ApiModelProperty(value = "使用时刻")
    private LocalDateTime consumeTime;

    @ApiModelProperty(value = "预计生效结束时间")
    private LocalDateTime exptEndTime;

    @ApiModelProperty(value = "是否支持叠加使用")
    private boolean overlay;

    @ApiModelProperty(value = "红点是否已读")
    private boolean hasRead;

    // public <T> T getCouponRule(T type) {
    //     return (T) couponRule;
    // }
}
