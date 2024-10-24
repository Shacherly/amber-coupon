package com.trading.backend.http.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Map;

/**
 * @author ~~ trading.s
 * @date 15:38 06/10/21
 */
@Setter
@Getter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponEditParam {

    @NotNull
    @ApiModelProperty(value = "券id", required = true)
    private Long couponId;

    @Size(max = 100, message = "Length of name is limit 100 !")
    @NotBlank(message = "coupon name can't be null.")
    @ApiModelProperty(value = "券名称", required = true)
    private String couponName;

    @NotEmpty(message = "multi_lan_title must not be null !")
    @ApiModelProperty(value = "多语言标题", required = true)
    private Map<String, String> multiLanTitle;

    @NotEmpty(message = "multi_lan_desc must not be null !")
    @ApiModelProperty(value = "多语言描述", required = true)
    private Map<String, String> multiLanDesc;

    @NotNull(message = "status can't be null.")
    @ApiModelProperty(value = "券状态(1启用，0禁用，-1过期，-2全部发放无可用数量)", required = true)
    private Short status;

    @Size(max = 1024, message = "Length of redirectUrl is limit 1024 !")
    @ApiModelProperty(value = "跳转地址")
    private String redirectUrl;

    @Size(max = 100, message = "Length of remark is limit 100 !")
    @ApiModelProperty(value = "备注")
    private String remark;

    @NotNull(message = "grantApproval can't be null.")
    @ApiModelProperty(value = "是否审批券(true/false)", required = true)
    private Boolean grantApproval;

    @NotNull(message = "total can't be null.")
    @ApiModelProperty(value = "发行总量", required = true)
    @Column(name = "total")
    private Long total;

    @NotNull(message = "possess_limit can't be null.")
    @ApiModelProperty(value = "单持上限", required = true)
    @Column(name = "possess_limit")
    private Integer possessLimit;

}