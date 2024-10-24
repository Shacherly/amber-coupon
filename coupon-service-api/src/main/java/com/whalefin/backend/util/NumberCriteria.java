package com.trading.backend.util;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;


/**
 * @author ~~ trading.s
 * @date 22:48 09/22/21
 */
public class NumberCriteria {

    public static final int PERSISTENT_SCALE = 16;

    public static final int ROUTINE_SCALE = 2;


    private NumberCriteria() {
    }

    /**
     * 约定入库精度为 32,16
     * @param source
     * @return
     */
    public static BigDecimal defaultScale(BigDecimal source) {
        return Optional.ofNullable(source)
                       .map(v -> {
                           return v.setScale(PERSISTENT_SCALE, RoundingMode.HALF_UP);
                       })
                       .orElse(null);
    }

    public static BigDecimal defaultScale(String source) {
        return Optional.ofNullable(source)
                       .map(v -> {
                           return new BigDecimal(v).setScale(PERSISTENT_SCALE, RoundingMode.HALF_UP);
                       })
                       .orElse(null);
    }

    /**
     * 常规精度 2位
     * @param source
     * @return
     */
    public static BigDecimal routineScale(BigDecimal source) {
        return Optional.ofNullable(source)
                       .map(v -> v.setScale(ROUTINE_SCALE, RoundingMode.HALF_UP))
                       .orElse(null);
    }

    public static String toPlainString(BigDecimal source) {
        return Optional.ofNullable(source)
                       .map(BigDecimal::toPlainString)
                       .orElse(null);
    }

    /**
     * 去掉末尾多余的0
     * @param source
     * @return
     */
    public static String stripTrailing(BigDecimal source) {
        return Optional.ofNullable(source)
                       .map(v -> v.stripTrailingZeros().toPlainString())
                       .orElse(null);
    }

    public static String to2ScaledString(BigDecimal source) {
        return Optional.ofNullable(source)
                       .map(v -> v.setScale(2, RoundingMode.HALF_UP))
                       .map(BigDecimal::toPlainString)
                       .orElse(null);
    }

    public static String to4ScaledString(BigDecimal source) {
        return Optional.ofNullable(source)
                       .map(v -> v.setScale(4, RoundingMode.HALF_UP))
                       .map(BigDecimal::toPlainString)
                       .orElse(null);
    }

    public static String toScaledString(BigDecimal source, int scale, RoundingMode roundingMode) {
        return Optional.ofNullable(source)
                       .map(v -> v.setScale(scale, roundingMode))
                       .map(BigDecimal::toPlainString)
                       .orElse(null);
    }
}
