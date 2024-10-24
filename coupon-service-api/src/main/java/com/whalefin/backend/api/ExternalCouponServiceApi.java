package com.trading.backend.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author ~~ trading.s
 * @date 10:54 09/26/21
 */
// @RequestMapping("/internal/v1")
@FeignClient(name = "trading-coupon-service", contextId = "coupon")
public interface ExternalCouponServiceApi {
}
