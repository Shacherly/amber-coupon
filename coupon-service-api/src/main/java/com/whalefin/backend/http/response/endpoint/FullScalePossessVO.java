package com.trading.backend.http.response.endpoint;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.domain.vo.CouponPolymericRuleVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FullScalePossessVO extends BasalExportPossessBO {

    private static final long serialVersionUID = -8319007392340269084L;

    @ApiModelProperty(value = "适用场景1理财加息 11借贷减息 51kyc认证 52单次入金 53单次理财")
    private Integer applyScene;

    @ApiModelProperty(value = "使用时刻")
    private Long consumeTime;

    @ApiModelProperty(value = "预计生效结束时间（资产券奖励的预计到账时间）")
    private Long exptEndTime;

    @ApiModelProperty(value = "红点是否出现")
    private Boolean reddotAppear;

    @ApiModelProperty(value = "券使用规则配置")
    private CouponPolymericRuleVO couponRule;
}
