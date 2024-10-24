package com.trading.backend.service;

import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.http.request.CouponPossessParam;
import com.trading.backend.http.request.FullScaleCouponParam;
import com.trading.backend.http.request.FullScalePossessParam;
import com.trading.backend.http.request.PossessAvailableParam;
import com.trading.backend.http.request.earn.EarnMatchParam;
import com.trading.backend.http.request.loan.LoanMatchParam;
import com.trading.backend.http.response.ConcisePossessVO;
import com.trading.backend.http.response.PossessAvailabeVO;
import com.trading.backend.http.response.dual.DualPossessVO;
import com.trading.backend.http.response.endpoint.FullScalePossessVO;
import com.trading.backend.http.response.club.ClubPossessVO;
import com.trading.backend.http.response.earn.EarnPossessVO;
import com.trading.backend.http.response.loan.LoanPossessVO;
import com.trading.backend.pojo.PossessDO;
import tk.mybatis.mapper.entity.Example;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public interface IPossesService {

    // List<PossessBO> getAvailablePossess(String uid);
    PossessDO getByPossessId(Long possessId);

    List<PossessDO> getTypedPossess(String uid, CouponTypeEnum typeEnum);

    List<PossessDO> getTypedPossess(String uid, List<Integer> types);

    List<PossessDO> getAppyScenePossess(String uid, CouponApplySceneEnum applyEnum);

    List<PossessDO> getAllAppyScenePossess(String uid, CouponApplySceneEnum applyEnum);

    List<PossessDO> getSortedScenedPossess(String uid, CouponApplySceneEnum applyEnum, Comparator<PossessDO> comparator);

    /**
     * @author ~~ trading.s
     * @date 11:14 01/18/22
     * @desc 返回单条记录，但我还是要用list接收
     */
    List<PossessDO> getOrderedPossessSingle(String uid, CouponApplySceneEnum applyEnum);

    List<LoanPossessVO> getLoanMatchs(LoanMatchParam param, List<LoanPossessVO> unMatch);

    List<EarnPossessVO> getEarnMatchs(EarnMatchParam param, List<EarnPossessVO> unMatch);

    List<DualPossessVO> getDualMatchs(String uid);

    List<ConcisePossessVO> getConcisePossess(CouponPossessParam param);

    List<FullScalePossessVO> getFullScalePossess(FullScalePossessParam param);

    List<FullScalePossessVO> getFullScalePossess(FullScaleCouponParam param);

    List<PossessAvailabeVO> getPossAvableWithType(PossessAvailableParam param);

    List<ClubPossessVO> getClubCoupons(String uid, List<Long> couponIds);

    boolean updatePossess(CouponPossess possess, Example example);

    void possesStageContributing(Long possessId, CouponPossess toUpdate);

    void releasePrelock(Long eventId, boolean pass);

    void issueNotify(List<BasalExportPossessBO> possesses, PossessSourceEnum sourceEnum);

    List<CouponPossess> selectExample(Supplier<Example> supplier);

    boolean hasCoupon(String uid, Long couponId);

}
