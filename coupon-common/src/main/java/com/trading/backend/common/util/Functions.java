package com.trading.backend.common.util;


import cn.hutool.core.collection.CollectionUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * @author ~~ trading.s
 * @date 18:43 10/18/21
 */
public interface Functions<T, R> {

    /**
     * 常用于redis设置hashKey，传Object Key但实际是String
     * @param sources
     * @param actualMapper
     * @param <S>
     * @param <V>
     * @return
     */
    static <S, V> List<Object> toObjects(List<S> sources, Function<S, V> actualMapper) {
        return sources.stream().map(actualMapper).collect(Collectors.toList());
    }

    static <S, E> List<E> mapper(List<S> sources, Class<E> beanClass) {
        return sources.stream().map(beanClass::cast).collect(Collectors.toList());
    }

    static <V> Map<String, V> toMap(List<V> sources, Function<V, Object> keyMapper) {
        return sources.stream().collect(Collectors.toMap(v -> String.valueOf(keyMapper.apply(v)), Function.identity(), (v1, v2) -> v1));
    }

    static <V> Map<String, V> toMap(List<V> sources, Function<V, Object> keyMapper, Supplier<Map<String, V>> mapSupplier) {
        return sources.stream().collect(Collectors.toMap(v -> String.valueOf(keyMapper.apply(v)), Function.identity(), (v1, v2) -> v1, mapSupplier));
    }

    static <K, KTMP, V> Map<K, V> toMap(List<V> sources, Function<V, KTMP> keyMapper, Function<KTMP, K> keyConverter) {
        return sources.stream().collect(Collectors.toMap(keyConverter.compose(keyMapper), Function.identity(), (v1, v2) -> v1));
    }

    static <K, KTMP, V> Map<K, V> toMap(List<V> sources, Function<V, KTMP> keyMapper, Function<KTMP, K> keyConverter, Supplier<Map<K, V>> mapSupplier) {
        return sources.stream().collect(Collectors.toMap(keyConverter.compose(keyMapper), Function.identity(), (v1, v2) -> v1, mapSupplier));
    }

    static <E, R> Set<R> toSet(List<E> sources, Function<E, R> valueMapper) {
        return sources.stream().map(valueMapper).collect(Collectors.toSet());
    }

    static <E, R> List<R> toList(List<E> sources, Function<E, R> valueMapper) {
        return sources.stream().map(valueMapper).collect(Collectors.toList());
    }

    static <E, TMP, R> List<R> toList(List<E> sources, Function<E, TMP> valueMapper, Function<TMP, R> valueConverter) {
        return sources.stream().map(valueConverter.compose(valueMapper)).collect(Collectors.toList());
    }

    static <K, V> Map<K, List<V>> groupingBy(List<V> sources, Function<V, K> classifier) {
        return sources.stream().collect(Collectors.groupingBy(classifier));
    }

    static <E> List<E> filter(List<E> sources, Predicate<E> predicate) {
        return sources.stream().filter(predicate).collect(Collectors.toList());
    }

    static <E, R> List<R> filter(List<E> sources, Predicate<E> predicate, Function<E, R> mapping) {
        return sources.stream().filter(predicate).map(mapping).collect(Collectors.toList());
    }

    static <E> List<E> sort(List<E> sources, Comparator<E> comparator) {
        return sources.stream().sorted(comparator).collect(Collectors.toList());
    }

    static <E, R> List<R> sort(List<E> sources, Comparator<E> comparator, Function<E, R> mapping) {
        return sources.stream().sorted(comparator).map(mapping).collect(Collectors.toList());
    }

    static <E,F> Collection<F> union(Collection<E> sourceList, Function<E, Collection<F>> valueMapper) {
        return sourceList.stream().map(valueMapper).collect(Collectors.toList()).stream().reduce(CollectionUtil::union).orElseGet(Collections::emptyList);
    }
}
