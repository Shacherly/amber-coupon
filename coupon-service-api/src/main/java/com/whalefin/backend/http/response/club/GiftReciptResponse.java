package com.trading.backend.http.response.club;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.bo.BasalExportPossessBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class GiftReciptResponse implements Serializable {
    private static final long serialVersionUID = -7213824975669356196L;


    @ApiModelProperty(value = "领券的到期时间")
    private Long validUntil;


    @ApiModelProperty(value = "领券后的券状态")
    private Integer buttonStatus;

    public static GiftReciptResponse received(BasalExportPossessBO possess) {
        GiftReciptResponse response = new GiftReciptResponse();
        response.setButtonStatus(possess.getBusinessStage());
        response.setValidUntil(possess.getAvailableEnd());
        return response;
    }

}
