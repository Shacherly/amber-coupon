package com.trading.backend.interceptor;

import com.trading.backend.annotation.PostHandle;
import lombok.RequiredArgsConstructor;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * @author ~~ trading.s
 * @date 10:34 01/24/22
 */
@Component @RequiredArgsConstructor
public class PostHandleAdvisor extends AbstractPointcutAdvisor {
    private static final long serialVersionUID = -4499528923635889875L;


    private final PostHandleInterceptor advice;


    @Override @NonNull
    public Pointcut getPointcut() {
        return AnnotationMatchingPointcut.forMethodAnnotation(PostHandle.class);
    }

    @Override @NonNull
    public Advice getAdvice() {
        return advice;
    }
}
