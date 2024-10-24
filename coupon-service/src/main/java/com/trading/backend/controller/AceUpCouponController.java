package com.trading.backend.controller;

import com.trading.backend.common.http.PageResult;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponEvent;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.CouponCreateParam;
import com.trading.backend.http.request.CouponEditParam;
import com.trading.backend.http.request.aceup.CouponListReq;
import com.trading.backend.http.request.aceup.IssueListReq;
import com.trading.backend.http.request.event.EventApprovalParam;
import com.trading.backend.http.request.event.EventCreateParam;
import com.trading.backend.http.request.event.EventListParam;
import com.trading.backend.http.response.aceup.CouponDetatilRes;
import com.trading.backend.http.response.aceup.CouponListRes;
import com.trading.backend.http.response.aceup.IssueDetailRes;
import com.trading.backend.http.response.aceup.IssueListRes;
import com.trading.backend.http.response.event.EventDetailVO;
import com.trading.backend.http.response.event.EventListVO;
import com.trading.backend.service.IAceUpEventService;
import com.trading.backend.service.ICouponAdminService;
import com.trading.backend.service.IValidator;
import com.trading.backend.util.Converter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author ~~ trading.s
 * @date 13:56 09/23/21
 */
@Slf4j
@Validated
@RestController
@RequestMapping(path = "/internal/v1")
@Api(tags = "ACEUP接口")
public class AceUpCouponController {

    @Autowired
    private ICouponAdminService couponAdminService;
    @Autowired
    private IAceUpEventService aceUpEventService;
    @Autowired
    private IValidator validator;


    @PostMapping(path = "/create")
    @ApiOperation(value = "新增券（不同类型的券传不同的rule json）")
    public Response<Long> createCoupon(@RequestBody @Validated CouponCreateParam param) {

        validator.validate(param);
        Coupon source = Converter.fromRequest(param);
        return Response.ok(couponAdminService.saveOrUpdate(source));

    }

    @PostMapping("/edit")
    @ApiOperation(value = "优惠券券编辑")
    public Response<Boolean> editCoupon(@Validated @RequestBody CouponEditParam param) {

        Coupon source = Converter.fromRequest(param);
        couponAdminService.saveOrUpdate(source);
        return Response.ok(true);
    }

    @GetMapping(path = {"/list"})
    @ApiOperation(value = "券管理列表List")
    public Response<PageResult<CouponListRes>> getAllCouponList(CouponListReq param, @RequestParam Integer page ,@RequestParam Integer page_size) {
        PageResult<CouponListRes> result = couponAdminService.queryCouponList(param);
        return Response.ok(result);
    }

    @GetMapping(path = {"/detail"})
    @ApiOperation(value = "券详情")
    public Response<CouponDetatilRes> getCouponDetail(@RequestParam Long id) {
        return Response.ok(couponAdminService.queryCouponDetail(id));
    }

    @GetMapping(path = {"/details"})
    @ApiOperation(value = "多张券详情")
    public Response<List<CouponDetatilRes>> getCouponDetails(@RequestParam List<Long> ids) {
        return Response.ok(couponAdminService.queryCouponDetails(ids));
    }

    @GetMapping("/grant/list")
    @ApiOperation(value = "发放明细记录列表")
    public Response<PageResult<IssueListRes>> getCouponGrants(IssueListReq param,@RequestParam Integer page , @RequestParam Integer page_size) {
        PageResult<IssueListRes> result = couponAdminService.queryCouponPossessList(param);
        return Response.ok(result);
    }


    @GetMapping(path = {"/grant/detail"})
    @ApiOperation(value = "发放明细详情")
    public Response<IssueDetailRes> getConsumeDetail(@RequestParam Long id) {
        return Response.ok(couponAdminService.queryConsumeDetail(id));
    }

    @GetMapping(path = {"/grant/revoke"})
    @ApiOperation(value = "撤回券")
    public Response revokeCoupon(@ApiParam(name = "id",value = "发放id(未使用的券才支持撤回)") @RequestParam Long id) {
        couponAdminService.revokeCoupon(id);
        return Response.ok(null);
    }


    //@PostMapping("/list/consume")
    @ApiOperation(value = "用券记录列表")
    public Response<PageResult<?>> getCouponUsages() {
        return null;
    }


    @PostMapping("/event/create")
    @ApiOperation(value = "创建优惠券event")
    public Response<Long> createEvent(@RequestBody @Validated EventCreateParam param) {
        CouponEvent event = aceUpEventService.createEvent(param);
        return Response.ok(event.getId());
    }

    @GetMapping("/event/list")
    @ApiOperation(value = "获取EventList")
    public Response<PageResult<EventListVO>> getEventList(EventListParam param) {
        PageResult<EventListVO> eventPage = aceUpEventService.getEventPage(param);
        return Response.ok(eventPage);
    }

    @GetMapping("/event/detail")
    @ApiOperation(value = "获取EventDetail")
    public Response<EventDetailVO> getEventDetail(@NotNull Long event_id) {
        EventDetailVO eventDetail = aceUpEventService.getEventDetail(event_id);
        return Response.ok(eventDetail);
    }


    @PostMapping("/event/approval")
    @ApiOperation(value = "审批事件管理-提交审批、审批通过或拒绝")
    public Response<Boolean> approvalEvent(@RequestBody @Validated EventApprovalParam param) {

        aceUpEventService.eventApproval(param);
        return Response.ok(true);
    }

}
