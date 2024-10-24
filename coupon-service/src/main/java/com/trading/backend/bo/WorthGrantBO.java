package com.trading.backend.bo;


import com.trading.backend.common.enums.CouponApplySceneEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorthGrantBO {

    private String uid;

    private String grantCoin;

    private String grantSize;

    private CouponApplySceneEnum applyScene;

    private Long possessId;

    private Long couponId;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WorthGrantBO{");
        sb.append("uid='").append(uid).append('\'');
        sb.append(", grantCoin='").append(grantCoin).append('\'');
        sb.append(", grantSize='").append(grantSize).append('\'');
        sb.append(", applyScene=").append(applyScene);
        sb.append(", possessId=").append(possessId);
        sb.append(", couponId=").append(couponId);
        sb.append('}');
        return sb.toString();
    }
}
