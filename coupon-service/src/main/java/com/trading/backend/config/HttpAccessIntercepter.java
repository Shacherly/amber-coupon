package com.trading.backend.config;


import com.trading.backend.util.ContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author ~~ trading.s
 * @date 11:20 10/15/21
 */
@Component
public class HttpAccessIntercepter extends HandlerInterceptorAdapter {


    @Override
    public void postHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            ModelAndView modelAndView) throws Exception {

        ContextHolder.clear();
        super.postHandle(request, response, handler, modelAndView);
    }


}
