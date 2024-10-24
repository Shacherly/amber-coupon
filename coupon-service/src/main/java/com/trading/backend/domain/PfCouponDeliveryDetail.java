package com.trading.backend.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Accessors(chain = true)
@Table(name = "pf_coupon_delivery_detail")
public class PfCouponDeliveryDetail implements Serializable {
    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * 关联优惠券id
     */
    @Column(name = "relate_coupon_id")
    private Long relateCouponId;

    /**
     * 用户id
     */
    @Column(name = "uid")
    private String uid;

    /**
     * 领取类型(0-新客户注册，1-首次充币，2-首次交易，3-首次理财，4-推荐好友活动，5-人工发放)
     */
    @Column(name = "receive_type")
    private Integer receiveType;

    /**
     * 领取方式(0-主动领取，1-系统发放，2-人工发放)
     */
    @Column(name = "receive_way")
    private Integer receiveWay;

    /**
     * 优惠券状态(0-待使用，1-待生效，2-已过期，3-已使用，4-已禁用，5-已删除)
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
     * 失效日期
     */
    @Column(name = "expire_time")
    private Long expireTime;

    /**
     * 生效时间：券产生实际效果的时间
     */
    @Column(name = "effective_time")
    private Long effectiveTime;

    /**
     * 使用时间（或者券被激活的时间）
     */
    @Column(name = "use_time")
    private Long useTime;

    /**
     * 关联的活动id
     */
    @Column(name = "relate_activity_id")
    private Long relateActivityId;

    /**
     * 用户是否已读发放的券
     */
    @Column(name = "has_read")
    private Boolean hasRead;

    /**
     * 按钮显示状态
     */
    @Column(name = "button_status")
    private Integer buttonStatus;

    /**
     * 所属券类型
     */
    @Column(name = "coupon_type")
    private Integer couponType;

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
        sb.append(", relateCouponId=").append(relateCouponId);
        sb.append(", uid=").append(uid);
        sb.append(", receiveType=").append(receiveType);
        sb.append(", receiveWay=").append(receiveWay);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", expireTime=").append(expireTime);
        sb.append(", effectiveTime=").append(effectiveTime);
        sb.append(", useTime=").append(useTime);
        sb.append(", relateActivityId=").append(relateActivityId);
        sb.append(", hasRead=").append(hasRead);
        sb.append(", buttonStatus=").append(buttonStatus);
        sb.append(", couponType=").append(couponType);
        sb.append(", businessSide=").append(businessSide);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}