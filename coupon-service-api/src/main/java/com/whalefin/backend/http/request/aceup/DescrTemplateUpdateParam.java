package com.trading.backend.http.request.aceup;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter @Getter
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DescrTemplateUpdateParam {

    @NotNull
    @ApiModelProperty(value = "id")
    private Long id;

    @NotEmpty
    @ApiModelProperty(value = "多语言模板")
    private JSONObject template;

}
