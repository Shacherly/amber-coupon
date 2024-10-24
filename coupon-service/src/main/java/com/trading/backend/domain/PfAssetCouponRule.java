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
@Table(name = "pf_asset_coupon_rule")
public class PfAssetCouponRule implements Serializable {
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * 入金规则币种
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
     * 卡券金额
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * 最低期限
     */
    @Column(name = "min_subscribe_days")
    private Integer minSubscribeDays;

    /**
     * 激活条件
     */
    @Column(name = "activate_condition")
    private Integer activateCondition;

    /**
     * 是否允许站内划转
     */
    @Column(name = "allow_transfer")
    private Boolean allowTransfer;

    /**
     * 最低申购/入金金额
     */
    @Column(name = "min_required_amount")
    private BigDecimal minRequiredAmount;

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
    @Column(name = "create_time")
    private Long createTime;

    /**
     * 更新时间戳
     */
    @Column(name = "update_time")
    private Long updateTime;

    /**
     * 资产券金额币种
     */
    @Column(name = "asset_coin")
    private String assetCoin;

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
        sb.append(", amount=").append(amount);
        sb.append(", minSubscribeDays=").append(minSubscribeDays);
        sb.append(", activateCondition=").append(activateCondition);
        sb.append(", allowTransfer=").append(allowTransfer);
        sb.append(", minRequiredAmount=").append(minRequiredAmount);
        sb.append(", remark=").append(remark);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", assetCoin=").append(assetCoin);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}