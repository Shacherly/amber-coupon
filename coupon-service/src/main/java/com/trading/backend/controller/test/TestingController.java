package com.trading.backend.controller.test;


import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "优惠券规则配置测试文档")
public class TestingController {


    @GetMapping("/1")
    @ApiOperation(value = "资产券规则配置（直接看响应参数）")
    public CashCouponRule getRule1() {
        return null;
    }

    @GetMapping("/2")
    @ApiOperation(value = "减息券规则配置（直接看响应参数）")
    public DeductCouponRule getRule2() {
        return null;
    }

    @GetMapping("/3")
    @ApiOperation(value = "加息券规则配置（直接看响应参数）")
    public InterCouponRule getRule3() {
        return null;
    }
}
