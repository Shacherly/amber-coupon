package com.trading.backend.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Accessors(chain = true)
@Table(name = "pf_coupon_reward_detail")
public class PfCouponRewardDetail implements Serializable {
    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * 关联优惠券发放id
     */
    @Column(name = "relate_coupon_delivery_id")
    private Long relateCouponDeliveryId;

    /**
     * 订单id，transfer_id
     */
    @Column(name = "order_id")
    private Long orderId;

    /**
     * 订单类型(0-定制化理财，1-现货交易，2-杠杆交易，3-充币，4-提币，5-定期理财，6-借贷减息，34-活动奖励)
     */
    @Column(name = "order_type")
    private Integer orderType;

    /**
     * 加息券适用币种、资产券奖励币种
     */
    @Column(name = "coin")
    private String coin;

    /**
     * 优惠金额、奖励金额
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * 优惠券状态(-1：已废弃， 0: 进行中，1: ACCEPT, 2: 已完成)
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "ctime")
    private LocalDateTime ctime;

    /**
     * 更新时间
     */
    @Column(name = "utime")
    private LocalDateTime utime;

    /**
     * 创建时间戳
     */
    @Column(name = "created_time")
    private Long createdTime;

    /**
     * 更新时间戳
     */
    @Column(name = "updated_time")
    private Long updatedTime;

    /**
     * 0-理财加息，1-抵扣手续费，2-满减本金，3-返现，4-体验奖励
     */
    @Column(name = "coupon_type")
    private Integer couponType;

    /**
     * 加息券到期时间戳
     */
    @Column(name = "interest_expiration_time")
    private Long interestExpirationTime;

    /**
     * 激活条件
     */
    @Column(name = "activate_condition")
    private Integer activateCondition;

    /**
     * 激活时间（达成激活条件的ms）
     */
    @Column(name = "activate_time")
    private Long activateTime;

    /**
     * 到账时间（ms）
     */
    @Column(name = "arrived_time")
    private Long arrivedTime;

    /**
     * 奖励发放uid
     */
    @Column(name = "reward_uid")
    private String rewardUid;

    /**
     * 关联的券ID
     */
    @Column(name = "coupon_id")
    private Long couponId;

    /**
     * 业务范围（1-App、2-Pro）
     */
    @Column(name = "business_side")
    private Short businessSide;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", relateCouponDeliveryId=").append(relateCouponDeliveryId);
        sb.append(", orderId=").append(orderId);
        sb.append(", orderType=").append(orderType);
        sb.append(", coin=").append(coin);
        sb.append(", amount=").append(amount);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", couponType=").append(couponType);
        sb.append(", interestExpirationTime=").append(interestExpirationTime);
        sb.append(", activateCondition=").append(activateCondition);
        sb.append(", activateTime=").append(activateTime);
        sb.append(", arrivedTime=").append(arrivedTime);
        sb.append(", rewardUid=").append(rewardUid);
        sb.append(", couponId=").append(couponId);
        sb.append(", businessSide=").append(businessSide);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}