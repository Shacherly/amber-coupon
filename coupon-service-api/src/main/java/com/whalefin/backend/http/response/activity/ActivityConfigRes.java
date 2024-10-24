package com.trading.backend.http.response.activity;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Getter @Setter
@Accessors(chain = true)
@ApiModel(value = "新手活动时间配置")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ActivityConfigRes implements Serializable {
    private static final long serialVersionUID = 4903506310134683934L;

    private long activityStart;

    private long activityEnd;


    public ActivityConfigRes(long activityStart, long activityEnd) {
        this.activityStart = activityStart;
        this.activityEnd = activityEnd;
    }

    public ActivityConfigRes() {
    }
}
