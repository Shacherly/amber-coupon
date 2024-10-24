package com.trading.backend.http.request;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;


@Setter @Getter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PossessAvailableParam extends InternalParamUid implements Serializable {
    private static final long serialVersionUID = -4155441388108099809L;

    @NotEmpty
    @ApiModelProperty(value = "持券券id，传数组可批量查询", required = true)
    private List<Long> possessIds;

    @Override
    public String toString() {
        return "PossessAvailableParam{" +
                ", possessIds=" + possessIds +
                "} " + super.toString();
    }
}
