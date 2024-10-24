package com.trading.backend.config;


import com.trading.backend.common.util.TemporalUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;


/**
 * 用于handler入参的转换
 */
@Component
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(@Nonnull String source) {
        return TemporalUtil.ofMilli(Long.parseLong(source));
    }
}
