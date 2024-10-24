package com.trading.backend.interceptor;

import com.trading.backend.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * @author ~~ trading mu
 * @date 18:30 03/29/22
 */
@Component @Slf4j
public class TraceableInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String traceId = MDC.get(Constant.TRACE_ID);
        if (StringUtils.isBlank(traceId))
            MDC.put(Constant.TRACE_ID, UUID.randomUUID().toString());
        log.info("Traceable Accessed at method [{}]", invocation.getMethod());
        Object proceed = null;
        try {
            proceed = invocation.proceed();
        } catch (Exception e) {
            log.error("Unexpected exception occurred when invoking method: {}", invocation.getMethod(), e);
        } finally {
            log.info("Traceable Left at method [{}]", invocation.getMethod());
            MDC.remove(Constant.TRACE_ID);
        }
        return proceed;
    }
}
