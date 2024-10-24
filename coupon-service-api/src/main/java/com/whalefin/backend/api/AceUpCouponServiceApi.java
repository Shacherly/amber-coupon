package com.trading.backend.api;

import com.trading.backend.common.http.PageResult;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.CouponCreateParam;
import com.trading.backend.http.request.CouponEditParam;
import com.trading.backend.http.request.aceup.CouponListReq;
import com.trading.backend.http.request.aceup.IssueListReq;
import com.trading.backend.http.response.aceup.CouponDetatilRes;
import com.trading.backend.http.response.aceup.CouponListRes;
import com.trading.backend.http.response.aceup.IssueDetailRes;
import com.trading.backend.http.response.aceup.IssueListRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "trading-coupon-service",
        path = "/internal/v1",
        contextId = "coupon"
)
public interface AceUpCouponServiceApi {

    @PostMapping(path = "/create")
    Response<Boolean> createCoupon(@RequestBody @Validated CouponCreateParam param);

    @PostMapping("/edit")
    Response<Boolean> editCoupon(@Validated @RequestBody CouponEditParam param);

    @PostMapping(path = {"/list"})
    Response<PageResult<CouponListRes>> getAllCouponList(@RequestBody CouponListReq param, @RequestParam Integer page , @RequestParam Integer page_size);

    @GetMapping(path = {"/detail"})
    Response<CouponDetatilRes> getCouponDetail(@RequestParam Long id);

    @PostMapping("/grant/list")
    Response<PageResult<IssueListRes>> getCouponGrants(@RequestBody IssueListReq param,@RequestParam Integer page , @RequestParam Integer page_size);

    @GetMapping(path = {"/grant/detail"})
    Response<IssueDetailRes> getConsumeDetail(@RequestParam Long id);

}
