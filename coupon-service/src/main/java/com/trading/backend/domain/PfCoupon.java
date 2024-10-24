package com.trading.backend.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import tk.mybatis.mapper.annotation.KeySql;

@Data
@Accessors(chain = true)
@Table(name = "pf_coupon")
public class PfCoupon implements Serializable {
    /**
     * 主键
     */
    @Id
    @KeySql(useGeneratedKeys = true)
    @Column(name = "id", insertable = false)
    private Long id;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 标题 coupon_description._id
     */
    @Column(name = "title")
    private String title;

    /**
     * 优惠券类型(0-加息券，1-抵扣券，2-满减券，3-返现券，4-体验券，5-资产券)
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 优惠券状态(0-禁用，1-启用，2-失效（过期或发放完）)
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 描述 coupon_description._id
     */
    @Column(name = "description")
    private String description;

    /**
     * 是否支持叠加使用
     */
    @Column(name = "over_lay")
    private Boolean overLay;

    /**
     * 可发行总量
     */
    @Column(name = "total")
    private Long total;

    /**
     * 已发行总量
     */
    @Column(name = "released")
    private Long released;

    /**
     * 已使用总量
     */
    @Column(name = "used")
    private Long used;

    /**
     * 单个用户限制（9999以上表示无限制）
     */
    @Column(name = "limit_per_people")
    private Long limitPerPeople;

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
     * 跳转链接
     */
    @Column(name = "redirect_url")
    private String redirectUrl;

    /**
     * 过期类型（0-固定起止日期，1-固定天数）
     */
    @Column(name = "expire_type")
    private Integer expireType;

    /**
     * 有效天数
     */
    @Column(name = "expire_days")
    private Integer expireDays;

    /**
     * 有效起始时间
     */
    @Column(name = "expire_at_start")
    private Long expireAtStart;

    /**
     * 有效结束时间
     */
    @Column(name = "expire_at_end")
    private Long expireAtEnd;

    /**
     * 预锁定数量
     */
    @Column(name = "locked")
    private Long locked;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", title=").append(title);
        sb.append(", type=").append(type);
        sb.append(", status=").append(status);
        sb.append(", remark=").append(remark);
        sb.append(", description=").append(description);
        sb.append(", overLay=").append(overLay);
        sb.append(", total=").append(total);
        sb.append(", released=").append(released);
        sb.append(", used=").append(used);
        sb.append(", limitPerPeople=").append(limitPerPeople);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append(", redirectUrl=").append(redirectUrl);
        sb.append(", expireType=").append(expireType);
        sb.append(", expireDays=").append(expireDays);
        sb.append(", expireAtStart=").append(expireAtStart);
        sb.append(", expireAtEnd=").append(expireAtEnd);
        sb.append(", locked=").append(locked);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}