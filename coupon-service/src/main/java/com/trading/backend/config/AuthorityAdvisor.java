package com.trading.backend.config;


import com.trading.backend.annotation.Authentication;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AuthorityAdvisor extends AbstractPointcutAdvisor {

    private final AuthorityHandlerIntercepter advice;

    public AuthorityAdvisor(AuthorityHandlerIntercepter advice) {
        this.advice = advice;
    }

    @Override @NonNull
    public Pointcut getPointcut() {
        return AnnotationMatchingPointcut.forMethodAnnotation(Authentication.class);
    }

    @Override @NonNull
    public Advice getAdvice() {
        return advice;
    }

    private static final long serialVersionUID = -3066375644659268014L;

}
