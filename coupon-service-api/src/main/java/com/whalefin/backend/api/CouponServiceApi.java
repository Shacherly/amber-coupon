package com.trading.backend.api;


import com.trading.backend.api.fallback.CouponApiFallbackFactory;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.BatchReceiptParam;
import com.trading.backend.http.request.CouponConsumeParam;
import com.trading.backend.http.request.CouponIds;
import com.trading.backend.http.request.CouponPossessParam;
import com.trading.backend.http.request.CouponReceiptParam;
import com.trading.backend.http.request.FullScaleCouponParam;
import com.trading.backend.http.request.PossessAvailableParam;
import com.trading.backend.http.request.club.ClubPossessParam;
import com.trading.backend.http.request.dual.DualConsumeParam;
import com.trading.backend.http.request.dual.DualMathParam;
import com.trading.backend.http.request.earn.EarnMatchParam;
import com.trading.backend.http.request.loan.LoanConsumeParam;
import com.trading.backend.http.request.loan.LoanMatchParam;
import com.trading.backend.http.response.CouponDetailVO;
import com.trading.backend.http.response.CouponListVO;
import com.trading.backend.http.response.ConcisePossessVO;
import com.trading.backend.http.response.PossessAvailabeVO;
import com.trading.backend.http.response.club.ClubPossessVO;
import com.trading.backend.http.response.dual.DualMatchedResponse;
import com.trading.backend.http.response.earn.EarnMatchedResponse;
import com.trading.backend.http.response.endpoint.FullScalePossessVO;
import com.trading.backend.http.response.loan.LoanMatchedResponse;
import com.trading.backend.http.response.PossessIdResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


/**
 * @author ~~ trading.s
 * @date 20:41 09/24/21
 */
@FeignClient(
        url = "${trading.domain}",
        name = "trading-coupon-service",
        path = "/coupon/internal/v1",
        contextId = "coupon",
        fallbackFactory = CouponApiFallbackFactory.class
)
public interface CouponServiceApi {


    @PostMapping("/possess/fullscale/acessible")
    Response<List<FullScalePossessVO>> getFullScalePossess(@Validated @RequestBody FullScaleCouponParam param);


    @PostMapping("/possess/brief/list")
    Response<List<ConcisePossessVO>> getPossessCoupons(@Validated @RequestBody CouponPossessParam param);


    @PostMapping("/club/possess")
    Response<List<ClubPossessVO>> getClubPossess(@Validated @RequestBody ClubPossessParam param);


    @PostMapping("/earn/matched/list")
    Response<EarnMatchedResponse> getMatchedCoupons(@Validated @RequestBody EarnMatchParam param);


    @PostMapping("/loan/matched/list")
    Response<LoanMatchedResponse> getMatchedCoupons(@Validated @RequestBody LoanMatchParam param);


    @PostMapping("/dual/matched/list")
    Response<DualMatchedResponse> getMatchedCoupons(@Validated @RequestBody DualMathParam param);


    @PostMapping("/receipt/referral")
    Response<PossessIdResponse> referralIssue(@Validated @RequestBody CouponReceiptParam param);

    @PostMapping("/receipt/multiuser")
    Response<Boolean> receiveCouponMultiuser(@Validated @RequestBody BatchReceiptParam param);

    @PostMapping("/receipt/dual")
    Response<PossessIdResponse> dualIssue(@Validated @RequestBody CouponReceiptParam param);


    @PostMapping("/consume/earn")
    Response<Boolean> useInterestCoupon(@Validated @RequestBody CouponConsumeParam param);


    @PostMapping("/consume/loan")
    Response<Boolean> useDeductCoupon(@Validated @RequestBody LoanConsumeParam param);


    @PostMapping("/consume/dual/profitincre")
    Response<Boolean> useDualProfitCoupon(@Validated @RequestBody DualConsumeParam param);

    @PostMapping("/consume/dual/trial")
    Response<Boolean> useDualTrialCoupon(@Validated @RequestBody DualConsumeParam param);


    @PostMapping("/possess/available/verify")
    Response<List<PossessAvailabeVO>> getByPossessIds(@Validated @RequestBody PossessAvailableParam param);


    @PostMapping("/entire/list")
    Response<List<CouponListVO>> allCoupons();

    @PostMapping("/details")
    Response<List<CouponDetailVO>> getDetailsByIds(@Validated @RequestBody CouponIds param);

    @PostMapping("/receipt/easter")
    Response<PossessIdResponse> easterRecv(@Validated @RequestBody CouponReceiptParam param);
}
