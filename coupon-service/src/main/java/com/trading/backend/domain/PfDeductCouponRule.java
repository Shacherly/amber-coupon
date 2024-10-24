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
@Table(name = "pf_deduct_coupon_rule")
public class PfDeductCouponRule implements Serializable {
    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * 适用币种
     */
    @Column(name = "apply_coin")
    private String applyCoin;

    /**
     * 优惠券id
     */
    @Column(name = "coupon_id")
    private Long couponId;

    /**
     * 减息折扣比例或减息利率
     */
    @Column(name = "deduct_rate")
    private BigDecimal deductRate;

    /**
     * 激活条件
     */
    @Column(name = "activate_condition")
    private Integer activateCondition;

    /**
     * 借贷金额上限
     */
    @Column(name = "max_loan_amount")
    private BigDecimal maxLoanAmount;

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
     * 借贷期限最低天数
     */
    @Column(name = "max_loan_days")
    private Integer maxLoanDays;

    /**
     * 借贷类型
     */
    @Column(name = "loan_type")
    private Integer loanType;

    /**
     * 借贷期限最低天数
     */
    @Column(name = "min_loan_days")
    private Integer minLoanDays;

    /**
     * 减息方式
     */
    @Column(name = "deduct_way")
    private Integer deductWay;

    /**
     * 减息天数
     */
    @Column(name = "deduct_days")
    private Integer deductDays;

    /**
     * 借贷金额下限
     */
    @Column(name = "min_loan_amount")
    private BigDecimal minLoanAmount;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", applyCoin=").append(applyCoin);
        sb.append(", couponId=").append(couponId);
        sb.append(", deductRate=").append(deductRate);
        sb.append(", activateCondition=").append(activateCondition);
        sb.append(", maxLoanAmount=").append(maxLoanAmount);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", maxLoanDays=").append(maxLoanDays);
        sb.append(", loanType=").append(loanType);
        sb.append(", minLoanDays=").append(minLoanDays);
        sb.append(", deductWay=").append(deductWay);
        sb.append(", deductDays=").append(deductDays);
        sb.append(", minLoanAmount=").append(minLoanAmount);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}