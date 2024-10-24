package com.trading.backend.common.util;


import cn.hutool.core.date.DateUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

/**
 * @author ~~ trading.s
 * @date 20:50 10/11/21
 */
public class TemporalUtil {

    public static final ZoneId DEAFAULT_ZONE_ID = ZoneId.of("UTC+8");

    public static final ZoneOffset DEFAULT_ZONE_OFFSET = ZoneOffset.ofHours(8);

    private TemporalUtil() {}

    /**
     * Min LocalDateTime of the first day of offset month from tody
     * @param offSet
     * @return
     */
    public static LocalDateTime offSetMonthStart(int offSet) {
        LocalDate localDate = LocalDate.now();
        return LocalDateTime.of(localDate.with(TemporalAdjusters.firstDayOfMonth()).plusMonths(offSet),LocalTime.MIN);
    }

    public static LocalDateTime offSetMonthEnd(int offSet) {
        LocalDate localDate = LocalDate.now();
        return LocalDateTime.of(localDate.with(TemporalAdjusters.lastDayOfMonth()).plusMonths(offSet),LocalTime.MAX);
    }

    public static LocalDateTime offSetDaysStart(int offSet) {
        LocalDate localDate = LocalDate.now();
        return LocalDateTime.of(localDate.plusDays(offSet), LocalTime.MIN);
    }

    public static ZonedDateTime offSetDaysStart(ZonedDateTime reference, int daysOffSet) {
        return ZonedDateTime.of(reference.plusDays(daysOffSet).toLocalDate(), LocalTime.MIN, DEAFAULT_ZONE_ID);
    }

    public static Instant offSetInstant(long delta, TemporalUnit unit) {
        return Instant.now().plus(delta, unit);
    }

    public static LocalDateTime thisMonthStart() {
        return offSetMonthStart(0);
    }

    public static LocalDateTime thisMonthEnd() {
        return offSetMonthEnd(0);
    }

    public static void main(String[] args) {
        System.out.println(offSetInstant(0, ChronoUnit.SECONDS));
        System.out.println(offSetInstant(0, ChronoUnit.SECONDS));
        System.out.println(offSetInstant(20, ChronoUnit.SECONDS));
    }

    /**
     * 本地时区的 LocalDateTime
     * @param millis
     * @return
     */
    public static LocalDateTime ofMilli(Long millis) {
        Objects.requireNonNull(millis);
        return DateUtil.toLocalDateTime(Instant.ofEpochMilli(millis));
    }

    public static ZonedDateTime ofDefaultZoneMilli(Long millis) {
        Objects.requireNonNull(millis);
        return Instant.ofEpochMilli(millis).atZone(DEAFAULT_ZONE_ID);
    }

    public static Long toEpochMilli(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime);
        return DateUtil.toInstant(dateTime).toEpochMilli();
    }

    public static Long toEpochMilli(ZonedDateTime dateTime) {
        Objects.requireNonNull(dateTime);
        return DateUtil.toInstant(dateTime).toEpochMilli();
    }

    public static ZonedDateTime zonedNowTime(ZoneOffset zoneOffset) {
        return Instant.now().atZone(DEAFAULT_ZONE_ID);
    }

    public static ZonedDateTime defaultZoneNowTime() {
        return zonedNowTime(DEFAULT_ZONE_OFFSET);
    }

    public static LocalDate defaultZoneNowDate() {
        return defaultZoneNowTime().toLocalDate();
    }
}
