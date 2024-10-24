package com.trading.backend.controller;


import com.trading.backend.annotation.Authentication;
import com.trading.backend.common.annotation.Idempotent;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.FullScalePossessParam;
import com.trading.backend.http.request.ReddotReadParam;
import com.trading.backend.http.response.endpoint.FullScalePossessVO;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author ~~ trading.s
 * @date 13:56 09/23/21
 */
@Slf4j
@RestController
@RequestMapping(path = {"/v1"})
@Api(tags = "Coupon-Api-External（APP或WEB调用）")

@ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", name = "x-gw-user", required = true, dataTypeClass = String.class),
        @ApiImplicitParam(paramType = "header", name = "access_token", required = true, dataTypeClass = String.class),
        @ApiImplicitParam(paramType = "header", name = "refresh_token", required = true, dataTypeClass = String.class)
})
public class tradingCouponController {


    @Autowired
    private ICouponService couponService;
    @Autowired
    private IPossesService possesService;


    @Authentication
    @PostMapping("/possess/fullscale/list")
    @ApiOperation(value = "获取用户券列表")

    public Response<List<FullScalePossessVO>> getPossessCoupons(@Validated @RequestBody FullScalePossessParam param) {

        List<FullScalePossessVO> possess = possesService.getFullScalePossess(param);
        return Response.ok(possess);
    }


    @Idempotent
    @Authentication
    @PostMapping("/read/reddot")
    @ApiOperation(value = "红点已读更新", notes = "红点已读更新")

    public Response<Integer> reddotRead(@Validated @RequestBody ReddotReadParam param) {
        couponService.reddotRead(param);
        return Response.ok(0);
    }
}
