package com.trading.backend.domain.base;


import cn.hutool.core.date.DateUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * adapt LocalDateTime to Long, BigDecimal to plain String
 * @author ~~ trading.s
 * @date 12:40 10/06/21
 */
public interface CouponAdaptable {

    // default LocalDateTime getReceiveTime() {
    //     throw new RuntimeException(this.getClass().getName() + " has no field named receiveTime!");
    // }
    //
    // default LocalDateTime getAvailableStart() {
    //     throw new RuntimeException(this.getClass().getName() + " has no field named availableStart!");
    // }
    //
    // default LocalDateTime getAvailableEnd() {
    //     throw new RuntimeException(this.getClass().getName() + " has no field named availableEnd!");
    // }
    //
    // default BigDecimal getWorth() {
    //     throw new RuntimeException(this.getClass().getName() + " has no field named worth!");
    // }
    //
    // default LocalDateTime getConsumeTime() {
    //     throw new RuntimeException(this.getClass().getName() + " has no field named consumeTime!");
    // }

    default Long toEpochMilli(LocalDateTime time) {
        return Optional.ofNullable(DateUtil.toInstant(time)).map(Instant::toEpochMilli).orElse(null);
    }

    // default Long getStartEpoch() {
    //     return Optional.ofNullable(DateUtil.toInstant(getAvailableStart())).map(Instant::toEpochMilli).orElse(null);
    // }

    // default Long getEndEpoch() {
    //     return Optional.ofNullable(DateUtil.toInstant(getAvailableEnd())).map(Instant::toEpochMilli).orElse(null);
    // }

    // default Long getReceivEpoch() {
    //     return Optional.ofNullable(DateUtil.toInstant(getReceiveTime())).map(Instant::toEpochMilli).orElse(null);
    // }

    // default Long getConsumeEpoch() {
    //     return Optional.ofNullable(DateUtil.toInstant(getConsumeTime())).map(Instant::toEpochMilli).orElse(null);
    // }

    default String toPlainString(BigDecimal source) {
        return Optional.ofNullable(source).map(BigDecimal::stripTrailingZeros).map(BigDecimal::toPlainString).orElse(null);
    }

}
