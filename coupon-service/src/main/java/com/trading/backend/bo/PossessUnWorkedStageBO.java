package com.trading.backend.bo;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author ~~ trading.s
 * @date 18:59 09/29/21
 * @desc possess bo at the stage of unused
 */
public class PossessUnWorkedStageBO implements Serializable {

    private static final long serialVersionUID = 940933099030549809L;

    private Long possessId;

    private Long couponId;

    private Integer couponType;

    private Integer apply_scene;

    private Integer possess_stage;

    private Integer business_stage;

    private Integer possess_event;

}
