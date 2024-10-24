package com.trading.backend.http.response.desctemplate;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@AllArgsConstructor @NoArgsConstructor
public class DescrTemplateVO implements Serializable {
    private static final long serialVersionUID = -57311988162147501L;

    private Long id;

    @ApiModelProperty(value = "使用场景1理财加息 51kyc资产券 52入金资产券 53理财资产券 11借贷减息 21双币收益增强 31双币体验金")
    private Integer applyScene;

    @ApiModelProperty(value = "多语言模板")
    private JSONObject template;


    @Override
    public String toString() {
        return "DescrTemplateVO{" +
                "id=" + id +
                ", applyScene=" + applyScene +
                ", template=" + template +
                '}';
    }

}
