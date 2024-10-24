package com.trading.backend.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Accessors(chain = true)
@Table(name = "pf_coupon_activity_info")
public class PfCouponActivityInfo implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * 活动类型(0-人工发券，1-专题活动，2-审批发券)
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 活动名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 活动说明
     */
    @Column(name = "description")
    private String description;

    /**
     * 参与客户类型(0-全部用户，1-指定用户，2-未理财客户，3-未交易客户，4-未基础认证客户，5-未高级认证客户 ,6-客户分组)，对于专题活动，这个值为0
     */
    @Column(name = "receiver_type")
    private Integer receiverType;

    /**
     * 任务类型(0-注册成功，1-完成基础认证，2-完成高级认证，3-完成首次理财，4-完成首次交易)，对于人工发券无效
     */
    @Column(name = "task_type")
    private Integer taskType;

    /**
     * 状态(-2-未提交审批，-1-审批中，0-未开始，1-进行中|审批通过，2-已发放，3-部分发放，4-已结束，5-发放失败，6-审批拒绝)
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 有效期限开始时间，对于人工发券无效
     */
    @Column(name = "expire_at_start")
    private Long expireAtStart;

    /**
     * 有效期限结束时间，对于人工发券无效
     */
    @Column(name = "expire_at_end")
    private Long expireAtEnd;

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
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private String couponId;

    /**
     * 1-app 2-pro
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
        sb.append(", type=").append(type);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", receiverType=").append(receiverType);
        sb.append(", taskType=").append(taskType);
        sb.append(", status=").append(status);
        sb.append(", expireAtStart=").append(expireAtStart);
        sb.append(", expireAtEnd=").append(expireAtEnd);
        sb.append(", remark=").append(remark);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", couponId=").append(couponId);
        sb.append(", businessSide=").append(businessSide);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}