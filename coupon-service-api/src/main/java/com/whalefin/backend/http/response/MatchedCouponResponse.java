package com.trading.backend.http.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ~~ trading.s
 * @date 15:38 09/23/21
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MatchedCouponResponse {

    private static final long serialVersionUID = 7758097087834311127L;

}
