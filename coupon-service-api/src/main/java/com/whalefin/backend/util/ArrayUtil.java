package com.trading.backend.util;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ~~ tradingmu
 * @date 11:06 03/02/22
 */
public class ArrayUtil {


    public static <E> List<E> toList(String source, String delimiter, Function<String, E> mapping) {
        return Optional.ofNullable(source)
                       .filter(src -> src.contains(delimiter))
                       .map(src -> Arrays.stream(src.split(delimiter)).map(mapping).collect(Collectors.toList()))
                       .orElseGet(Collections::emptyList);
    }


}
