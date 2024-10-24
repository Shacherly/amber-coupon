package com.trading.backend.config;


import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.LoadingCache;
import com.trading.backend.common.annotation.Idempotent;
import com.trading.backend.filter.RequestWrapper;
import com.trading.backend.util.ContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author ~~ trading.s
 * @date 14:41 10/20/21
 */
@Slf4j
@Component
public class IdempotentInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    @Qualifier("idempotentAccessCache")
    private LoadingCache<String, AtomicInteger> cache;

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Idempotent ideAnno = handlerMethod.getMethodAnnotation(Idempotent.class);
            if (Objects.isNull(ideAnno))
                return super.preHandle(request, response, handler);

            RequestWrapper requestWrapper = new RequestWrapper(request);
            JSONObject requestBody = JSONObject.parseObject(requestWrapper.getBody());
            String uniqueTag = requestBody.getString(ideAnno.uniqueKey());
            String gwUser = ContextHolder.get().getXGwUser();
            String uri = request.getRequestURI();

            String key = StringUtils.join(uri, "?uid=", gwUser, "&unique=", uniqueTag);
            AtomicInteger cacheVal = cache.get(key);
            boolean intervalOut = cacheVal.get() == 1;
            cache.put(key, new AtomicInteger(cacheVal.addAndGet(1)));
            if (intervalOut) return true;

            log.error("Api access {} restrict in count {}", key, cacheVal.get() - 1);
            return false;
        }
        return true;
    }
}
