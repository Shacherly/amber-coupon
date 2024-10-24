package com.trading.backend.http.response.aceup;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author ：trading mu
 * @date ：Created in 2021/10/16 17:34
 * @description：
 * @modified By：
 */

@Data
public class IssueListVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "姓名")
    private String username;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "领取途径 0新用户注册领券，1运营活动发券，2会员权益礼券，101用户主动领取")
    private Integer source;

    @ApiModelProperty(value = "优惠券id")
    private Long couponId;

    @ApiModelProperty(value = "优惠券类型")
    private Integer couponType;

    @ApiModelProperty(value = "优惠券名称")
    private String couponName;

    @ApiModelProperty(value = "激活条件 1理财加息，11借贷减息，51KYC返现，52入金返现，53理财返现")
    private Integer applyScene;

    @ApiModelProperty(value = "具体券的业务状态（对应按钮状态）：初始状态默认0待使用</br>" +
            "->加息券(0待使用1已过期2已使用3已禁用)；<br>" +
            "->资产券(0待使用1已过期2待发放3已禁用4发放失败6已发放)；<br>" +
            "->减息券(0待使用1已过期2已使用3已禁用)")
    private Integer businessStage;

    @ApiModelProperty(value = "生效时间")
    private LocalDateTime usableTime;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime exprTime;

    @ApiModelProperty(value = "领取时间")
    private LocalDateTime ctime;

    @ApiModelProperty(value = "使用时间")
    private LocalDateTime consumeTime;
}
