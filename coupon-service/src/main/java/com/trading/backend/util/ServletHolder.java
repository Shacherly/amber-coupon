package com.trading.backend.util;


import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author ~~ trading.s
 * @date 16:23 09/29/21
 */
public class ServletHolder {

    public static ServletRequestAttributes getRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static HttpServletRequest getHttpRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getHttpResponse() {
        return getRequestAttributes().getResponse();
    }

    public static String getParameter(String param, String orDefault) {
        return Optional.ofNullable(getHttpRequest().getParameter(param)).orElse(orDefault);
    }

    public static Integer getIntParameter(String param, Integer orDefault) {
        return Integer.valueOf(getParameter(param, String.valueOf(orDefault)));
    }

    public static <T> T getParameter(String param, Function<String, T> converter, T orDefault) {
        return converter.apply(getParameter(param, String.valueOf(orDefault)));
    }

    public static <T> T getAttribute(String key) {
        return (T) getHttpRequest().getAttribute(key);
    }

    // public static String get() {
    //     ClientHeader clientHeader = (ClientHeader) getHttpRequest().getAttribute(Constant.CLIENT_HEADER);
    //
    // }
}
