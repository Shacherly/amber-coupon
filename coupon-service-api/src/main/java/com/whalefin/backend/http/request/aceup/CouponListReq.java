package com.trading.backend.http.request.aceup;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;

/**
 * @author ：trading mu
 * @date ：Created in 2021/10/16 14:27
 * @description：券列表请求实体
 * @modified By：
 */

@Getter
@Setter
@ApiModel(value = "券列表请求实体")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CouponListReq implements Serializable {

    private static final long serialVersionUID = -1952645394449770230L;

    @ApiModelProperty(value = "券id")
    private Long id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "优惠券类型(0加息券，1减息券，5资产券)")
    private Integer type;

    @ApiModelProperty(value = "优惠券状态(1启用，0禁用，-1过期，-2全部发放无可用数量)")
    private Integer status;

    @ApiModelProperty(value = "是否审批券(true/false)")
    private Boolean grant_approval;

}
