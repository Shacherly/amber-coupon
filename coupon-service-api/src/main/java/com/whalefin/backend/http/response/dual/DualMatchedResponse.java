package com.trading.backend.http.response.dual;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


/**
 * @author ~~ trading.s
 * @date 13:02 09/26/21
 */
@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DualMatchedResponse implements Serializable {

    private static final long serialVersionUID = 1661665539218543225L;

    @ApiModelProperty(value = "匹配条件的优惠券List")
    private List<DualPossessVO> matched;

    // @ApiModelProperty(value = "不匹配条件的优惠券列表List")
    // private List<LoanPossessVO> unMatched;

    public DualMatchedResponse(List<DualPossessVO> matched/*, List<LoanPossessVO> unMatched*/) {
        this.matched = matched;
        // this.unMatched = unMatched;
    }

    public DualMatchedResponse() {
    }
}
