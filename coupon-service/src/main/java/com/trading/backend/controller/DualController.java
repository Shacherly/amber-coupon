package com.trading.backend.controller;


import com.trading.backend.annotation.Authentication;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.client.IDualServiceApi;
import com.trading.backend.common.annotation.Idempotent;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.http.PageResult;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.TypedParam;
import com.trading.backend.http.request.club.ClubGiftReceiptParam;
import com.trading.backend.http.response.club.GiftReciptResponse;
import com.trading.backend.http.response.endpoint.FullScaleCouponVO;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.util.ContextHolder;
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
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author ~~ tradingmu
 * @date 10:23 03/02/22
 */
@Slf4j @Validated
@RestController
@Api(tags = "双币相关接口")

@ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", name = "x-gw-user", required = true, dataTypeClass = String.class),
        @ApiImplicitParam(paramType = "header", name = "access_token", required = true, dataTypeClass = String.class),
        @ApiImplicitParam(paramType = "header", name = "refresh_token", required = true, dataTypeClass = String.class)
})
public class DualController {

    final static private String WEB_PREFIX = "/v1/dual";
    final static private String POD_PREFIX = "/internal/v1/dual";

    @Autowired
    private ICouponService couponService;
    @Autowired
    private IDualServiceApi dualService;
    @Autowired
    private IPossesService possesService;


    @GetMapping(WEB_PREFIX + "/coupons")
    @ApiOperation(value = "查询体验金券列表")
    public Response<PageResult<FullScaleCouponVO>> getCoupon4Dual(@Validated TypedParam param) {
        PageResult<FullScaleCouponVO> result = couponService.getDualTrial();
        return Response.ok(result);
    }

    @Idempotent(uniqueKey = "coupon_id")
    @Authentication
    @PostMapping(WEB_PREFIX + "/receipt")
    @ApiOperation(value = "领取双币体验金券")
    public Response<GiftReciptResponse> dualReceive(@Validated @RequestBody ClubGiftReceiptParam param) {
        // couponService.clubRecevCheck(param.getHeaderUid(), param.getCouponId());
        couponService.typePermit(param.getCouponId(), CouponTypeEnum.TRIALFUND_TYPE.getCode());
        dualService.trialAllow(param.getHeaderUid());
        List<BasalExportPossessBO> receive = couponService.receive(
                param.getHeaderUid(), param.getCouponId(), null, PossessSourceEnum.DUAL_REWARD);
        return Response.ok(GiftReciptResponse.received(receive.get(0)));
    }

    @GetMapping(WEB_PREFIX + "/trial-recved")
    @ApiOperation(value = "根据体验金的id查询是否有领取")
    public Response<Boolean> trialRecved(@Validated @NotNull Long coupon_id) {

        boolean b = possesService.hasCoupon(ContextHolder.get().getXGwUser(), coupon_id);
        return Response.ok(b);
    }

}
