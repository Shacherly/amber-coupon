package com.trading.backend.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableKnife4j
@EnableSwagger2
@Profile({"dev", "sit", "uat", "local", "localsit", "localuat"})
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                // 加了ApiOperation注解的类，才生成接口文档
                .apis(RequestHandlerSelectors.basePackage("com.trading.backend.coupon.controller"))
                // 路径配置
                .paths(PathSelectors.any())
                .build()
                .directModelSubstitute(java.util.Date.class, String.class)
                .globalRequestParameters(getGlobalRequestParameters());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("CouponServer")
                // 描述
                .description("CouponServer API doc")
                // 文档请求host地址
                .termsOfServiceUrl("")
                // api版本
                .version("0.0.1").build();
    }

    private List<RequestParameter> getGlobalRequestParameters() {
        List<RequestParameter> list = new ArrayList<>();

        list.add(new RequestParameterBuilder()
                .name("x-gw-user")
                .description("格式为 {\"user_id\":\"61398b3345a9b22dc8b9ebfd\"}")
                .example(new Example("{\"user_id\":\"61398b3345a9b22dc8b9ebfd\"}"))
                .required(false)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("x-gw-requestid")
                .description("请求id用于链路追踪")
                .required(true)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("client_platform")
                .description("IOS,Android,Web")
                .required(false)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("client_version")
                .description("app版本")
                .required(true)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("client_ip")
                .description("")
                .required(false)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("client_distinct_id")
                .description("")
                .required(false)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("client_timezone")
                .description("")
                .required(false)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("client_language")
                .description("传多语言，如en_US")
                .example(new Example("en_US"))
                .required(true)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("origin_channel")
                .description("origin channel, 值为 APP、WEB、BACKEND")
                .example(new Example("APP"))
                .required(true)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        list.add(new RequestParameterBuilder()
                .name("mock")
                .description("true or false 用于mock数据请求")
                .required(false)
                .in(ParameterType.HEADER)
                .query(param -> param.model(model -> model.scalarModel(ScalarType.STRING)))
                .build());
        return list;
    }

}