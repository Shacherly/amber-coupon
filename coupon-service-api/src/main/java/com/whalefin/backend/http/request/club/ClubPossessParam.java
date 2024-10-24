package com.trading.backend.http.request.club;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.trading.backend.http.request.InternalParamUid;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;


@Getter @Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClubPossessParam extends InternalParamUid {
    private static final long serialVersionUID = -5001022130152319073L;


    @NotEmpty
    @ApiModelProperty(value = "券id，传数组可批量查询", required = true)
    private List<Long> couponIds;


    @Override
    public String toString() {
        return "ClubPossessParam{" +
                "couponIds=" + couponIds +
                "} " + super.toString();
    }

    public ClubPossessParam(String uid, List<Long> couponIds) {
        super(uid);
        this.couponIds = couponIds;
    }

    public ClubPossessParam(List<Long> couponIds) {
        this.couponIds = couponIds;
    }

    public ClubPossessParam() {
    }
}
