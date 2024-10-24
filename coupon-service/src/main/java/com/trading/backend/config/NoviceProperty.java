package com.trading.backend.config;


import cn.hutool.core.collection.CollectionUtil;
import com.trading.backend.common.util.TemporalUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author ~~ trading.s
 * @date 15:12 10/29/21
 */
@Getter @Setter
@Component @RefreshScope
@ConfigurationProperties(prefix = "novice-property")
public class NoviceProperty {

    private static final Set<Long> SYNCRETI_COUPONS = new HashSet<>(16);

    @Autowired
    private GlobalSystemProperty systemProperty;

    private String noviceStage;

    private String startTime;

    private String endTime;

    private String registCoupon;

    private List<Long> registCouponV2;

    private Long kycCouponExtra;

    private String oldRegistCoupon;

    public ZonedDateTime getStart() {
        return LocalDateTime.parse(startTime).atZone(systemProperty.getZoneId());
    }

    public ZonedDateTime getEnd() {
        return LocalDateTime.parse(endTime).atZone(systemProperty.getZoneId());
    }

    public Long getStartMilli() {
        return TemporalUtil.toEpochMilli(getStart());
    }

    public Long getEndMilli() {
        return TemporalUtil.toEpochMilli(getEnd());
    }

    public List<Long> getRegistCoupon() {
        return Optional.ofNullable(registCoupon)
                       .filter(v -> v.contains(","))
                       .map(v -> v.split(","))
                       .map(v -> Arrays.stream(v).map(Long::parseLong).collect(Collectors.toList()))
                       .orElseGet(Collections::emptyList);
    }

    public List<Long> getOldRegistCoupon() {
        return Optional.ofNullable(oldRegistCoupon)
                .filter(v -> v.contains(","))
                .map(v -> v.split(","))
                .map(v -> Arrays.stream(v).map(Long::parseLong).collect(Collectors.toList()))
                .orElseGet(Collections::emptyList);
    }

    public Set<Long> getSyncretiCoupons() {
        synchronized (this) {
            if (CollectionUtil.isEmpty(SYNCRETI_COUPONS)) {
                SYNCRETI_COUPONS.addAll(getRegistCoupon());
                SYNCRETI_COUPONS.addAll(getRegistCouponV2());
            }
        }
        return SYNCRETI_COUPONS;
    }

    public int getNovicePhase() {
        return Optional.ofNullable(noviceStage).map(Integer::parseInt).orElse(2);
    }
}
