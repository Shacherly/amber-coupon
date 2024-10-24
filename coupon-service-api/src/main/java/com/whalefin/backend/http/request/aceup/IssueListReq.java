package com.trading.backend.http.request.aceup;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ：trading mu
 * @date ：Created in 2021/10/16 17:00
 * @description：发放明细请求实体
 * @modified By：
 */

@Getter
@Setter
@ApiModel(value = "发放明细请求实体")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class IssueListReq implements Serializable {

    private static final long serialVersionUID = -1952645394449770230L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "优惠券id")
    private Long coupon_id;

    @ApiModelProperty(value = "优惠券名称")
    private String coupon_name;

    @ApiModelProperty(value = "优惠券类型 0加息券，1减息券，5资产券")
    private Integer coupon_type;

    @ApiModelProperty(value = "领取途径 0新用户注册领券，1运营活动发券，2会员权益礼券，101用户主动领取")
    private Integer source;

    @ApiModelProperty(value = "激活条件 1理财加息，11借贷减息，51KYC返现，52入金返现，53理财返现")
    private Integer apply_scene;

    @ApiModelProperty(value = "具体券的业务状态（对应按钮状态）：初始状态默认0待使用</br>" +
            "->加息券(0待使用1已过期2已使用3已禁用)；<br>" +
            "->资产券(0待使用1已过期2待发放3已禁用4发放失败6已发放)；<br>" +
            "->减息券(0待使用1已过期2已使用3已禁用)")
    private Integer business_stage;

    @ApiModelProperty(value = "生效时间-开始")
    private Long usable_time_begin;

    @ApiModelProperty(value = "生效时间-结束")
    private Long usable_time_end;

    @ApiModelProperty(value = "失效时间-开始")
    private Long expr_time_begin;

    @ApiModelProperty(value = "失效时间-结束")
    private Long expr_time_end;

    @ApiModelProperty(value = "领取时间-开始")
    private Long c_time_begin;

    @ApiModelProperty(value = "领取时间-结束")
    private Long c_time_end;

    @ApiModelProperty(value = "使用时间-开始")
    private Long consume_time_begin;

    @ApiModelProperty(value = "使用时间-结束")
    private Long consume_time_end;


}
