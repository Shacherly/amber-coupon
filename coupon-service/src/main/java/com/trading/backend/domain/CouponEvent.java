package com.trading.backend.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Accessors(chain = true)
@Table(name = "coupon_event")
public class CouponEvent implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * event关联的优惠券id
     */
    @Column(name = "coupon_ids")
    private String couponIds;

    /**
     * 0审批发券，1运营人工发券，2专题活动发券
     */
    @Column(name = "type")
    private Integer type;

    @Column(name = "name")
    private String name;

    @Column(name = "descr")
    private String descr;

    /**
     * 0未开始，1进行中，2部分用户发放成功，3全部发放成功，4全部发放失败
     */
    @Column(name = "event_stage")
    private Integer eventStage;

    @Column(name = "approval_event")
    private Boolean approvalEvent;

    /**
     * 0审批未提交，1审批中，2审批拒绝，3审批通过
     */
    @Column(name = "approval_stage")
    private Integer approvalStage;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "ctime")
    private LocalDateTime ctime;

    @Column(name = "utime")
    private LocalDateTime utime;

    @Column(name = "remark")
    private String remark;

    /**
     * 发放客户对象类型ALL、SELECTED_USER、IMPORTED_USER、USER_LABEL、SINGLE_USER
     */
    @Column(name = "object_type")
    private String objectType;

    /**
     * 用户关联参数
     */
    @Column(name = "object_attaches")
    private String objectAttaches;

    @Column(name = "result_phase")
    private String resultPhase;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", couponIds=").append(couponIds);
        sb.append(", type=").append(type);
        sb.append(", name=").append(name);
        sb.append(", descr=").append(descr);
        sb.append(", eventStage=").append(eventStage);
        sb.append(", approvalEvent=").append(approvalEvent);
        sb.append(", approvalStage=").append(approvalStage);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", remark=").append(remark);
        sb.append(", objectType=").append(objectType);
        sb.append(", objectAttaches=").append(objectAttaches);
        sb.append(", resultPhase=").append(resultPhase);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}