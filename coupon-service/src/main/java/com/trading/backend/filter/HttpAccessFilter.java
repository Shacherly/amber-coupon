package com.trading.backend.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.trading.backend.http.ContextHeader;
import com.trading.backend.constant.Constant;
import com.trading.backend.http.Response;
import com.trading.backend.util.ContextHolder;
import com.trading.backend.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
@WebFilter(filterName = "HttpAccessFilter", urlPatterns = "/*")
public class HttpAccessFilter implements Filter {

    // private static final String START_TIME = "request-start";
    //
    // private static final List<String> langArr = Arrays.asList(
    //         "en_US", "zh_CN", "zh_TW", "ko_KR", "es_ES", "pt_PT", "ja_JP", "ru_RU", "tr_TR"
    // );
    // private static final List<String> requireValidateJwt = Arrays.asList(
    //         "/usercenter/user/profile"
    // );
    private static final Set<String> ACCESS_TRACE_LIST = Sets.newHashSet(
            "/coupon/v1",
            "/coupon/internal/v1"
    );

    private static boolean tracable(String uri) {
        return ACCESS_TRACE_LIST.stream().anyMatch(uri::startsWith);
    }

    @Autowired
    private Environment env;

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        Instant start = Instant.now();
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String accessUri = request.getRequestURI();
        // 只记录Controller的访问
        if (!tracable(accessUri)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Set<String> headFields = ReflectionUtil.getSnakeFields(ContextHeader.class);
        Map<String, Object> headMap = new HashMap<>();
        Enumeration<String> httpHeaders = request.getHeaderNames();
        for (; ; ) {
            if (!httpHeaders.hasMoreElements()) break;
            String headKey = httpHeaders.nextElement();
            String headVal = request.getHeader(headKey);
            if (headFields.contains(headKey))
                headMap.put(headKey, headVal);
            if (headKey.equals(Constant.HEADER_USER))
                headMap.put(headKey, Optional.ofNullable(JSON.parseObject(headVal))
                                             .map(v -> v.getString(Constant.HEADER_USER_ID)).orElse(null));
        }

        JSONObject headerObject = new JSONObject(headMap);
        ContextHeader contextHeader = JSON.toJavaObject(headerObject, ContextHeader.class);
        contextHeader.setSpringEnv(env.getActiveProfiles()[0]);
        ContextHolder.set(contextHeader);
        // 网关请求XGwRequestid
        String reqId = Optional.ofNullable(contextHeader.getTraceId())
                               .orElseGet(() -> {
                                   // 内部请求 TraceId
                                   return Optional.ofNullable(contextHeader.getXGwRequestid())
                                                  .orElseGet(() -> UUID.randomUUID().toString());
                               });

        MDC.put(Constant.TRACE_ID, reqId);
        // if (clientHeader.getOriginChannel() == null && !originChannelValWhiteList.contains(request.getRequestURI())) {
        //     Response<String> r = Response.fail(ExceptionEnum.PARAM_ERROR.getCode(), "origin channel invalid");
        //     HttpServletResponse resp = (HttpServletResponse) servletResponse;
        //     resp.setContentType("text/json;charset=utf-8");
        //     resp.setStatus(HttpStatus.OK.value());
        //     resp.getWriter().write(JSONUtil.toJsonStr(r));
        //     resp.getWriter().flush();
        //     return;
        // }

        // if (clientHeader.getUid() == null && requireValidateJwt.contains(request.getRequestURI())) {
        //     Response<String> r = Response.fail(ExceptionEnum.PARAM_ERROR.getCode(), "uid invalid");
        //     HttpServletResponse resp = (HttpServletResponse) servletResponse;
        //     resp.setContentType("text/json;charset=utf-8");
        //     resp.setStatus(HttpStatus.OK.value());
        //     resp.getWriter().write(JSONUtil.toJsonStr(r));
        //     resp.getWriter().flush();
        //     return;
        // }
        request.setAttribute(Constant.CLIENT_HEADER, contextHeader);

        HashMap<String, Object> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String value = request.getParameter(name);
            params.put(name, value);
        }

        RequestWrapper requestWrapper = new RequestWrapper(request);
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        httpServletResponse.setHeader(Constant.HEADER_REQUEST_ID, reqId);
        httpServletResponse.setHeader("current_env", contextHeader.getSpringEnv());

        ResponseWrapper responseWrapper =
                new ResponseWrapper(httpServletResponse);

        filterChain.doFilter(requestWrapper, responseWrapper);

        byte[] responseContent = responseWrapper.getBody();
        String properResponse = new String(responseContent);
        if (responseContent.length == 0) {
            Response<Object> ok = Response.ok("");
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpServletResponse.setStatus(HttpStatus.OK.value());
            httpServletResponse.getWriter().write(JSONObject.toJSONString(ok));
            httpServletResponse.getWriter().flush();
            properResponse = JSONObject.toJSONString(ok);
        }

        Instant end = Instant.now();
        Map<String, Object> requestEntity = new LinkedHashMap<>(16);
        // requestEntity.put("ip", this.getRealIP(request));
        requestEntity.put("uri", accessUri);
        requestEntity.put("method", request.getMethod());
        requestEntity.put("params", JSONObject.parseObject(JSON.toJSONString(params)));
        requestEntity.put("header", headerObject);
        requestEntity.put("body", JSONObject.parseObject(requestWrapper.getBody()));

        Map<String, Object> responseEntity = new LinkedHashMap<>(8);
        responseEntity.put("response", properResponse);
        responseEntity.put("spend", Duration.between(start, end).toMillis() + " ms");
        log.info("request entity = {}, response entity = {}", JSONObject.toJSONString(requestEntity), JSONObject.toJSONString(responseEntity));
        ContextHolder.clear();
        MDC.remove(Constant.TRACE_ID);
    }

    @Override
    public void destroy() {}

    /**
     * 获取IP地址
     *
     * @param request
     * @return request发起客户端的IP地址
     */
    private String getRealIP(HttpServletRequest request) {
        if (request == null) {
            return "0.0.0.0";
        }

        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");

        String UNKNOWN_IP = "unknown";
        if (StringUtils.isNotEmpty(XFor) && !UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            }
            else {
                return XFor;
            }
        }

        XFor = Xip;
        if (StringUtils.isNotEmpty(XFor) && !UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            return XFor;
        }

        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(XFor) || UNKNOWN_IP.equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }

}
