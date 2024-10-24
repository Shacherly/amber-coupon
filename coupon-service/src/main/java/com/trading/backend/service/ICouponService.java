package com.trading.backend.service;


import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.http.PageResult;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.http.request.CouponConsumeParam;
import com.trading.backend.http.request.ExternalHeaderUid;
import com.trading.backend.http.request.ReddotReadParam;
import com.trading.backend.http.request.TypedParam;
import com.trading.backend.http.request.dual.DualConsumeParam;
import com.trading.backend.http.request.loan.LoanConsumeParam;
import com.trading.backend.coupon.http.response.*;
import com.trading.backend.http.response.endpoint.FullScaleCouponVO;
import com.trading.backend.pojo.CouponContributeParam;
import com.trading.backend.pojo.PossessDO;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface ICouponService {

    List<Coupon> getCoupons(List<Long> ids);

    void clubRecevCheck(String uid, Long couponId);

    void possessLimitCheck(String uid, Coupon coupon);

    boolean possessLimitPermit(String uid, Coupon coupon);

    List<String> possessLimitReduce(List<String> uids, Coupon coupon);

    void couponTotalCheck(long delta, List<Coupon> coupons);

    void statusCheck(List<Coupon> coupons);

    List<BasalExportPossessBO> receive(String uid, Long couponId, Long sourceId, PossessSourceEnum sourceEnum);

    List<BasalExportPossessBO> receive(String uid, List<Long> couponIds, Long sourceId, PossessSourceEnum sourceEnum);

    List<BasalExportPossessBO> receiveMultiUser(List<String> uids, List<Long> couponIds, Long sourceId, PossessSourceEnum sourceEnum, boolean preGrant);

    List<BasalExportPossessBO> eventCouponIssue(List<String> uids, List<Coupon> coupons, Long sourceId);

    int cashKycRemedy(List<CouponPossess> sources);

    int cashKycReactiv(List<String> possUids);

    void issuesIncrease(List<Long> couponIds);

    void issuesIncrease(List<Long> couponIds, int increase);

    void incrIssueReleasePrelock(List<Long> couponIds, int increase);

    void releasePrelock(List<Long> couponIds, int increase);

    Long checkRemainingAlert(Coupon coupon);

    List<Coupon> getAndPresentCheck(List<Long> couponIds);

    void remainCheck4Event(List<Coupon> coupons, long delta);

    // List<CouponPossessPO>
    List<PossesStatusVO> getCouponDetails(List<Long> ids);

    PossesStatusVO getSingleDetail(Long id);

    void checkPossessUsable(CouponPossess possess);

    void interCouponUsage(CouponConsumeParam param);

    void deductCouponUsage(LoanConsumeParam param);

    void dualProfitCouponUsage(DualConsumeParam param);

    void dualTrialCouponUsage(DualConsumeParam param);

    void cashCouponActivate(CouponContributeParam param, PossessDO possessDo);

    void reddotRead(ReddotReadParam param);

    List<CashGrantPopupVO> getCashGrantPopup(ExternalHeaderUid param);

    boolean cashPopupRead(ExternalHeaderUid param);

    TaskProgressRes taskProgress(String uid);

    List<CouponDetailVO> getCouponsDetail(List<Long> couponIds);

    BitInfoRes bitInfo();

    boolean registAfterActivity(String uid);

    void couponPreLock(List<Long> couponIds, Long preLock);

    List<Coupon> selectExample(Supplier<Example> supplier);

    Collection<Long> getBlueCouponIds();

    PageResult<FullScaleCouponVO> typedPaging(TypedParam param);

    PageResult<FullScaleCouponVO> getDualTrial();

    void typePermit(Long couponId, Integer type);
}
