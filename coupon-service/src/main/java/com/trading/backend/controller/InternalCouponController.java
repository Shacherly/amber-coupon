package com.trading.backend.controller;


import com.trading.backend.api.CouponServiceApi;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.common.annotation.Idempotent;
import com.trading.backend.common.enums.PossessSourceEnum;
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
import com.trading.backend.coupon.http.response.*;
import com.trading.backend.http.response.club.ClubPossessVO;
import com.trading.backend.http.response.dual.DualMatchedResponse;
import com.trading.backend.http.response.earn.EarnMatchedResponse;
import com.trading.backend.http.response.endpoint.FullScalePossessVO;
import com.trading.backend.http.response.loan.LoanMatchedResponse;
import com.trading.backend.service.ICouponDualService;
import com.trading.backend.service.ICouponEarnService;
import com.trading.backend.service.ICouponLoanService;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.util.Converter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author ~~ trading.s
 * @date 13:56 09/23/21
 * 父接口可以加上@ApiOperation，其他服务引用的时候可以生成接口文档
 */
@Slf4j
@RestController
@RequestMapping(path = { "/internal/v1"})
@Api(tags = "Internal-Api（pod之间调用）")
public class InternalCouponController implements CouponServiceApi {


    @Autowired
    private ICouponService couponService;
    @Autowired
    private IPossesService possesService;
    @Autowired
    private ICouponLoanService loanService;
    @Autowired
    private ICouponEarnService earnService;
    @Autowired
    private ICouponDualService dualService;


    @Override
    @ApiOperation(value = "BWC专属查券")
    public Response<List<FullScalePossessVO>> getFullScalePossess(FullScaleCouponParam param) {
        List<FullScalePossessVO> possess = possesService.getFullScalePossess(param);
        return Response.ok(possess);
    }

    @Override
    @ApiOperation(value = "1.获取用户拥有的优惠券")
    public Response<List<ConcisePossessVO>> getPossessCoupons(@Validated @RequestBody CouponPossessParam param) {

        List<ConcisePossessVO> possess = possesService.getConcisePossess(param);
        return Response.ok(possess);
    }


    @Override
    @ApiOperation(value = "2.获取用户的club优惠券")
    public Response<List<ClubPossessVO>> getClubPossess(@Validated @RequestBody ClubPossessParam param) {

        List<ClubPossessVO> details = possesService.getClubCoupons(param.getUid(), param.getCouponIds());
        // throw new BusinessException(ExceptionEnum.INSERT_ERROR);
        return Response.ok(details);
    }


    @Override
    @ApiOperation(value = "3.get matched coupons for earn")
    public Response<EarnMatchedResponse> getMatchedCoupons(@Validated @RequestBody EarnMatchParam param) {

        EarnMatchedResponse match = earnService.getMathCoupons(param);
        return Response.ok(match);
    }


    @Override
    @ApiOperation(value = "4.get matched coupons for loan")
    public Response<LoanMatchedResponse> getMatchedCoupons(@Validated @RequestBody LoanMatchParam param) {

        LoanMatchedResponse loanMatch = loanService.getMathCoupons(param);
        return Response.ok(loanMatch);
    }


    @Override
    @ApiOperation(value = "5.get matched coupons for dual收益增强")
    public Response<DualMatchedResponse> getMatchedCoupons(@Validated @RequestBody DualMathParam param) {

        DualMatchedResponse dualMatch = dualService.getMathCoupons(param.getUid());
        return Response.ok(dualMatch);
    }

    @Override
    @Idempotent(uniqueKey = "coupon_id")
    @ApiOperation(value = "5.refrerral receive coupons")
    public Response<PossessIdResponse> referralIssue(@Validated @RequestBody CouponReceiptParam param) {

        List<BasalExportPossessBO> receive = couponService.receive(param.getUid(), param.getCouponId(), null, PossessSourceEnum.REFERRAL_REWARD);
        return Response.ok(new PossessIdResponse(receive.get(0).getPossesssId()));
    }

    @Override
    @Idempotent(uniqueKey = "coupon_id")
    @ApiOperation(value = "双币领券")
    public Response<PossessIdResponse> dualIssue(CouponReceiptParam param) {
        List<BasalExportPossessBO> receive = couponService.receive(param.getUid(), param.getCouponId(), null, PossessSourceEnum.DUAL_REWARD);
        return Response.ok(new PossessIdResponse(receive.get(0).getPossesssId()));
    }

    @Override
    @ApiOperation(value = "批量领券接口")
    public Response<Boolean> receiveCouponMultiuser(BatchReceiptParam param) {

        couponService.receiveMultiUser(param.getUids(), param.getCouponIds(), null, PossessSourceEnum.INITIATIVE_RECEIVE, false);
        return Response.ok(true);
    }

    /**
     * 用券时通过同步调用更新状态，赎回时通过kafka异步更新状态
     * @param param
     * @return
     */
    @Override
    @ApiOperation(value = "理财用券")
    public Response<Boolean> useInterestCoupon(@Validated @RequestBody CouponConsumeParam param) {

        couponService.interCouponUsage(param);
        return Response.ok(true);
    }

    @Override
    @ApiOperation(value = "借贷用券")
    public Response<Boolean> useDeductCoupon(@Validated @RequestBody LoanConsumeParam param) {

        couponService.deductCouponUsage(param);
        return Response.ok(true);
    }

    @Override
    @ApiOperation(value = "双币使用收益增强券")
    public Response<Boolean> useDualProfitCoupon(DualConsumeParam param) {
        couponService.dualProfitCouponUsage(param);
        return Response.ok(true);
    }

    @Override
    @ApiOperation(value = "双币使用体验金券")
    public Response<Boolean> useDualTrialCoupon(DualConsumeParam param) {
        couponService.dualTrialCouponUsage(param);
        return Response.ok(true);
    }

    @Override
    @ApiOperation(value = "根据possess_id获取券详情配置")
    public Response<List<PossessAvailabeVO>> getByPossessIds(PossessAvailableParam param) {

        List<PossessAvailabeVO> detail = possesService.getPossAvableWithType(param);
        return Response.ok(detail);
    }

    @Override
    @ApiOperation(value = "Get All Coupons 4 Testing", hidden = true)
    public Response<List<CouponListVO>> allCoupons() {

        return null;
    }

    @Override
    @ApiOperation(value = "根据coupon_ids获取券详情配置")
    public Response<List<CouponDetailVO>> getDetailsByIds(CouponIds param) {

        List<CouponDetailVO> detail = couponService.getCouponsDetail(param.getCouponIds());
        return Response.ok(detail);
    }

    @GetMapping("/task/progress")
    @ApiOperation(value = "新手任务进度查询(内部接口)", notes = "新手任务进度查询(内部接口)")
    public Response<TaskProgressAggreRes> taskProgress(@RequestParam String uid) {
        return Response.ok(Converter.fromTaskProgress(couponService.taskProgress(uid)));
    }

    @Override
    public Response<PossessIdResponse> easterRecv(CouponReceiptParam param) {
        List<BasalExportPossessBO> receive = couponService.receive(param.getUid(), param.getCouponId(), null, PossessSourceEnum.EASTER_PRIZE);
        return Response.ok(new PossessIdResponse(receive.get(0).getPossesssId()));
    }
}
