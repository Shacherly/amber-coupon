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
public class DescrTemplateCreateParam {

    @NotNull
    @ApiModelProperty(value = "使用场景1理财加息 51kyc资产券 52入金资产券 53理财资产券 11借贷减息 21双币收益增强 31双币体验金")
    private Integer applyScene;

    @NotEmpty
    @ApiModelProperty(value = "多语言模板")
    private JSONObject template;

}
