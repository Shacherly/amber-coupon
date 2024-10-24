package com.trading.backend.http.response;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @author ~~ trading.s
 * @date 20:49 09/26/21
 */
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PossessIdResponse implements Serializable {
    private static final long serialVersionUID = 6151896387571758210L;

    @ApiModelProperty(value = "领券后返回持券id")
    private Long possessId;

    @Override
    public String toString() {
        return "PossessIdResponse{" +
                "possessId=" + possessId +
                '}';
    }

    public PossessIdResponse(Long possessId) {
        this.possessId = possessId;
    }

    public PossessIdResponse() {
    }
}
