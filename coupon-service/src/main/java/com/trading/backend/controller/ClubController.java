package com.trading.backend.controller;

import com.trading.backend.annotation.Authentication;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.common.annotation.Idempotent;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.club.ClubGiftReceiptParam;
import com.trading.backend.http.response.club.GiftReciptResponse;
import com.trading.backend.service.ICouponService;
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
@Api(tags = "Club相关接口")

@ApiImplicitParams({
        @ApiImplicitParam(paramType = "header", name = "x-gw-user", required = true, dataTypeClass = String.class),
        @ApiImplicitParam(paramType = "header", name = "access_token", required = true, dataTypeClass = String.class),
        @ApiImplicitParam(paramType = "header", name = "refresh_token", required = true, dataTypeClass = String.class)
})
public class ClubController {

    @Autowired
    private ICouponService couponService;

    @Idempotent(uniqueKey = "coupon_id")
    @Authentication
    @PostMapping("/club/gift/receipt")
    @ApiOperation(value = "领取club会员礼券")

    public Response<GiftReciptResponse> clubGiftReceive(@Validated @RequestBody ClubGiftReceiptParam param) {

        couponService.clubRecevCheck(param.getHeaderUid(), param.getCouponId());
        List<BasalExportPossessBO> receive = couponService.receive(
                param.getHeaderUid(), param.getCouponId(), null, PossessSourceEnum.CLUB_GIFT);
        return Response.ok(GiftReciptResponse.received(receive.get(0)));
    }

    @Idempotent(uniqueKey = "coupon_id")
    @Authentication
    @PostMapping("/club/blue-tt/receipt")
    @ApiOperation(value = "领取BWC权益优惠券")

    public Response<GiftReciptResponse> bluettReceive(@Validated @RequestBody ClubGiftReceiptParam param) {

        couponService.clubRecevCheck(param.getHeaderUid(), param.getCouponId());
        List<BasalExportPossessBO> receive = couponService.receive(
                param.getHeaderUid(), param.getCouponId(), null, PossessSourceEnum.BWC_GIFT);
        return Response.ok(GiftReciptResponse.received(receive.get(0)));
    }

}

