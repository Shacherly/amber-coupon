package com.trading.backend.interceptor;


import cn.hutool.extra.spring.SpringUtil;
import com.trading.backend.annotation.PostHandle;
import com.trading.backend.config.ExecutorConfigurer;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Function;


/**
 * @author ~~ trading.s
 * @date 10:54 01/24/22
 */
@Component @Slf4j
public class PostHandleInterceptor implements MethodInterceptor {

    @Autowired
    private ExecutorConfigurer threadConf;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object proceed = invocation.proceed();
        Method method = invocation.getMethod();
        PostHandle postHandle = AnnotationUtils.getAnnotation(method, PostHandle.class);
        return Optional.ofNullable(postHandle)
                       .map(post -> {
                           threadConf.getExecutor().execute(() -> {
                               PostHandleService handleService = SpringUtil.getBean(PostHandleService.class);
                               Function<Object, Object> invoke = handleService.getMethodPostInvoke(post.handler(), method.getReturnType());
                               log.info("PostMethod result={}", proceed);
                               Object apply = invoke.apply(proceed);
                               log.info("MethodPostHandle result={}", apply);
                           });
                           return proceed;
                       })
                       .orElse(proceed);
    }
}
