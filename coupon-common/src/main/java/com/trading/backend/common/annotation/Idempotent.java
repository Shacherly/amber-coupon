package com.trading.backend.common.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;


/**
 * @author ~~ trading.s
 * @date 14:38 10/20/21
 * @desc 幂等注解，一般有更新数据的接口加上即可，纯获取数据的接口、GET请求等不要加 ~~~
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    long interval() default 4;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    String uniqueKey() default "";

}
