package com.trading.backend.domain;

import com.alibaba.fastjson.JSONObject;
import com.trading.backend.common.domain.BaseEntity;
import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Table(name = "coupon_descr_template")
@EqualsAndHashCode(callSuper = true)
public class CouponDescrTemplate extends BaseEntity implements Serializable {
    
    @Column(name = "apply_scene")
    private Integer applyScene;

    @Column(name = "coupon_descr")
    private JSONObject couponDescr;

    /**
     * ENABLE,DISABLE
     */
    @Column(name = "status")
    private String status;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return new java.util.StringJoiner(
                ", ", getClass().getSimpleName() + "[", "]")
                .add("applyScene=" + applyScene)
                .add("couponDescr=" + couponDescr)
                .add("status='" + status + "'")
                .add(super.toString())
                .toString();
    }
}