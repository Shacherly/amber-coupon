package com.trading.backend.http.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
public class ExternalHeaderUid implements Serializable {
    private static final long serialVersionUID = 6999243785994828253L;

    @ApiModelProperty(value = "用户唯一id（内部调用直接传uid）", hidden = true)
    private String headerUid;

    @Override
    public String toString() {
        return "ExternalHeaderUid{" +
                "uid='" + headerUid + '\'' +
                '}';
    }
}
