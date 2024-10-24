package com.trading.backend.http.request.club;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.http.request.ExternalHeaderUid;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


/**
 * @author ~~ trading.s
 * @date 17:07 09/23/21
 */
@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClubGiftReceiptParam extends ExternalHeaderUid {

    private static final long serialVersionUID = -6390946306163818426L;

    @NotNull
    @ApiModelProperty(value = "领取的券id", required = true)
    private Long couponId;

    @Override
    public String toString() {
        return "ClubGiftReceiptParam{" +
                "couponId=" + couponId +
                "} " + super.toString();
    }
}
