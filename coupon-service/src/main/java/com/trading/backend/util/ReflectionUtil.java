package com.trading.backend.util;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ArrayListMultimap;
import com.trading.backend.annotation.Condition;
import com.trading.backend.annotation.CoulumnCondition;
import org.apache.commons.lang3.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ~~ trading.s
 * @date 23:33 10/05/21
 */
public class ReflectionUtil {

    private static final SimpleCache<Class<?>, Set<String>> SNAKES_CACHE = new SimpleCache<>();

    private ReflectionUtil() {}

    public static Set<String> getSnakeFields(Class<?> beanClass) {
        return Optional.ofNullable(SNAKES_CACHE.get(beanClass))
                       .orElseGet(() -> {
                           Set<String> collect =
                                   ReflectUtil.getFieldMap(beanClass).values().stream()
                                              .map(ReflectionUtil::getSnakeField).collect(Collectors.toSet());
                           SNAKES_CACHE.put(beanClass, collect);
                           return collect;
                       });
    }

    public static Example getExample(Object param, Class<?> cla) {
        Example example = new Example(cla);
        Example.Criteria criteria = example.createCriteria();
        ArrayListMultimap<String, Object> betweenMap = ArrayListMultimap.create();
        Field[] fields = ReflectUtil.getFields(param.getClass());
        for (Field field : fields) {
            Object fieldValue = ReflectUtil.getFieldValue(param, field);
            if (Modifier.isStatic(field.getModifiers()) || fieldValue == null) continue;
            CoulumnCondition columnAnno = field.getAnnotation(CoulumnCondition.class);
            String property = columnAnno.property();
            Condition condi = columnAnno.condition();
            if (condi == Condition.EQUAL) {
                criteria.andEqualTo(property, fieldValue);
            }
            else if (condi == Condition.LIKE) {
                criteria.andLike(property, StringUtils.join("%", fieldValue, "%"));
            }
            else if (condi == Condition.BETWEEN) {
                betweenMap.put(property, fieldValue);
            }
        }
        betweenMap.keySet().forEach(key -> {
            List values = betweenMap.get(key);
            values.sort(Comparator.comparing(Function.identity()));
            criteria.andBetween(key, values.get(0), values.get(1));
        });
        return example;
    }

    private static String getSnakeField(Field field) {
        return Optional.ofNullable(field)
                       .map(f -> {
                           return Optional.ofNullable(field.getAnnotation(JSONField.class))
                                          .map(JSONField::name)
                                          .orElseGet(() -> {
                                              return Optional.ofNullable(field.getAnnotation(JsonProperty.class))
                                                             .map(JsonProperty::value)
                                                             .orElseGet(() -> StrUtil.toUnderlineCase(field.getName()));
                                          });
                       })
                       .orElse(null);

    }
}
