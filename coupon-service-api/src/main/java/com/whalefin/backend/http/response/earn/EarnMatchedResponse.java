package com.trading.backend.http.response.earn;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


/**
 * @author ~~ trading.s
 * @date 15:38 09/23/21
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EarnMatchedResponse implements Serializable {

    private static final long serialVersionUID = -5316556957016925671L;

    @ApiModelProperty(value = "加息率(减息率)显示标签")
    private String tips;

    // @ApiModelProperty(value = "有效的持券总数")
    // private Integer totalNum;

    // @ApiModelProperty(value = "匹配条件的优惠券数量")
    // private Integer matchedNum;
    //
    // @ApiModelProperty(value = "不匹配条件的优惠券数量")
    // private Integer unMatchedNum;

    @ApiModelProperty(value = "匹配条件的优惠券List")
    private List<EarnPossessVO> matched;

    @ApiModelProperty(value = "不匹配条件的优惠券列表List")
    private List<EarnPossessVO> unMatched;

    public EarnMatchedResponse(String tips, List<EarnPossessVO> matched, List<EarnPossessVO> unMatched) {
        this.tips = tips;
        this.matched = matched;
        this.unMatched = unMatched;
    }

    public EarnMatchedResponse() {
    }
}
