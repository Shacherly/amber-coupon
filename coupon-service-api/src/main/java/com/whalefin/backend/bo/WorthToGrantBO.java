package com.trading.backend.bo;


import lombok.Data;

import java.io.Serializable;


@Data
public class WorthToGrantBO implements Serializable {

    private static final long serialVersionUID = 2299786772208057892L;

    private Long exptEndTime;

    private Integer applyScene;

    private Long possessId;

    private String uid;
}
