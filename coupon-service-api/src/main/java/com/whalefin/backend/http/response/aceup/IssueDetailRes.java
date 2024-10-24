package com.trading.backend.http.response.aceup;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

/**
 * @author ：trading mu
 * @date ：Created in 2021/10/18 11:11
 * @description：发放明细详情
 * @modified By：
 */

@Data
@ApiModel(value = "发放明细返回实体")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IssueDetailRes {

    @ApiModelProperty(value = "领取途径 0新用户注册领券，1运营活动发券，2会员权益礼券，101用户主动领取")
    private Integer source;

    @ApiModelProperty(value = "优惠券id")
    private Long couponId;

    @ApiModelProperty(value = "优惠券类型（0：加息券 1：减息券 5：资产券）")
    private Integer couponType;

    @ApiModelProperty(value = "优惠券名称")
    private String couponName;

    @NotEmpty(message = "multi_lan_title must not be null !")
    @ApiModelProperty(value = "优惠券标题（根据当前语言选择展示）", required = true)
    private Map<String, String> multiLanTitle;

    @ApiModelProperty(value = "状态(0待使用1过期2被回收3被禁用4已使用)")
    private Integer possessStage;

    @ApiModelProperty(value = "每人限领数量", required = true)
    @JSONField(name = "possess_limit")
    private Integer possessLimit;

    @ApiModelProperty(value = "固定天数过期（天数和日期二选一，只会存在一个）")
    private Integer exprInDays;

    @ApiModelProperty(value = "固定起止日期过期-起始")
    private Long exprAtStart;

    @ApiModelProperty(value = "固定起止日期过期-结束")
    private Long exprAtEnd;

    @ApiModelProperty(value = "跳转地址")
    @JSONField(name = "redirect_url")
    private String redirectUrl;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "失效时间")
    private Long exprTime;

    @ApiModelProperty(value = "领取时间")
    private Long ctime;

    @ApiModelProperty(value = "使用时间")
    private Long consumeTime;

    @ApiModelProperty(value = "加息券规则配置，根据券的类型传不同的配置字段，选其一")
    private InterCouponRule interCouponRule;

    @ApiModelProperty(value = "资产券规则配置，根据券的类型传不同的配置字段，选其一")
    private CashCouponRule cashCouponRule;

    @ApiModelProperty(value = "减息券规则配置，根据券的类型传不同的配置字段，选其一")
    private DeductCouponRule deductCouponRule;


}
