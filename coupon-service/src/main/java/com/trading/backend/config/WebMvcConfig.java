package com.trading.backend.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author ~~ trading.s
 * @date 19:26 09/27/21
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // @Autowired
    // private MethodArgementResolver resolver;
    @Autowired
    private HttpAccessIntercepter accessIntercepter;
    @Autowired
    private IdempotentInterceptor idempotentInterceptor;
    @Autowired
    private LocalDateTimeConverter dateTimeConverter;
    private final static String DOC_HTML = "/doc.html";

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:" + DOC_HTML);
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        // WebMvcConfigurer.super.addViewControllers(registry);
    }

    // @Override
    // public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    //     // resolvers.add(resolver);
    //     // WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    // }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessIntercepter);
        registry.addInterceptor(idempotentInterceptor).addPathPatterns("/v1/**");
        // WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(dateTimeConverter);
    }
}
