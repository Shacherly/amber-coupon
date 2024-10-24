package com.trading.backend.http.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter @Setter
public abstract class InternalParamUid implements Serializable {


    private static final long serialVersionUID = 7266884184105123299L;

    @NotBlank
    @ApiModelProperty(value = "用户唯一id（模块间调用直接传参，不传header）", required = true)
    private String uid;

    public InternalParamUid(String uid) {
        this.uid = uid;
    }

    public InternalParamUid() {
    }
}
