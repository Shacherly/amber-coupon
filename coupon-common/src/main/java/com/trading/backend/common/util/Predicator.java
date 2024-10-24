package com.trading.backend.common.util;


import cn.hutool.core.collection.CollectionUtil;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * @author ~~ trading.s
 * @date 13:16 10/03/21
 */
@FunctionalInterface
public interface Predicator<T> extends Predicate<T> {

    static <T, E> Predicate<T> isNull(Function<T, E> function) {
        return object -> Objects.equals(function.apply(object), null);
    }

    static <T> Predicate<T> isNull() {
        return Objects::isNull;
    }

    static <T, E> Predicate<T> nonNull(Function<T, E> function) {
        return isNull(function).negate();
    }

    static <T> Predicate<T> nonNull() {
        return Objects::nonNull;
    }

    static <T> Predicate<T> existIn(Set<T> collection) {
        return CollectionUtil.isEmpty(collection) ? object -> false : collection::contains;
    }

    static <T, E> Predicate<T> existIn(Function<T, E> function, Set<E> collection) {
        return object -> CollectionUtil.isNotEmpty(collection) && collection.contains(function.apply(object));
    }

    static <T> Predicate<T> notExist(Set<T> collection) {
        return existIn(collection).negate();
    }

    static <T, E> Predicate<T> notExist(Function<T, E> function, Set<E> collection) {
        return existIn(function, collection).negate();
    }

    static <T, E> Predicate<T> isEqual(Function<T, E> function, E targetRef) {
        return object -> Objects.equals(function.apply(object), targetRef);
    }

    static <T, E> Predicate<T> isNotEqual(Function<T, E> function, E targetRef) {
        return isEqual(function, targetRef).negate();
    }
}
