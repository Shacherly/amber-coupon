package com.trading.backend;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;


// @EnableAsync
@Import(SpringUtil.class)
@EnableScheduling
@EnableFeignClients("com.trading.backend.coupon.client")
// @MapperScan(basePackages = "com.trading.backend.coupon.mapper")
@SpringBootApplication(
        exclude = {
                DruidDataSourceAutoConfigure.class,
                SecurityAutoConfiguration.class,
                ManagementWebSecurityAutoConfiguration.class
        }
)
public class CouponServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponServiceApplication.class, args);
    }

}
