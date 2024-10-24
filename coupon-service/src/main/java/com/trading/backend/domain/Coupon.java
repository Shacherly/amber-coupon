package com.trading.backend.domain;

import com.alibaba.fastjson.JSONObject;
import com.trading.backend.domain.base.CouponAdaptable;
import com.trading.backend.domain.base.CouponInspectable;
import com.trading.backend.domain.base.CouponMultiLocaliable;
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
@Table(name = "coupon")
public class Coupon implements Serializable, CouponMultiLocaliable, CouponAdaptable, CouponInspectable {
    /**
     * primary key
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
     * 多语言标题
     */
    @Column(name = "title")
    private String title;

    /**
     * 1启用，0禁用，-1过期，-2全部发放无可用数量
     */
    @Column(name = "status")
    private Short status;

    /**
     * 券类型
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 多语言描述
     */
    @Column(name = "descr")
    private String descr;

    /**
     * 是否可叠加使用
     */
    @Column(name = "overlay")
    private Boolean overlay;

    /**
     * 发行总量
     */
    @Column(name = "total")
    private Long total;

    /**
     * 已领取总量
     */
    @Column(name = "issue")
    private Long issue;

    /**
     * 单持上限 大于9999为无限制
     */
    @Column(name = "possess_limit")
    private Integer possessLimit;

    /**
     * 几天后过期
     */
    @Column(name = "expr_in_days")
    private Integer exprInDays;

    /**
     * 有效开始时间
     */
    @Column(name = "expr_at_start")
    private LocalDateTime exprAtStart;

    /**
     * 有效结束时间
     */
    @Column(name = "expr_at_end")
    private LocalDateTime exprAtEnd;

    /**
     * 预锁定数量
     */
    @Column(name = "pre_lock")
    private Long preLock;

    /**
     * 跳转链接
     */
    @Column(name = "redirect_url")
    private String redirectUrl;

    /**
     * 规则配置
     */
    @Column(name = "rule")
    private JSONObject rule;

    @Column(name = "ctime")
    private LocalDateTime ctime;

    @Column(name = "utime")
    private LocalDateTime utime;

    /**
     * 价值币种
     */
    @Column(name = "worth_coin")
    private String worthCoin;

    /**
     * 金额、加息减息率
     */
    @Column(name = "worth")
    private BigDecimal worth;

    /**
     * 适用场景1理财加息，11借贷减息，51KYC返现，52入金返现，53理财返现
     */
    @Column(name = "apply_scene")
    private Integer applyScene;

    @Column(name = "remark")
    private String remark;

    @Column(name = "grant_approval")
    private Boolean grantApproval;

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
        sb.append(", status=").append(status);
        sb.append(", type=").append(type);
        sb.append(", descr=").append(descr);
        sb.append(", overlay=").append(overlay);
        sb.append(", total=").append(total);
        sb.append(", issue=").append(issue);
        sb.append(", possessLimit=").append(possessLimit);
        sb.append(", exprInDays=").append(exprInDays);
        sb.append(", exprAtStart=").append(exprAtStart);
        sb.append(", exprAtEnd=").append(exprAtEnd);
        sb.append(", preLock=").append(preLock);
        sb.append(", redirectUrl=").append(redirectUrl);
        sb.append(", rule=").append(rule);
        sb.append(", ctime=").append(ctime);
        sb.append(", utime=").append(utime);
        sb.append(", worthCoin=").append(worthCoin);
        sb.append(", worth=").append(worth);
        sb.append(", applyScene=").append(applyScene);
        sb.append(", remark=").append(remark);
        sb.append(", grantApproval=").append(grantApproval);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}