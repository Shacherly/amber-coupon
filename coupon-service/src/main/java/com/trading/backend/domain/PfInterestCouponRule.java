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
@Table(name = "pf_interest_coupon_rule")
public class PfInterestCouponRule implements Serializable {
    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * 币种
     */
    @Column(name = "coin")
    private String coin;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Long couponId;

    /**
     * 关联过期规则id
     */
    @Column(name = "expire_rule_id")
    private Long expireRuleId;

    /**
     * 使用加息券时的最小申购金额
     */
    @Column(name = "min_subscribe_amount")
    private BigDecimal minSubscribeAmount;

    /**
     * 使用加息券时的最大申购金额
     */
    @Column(name = "max_subscribe_amount")
    private BigDecimal maxSubscribeAmount;

    /**
     * 加息比例
     */
    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    /**
     * 使用加息券时的最小申购天数
     */
    @Column(name = "min_subscribe_days")
    private Integer minSubscribeDays;

    /**
     * 使用加息券时的最大申购天数
     */
    @Column(name = "max_subscribe_days")
    private Integer maxSubscribeDays;

    /**
     * 加息天数
     */
    @Column(name = "interest_days")
    private Integer interestDays;

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

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", coin=").append(coin);
        sb.append(", couponId=").append(couponId);
        sb.append(", expireRuleId=").append(expireRuleId);
        sb.append(", minSubscribeAmount=").append(minSubscribeAmount);
        sb.append(", maxSubscribeAmount=").append(maxSubscribeAmount);
        sb.append(", interestRate=").append(interestRate);
        sb.append(", minSubscribeDays=").append(minSubscribeDays);
        sb.append(", maxSubscribeDays=").append(maxSubscribeDays);
        sb.append(", interestDays=").append(interestDays);
        sb.append(", remark=").append(remark);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}