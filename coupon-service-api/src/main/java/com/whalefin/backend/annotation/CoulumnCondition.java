package com.trading.backend.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author ~~ trading.s
 * @date 12:28 09/27/21
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CoulumnCondition {

    String property();

    Condition condition() default Condition.EQUAL;
}



