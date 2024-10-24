package com.trading.backend.config;


import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Deprecated
public class LocalDateTimeSerializer implements ObjectSerializer {

    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        Optional.ofNullable(object)
                .map(LocalDateTime.class::cast)
                .map(time -> time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .map(time -> { out.writeString(time); return 1; })
                .orElseGet(() -> { out.writeNull(); return 1; });
    }

}
