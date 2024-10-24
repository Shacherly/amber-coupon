package com.trading.backend.domain;

import com.trading.backend.domain.base.CouponAdaptable;
import com.trading.backend.domain.base.PossessLifecycle;
import lombok.Data;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@Table(name = "coupon_possess")
public class CouponPossess implements Serializable, PossessLifecycle, CouponAdaptable {
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * 用户唯一id
     */
    @Column(name = "uid")
    private String uid;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Long couponId;

    /**
     * 优惠券类型
     */
    @Column(name = "coupon_type")
    private Integer couponType;

    /**
     * 适用场景0理财加息，1KYC返现，2入金返现，3理财返现，4借贷减息
     */
    @Column(name = "apply_scene")
    private Integer applyScene;

    /**
     * 0持券有效、1持券无效
     */
    @Column(name = "possess_stage")
    private Integer possessStage;

    @Column(name = "ctime")
    private LocalDateTime ctime;

    @Column(name = "utime")
    private LocalDateTime utime;

    @Column(name = "has_read")
    private Boolean hasRead;

    /**
     * 领券事件0新用户注册领券，1运营活动发券，2会员权益礼券，101用户主动领取
     */
    @Column(name = "source")
    private Integer source;

    /**
     * 来源id：
     活动id
     */
    @Column(name = "source_id")
    private Long sourceId;

    /**
     * 过期时间
     */
    @Column(name = "expr_time")
    private LocalDateTime exprTime;

    /**
     * 用券的时刻
     */
    @Column(name = "consume_time")
    private LocalDateTime consumeTime;

    /**
     * 用券的业务id
     */
    @Column(name = "business_id")
    private String businessId;

    /**
     * 不同券的业务状态，初始状态默认0待使用
     inter(0待使用2加息中4加息提前结束6加息正常结束)
     cash(0待激活2待发放4激活失败6已发放)
     deduct(0待使用2减息中6减息结束)
     */
    @Column(name = "business_stage")
    private Integer businessStage;

    /**
     * 预计的生效结束时间，具体而言
     加息结束时间
     资产券到账时间
     减息结束时间
     */
    @Column(name = "expt_end_time")
    private LocalDateTime exptEndTime;

    /**
     * 实际的终止|结束时间
     */
    @Column(name = "actual_end_at")
    private LocalDateTime actualEndAt;

    @Column(name = "remark")
    private String remark;

    /**
     * 可以使用的时间
     */
    @Column(name = "usable_time")
    private LocalDateTime usableTime;

    /**
     * 用券业务币种
     */
    @Column(name = "business_coin")
    private String businessCoin;

    /**
     * 用券预计能产生的折扣
     */
    @Column(name = "expt_discount")
    private BigDecimal exptDiscount;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", uid=").append(uid);
        sb.append(", couponId=").append(couponId);
        sb.append(", couponType=").append(couponType);
        sb.append(", applyScene=").append(applyScene);
        sb.append(", possessStage=").append(possessStage);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", hasRead=").append(hasRead);
        sb.append(", source=").append(source);
        sb.append(", sourceId=").append(sourceId);
        sb.append(", exprTime=").append(exprTime);
        sb.append(", consumeTime=").append(consumeTime);
        sb.append(", businessId=").append(businessId);
        sb.append(", businessStage=").append(businessStage);
        sb.append(", exptEndTime=").append(exptEndTime);
        sb.append(", actualEndAt=").append(actualEndAt);
        sb.append(", remark=").append(remark);
        sb.append(", usableTime=").append(usableTime);
        sb.append(", businessCoin=").append(businessCoin);
        sb.append(", exptDiscount=").append(exptDiscount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}