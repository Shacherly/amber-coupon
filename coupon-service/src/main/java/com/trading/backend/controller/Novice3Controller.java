package com.trading.backend.controller;


import com.trading.backend.annotation.Authentication;
import com.trading.backend.common.annotation.Idempotent;
import com.trading.backend.config.NoviceProperty;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.ExternalHeaderUid;
import com.trading.backend.http.response.BitInfoRes;
import com.trading.backend.http.response.CashGrantPopupVO;
import com.trading.backend.http.response.TaskProgressAggreRes;
import com.trading.backend.http.response.activity.ActivityConfigRes;
import com.trading.backend.service.ICouponService;
import com.trading.backend.util.ContextHolder;
import com.trading.backend.util.Converter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
@Api(tags = "新手活动3期-Api-External")

@ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", name = "x-gw-user", required = true, dataTypeClass = String.class),
        @ApiImplicitParam(paramType = "header", name = "access_token", required = true, dataTypeClass = String.class),
        @ApiImplicitParam(paramType = "header", name = "refresh_token", required = true, dataTypeClass = String.class)
})
public class Novice3Controller {


    @Autowired
    private ICouponService couponService;
    @Autowired
    private NoviceProperty noviceProperty;


    @Authentication
    @PostMapping("/cash/granted/popup")
    @ApiOperation(value = "资产券充值到账弹窗数据", notes = "资产券充值到账弹窗数据")
    public Response<List<CashGrantPopupVO>> getCashGrantPopup(@Validated @RequestBody ExternalHeaderUid param) {
        List<CashGrantPopupVO> cashGrantPopup = couponService.getCashGrantPopup(param);
        return Response.ok(cashGrantPopup);
    }


    @Idempotent
    @Authentication
    @PostMapping("/cash/granted/popup/read")
    @ApiOperation(value = "资产券充值到账弹窗数据已读", notes = "资产券充值到账弹窗数据已读")
    public Response<Boolean> assetRead(@Validated @RequestBody ExternalHeaderUid param) {
        return Response.ok(couponService.cashPopupRead(param));
    }

    @GetMapping("/task/progress")
    @ApiOperation(value = "新手任务进度查询", notes = "新手任务进度查询")
    public Response<TaskProgressAggreRes> taskProgress() {
        return Response.ok(Converter.fromTaskProgress(couponService.taskProgress(ContextHolder.get().getXGwUser())));
    }


    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "x-gw-user", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(paramType = "header", name = "access_token", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(paramType = "header", name = "refresh_token", required = false, dataTypeClass = String.class)
    })
    @GetMapping("/bit/info")
    @ApiOperation(value = "获取BitCoin币价和涨幅", notes = "获取BitCoin币价和涨幅")
    public Response<BitInfoRes> bitInfo() {
        return Response.ok(couponService.bitInfo());
    }


    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "x-gw-user", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(paramType = "header", name = "access_token", required = false, dataTypeClass = String.class),
            @ApiImplicitParam(paramType = "header", name = "refresh_token", required = false, dataTypeClass = String.class)
    })
    @GetMapping("/activity/config/novice3")
    @ApiOperation(value = "新手活动3期 时间配置")
    public Response<ActivityConfigRes> registAfterActivity() {
        return Response.ok(new ActivityConfigRes(noviceProperty.getStartMilli(), noviceProperty.getEndMilli()));
    }
}
