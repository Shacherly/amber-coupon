package com.trading.backend.config;


import com.trading.backend.http.ContextHeader;
import com.trading.backend.http.request.ExternalHeaderUid;
import com.trading.backend.util.ContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Component
public class AuthorityHandlerIntercepter implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object[] arguments = invocation.getArguments();
        Class<?>[] paramTypes = invocation.getMethod().getParameterTypes();
        int index = 0;
        ContextHeader header = ContextHolder.get();
        for (Object argument : arguments) {
            if (Objects.isNull(argument) && ExternalHeaderUid.class.isAssignableFrom(paramTypes[index++])){
                argument = arguments[index - 1] = paramTypes[index - 1].newInstance();
            }
            if (ExternalHeaderUid.class.isAssignableFrom(argument.getClass())) {
                if (StringUtils.isNotBlank(header.getXGwUser())) {
                    ((ExternalHeaderUid) argument).setHeaderUid(header.getXGwUser());
                    break;
                }
            }
        }
        return invocation.proceed();
    }
}
