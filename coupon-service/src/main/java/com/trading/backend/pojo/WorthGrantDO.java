package com.trading.backend.pojo;


import com.trading.backend.domain.base.CouponAdaptable;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author ~~ trading.s
 * @date 13:15 10/15/21
 */
@Data
public class WorthGrantDO implements Serializable, CouponAdaptable {
    private static final long serialVersionUID = -7630131744451814218L;

    private LocalDateTime exptEndTime;

    private Integer applyScene;

    private Long possessId;

    private String uid;
}
