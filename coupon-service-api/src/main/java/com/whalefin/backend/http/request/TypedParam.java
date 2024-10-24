package com.trading.backend.http.request;


import com.trading.backend.http.PagingRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;


@Getter @Setter
public class TypedParam extends PagingRequest {
    private static final long serialVersionUID = 4188366342741242048L;

    @NotBlank
    @ApiModelProperty(value = "券类型，单个类型，或者多个类型逗号分割", example = "1,3,5")
    private String types;

    @Override
    public String toString() {
        return "TypedParam{" +
                "types='" + types + '\'' +
                "} " + super.toString();
    }
}
