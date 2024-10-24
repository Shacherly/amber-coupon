package com.trading.backend.mapper;


import com.trading.backend.domain.CouponPossess;
import com.trading.backend.http.response.aceup.IssueListVo;
import com.trading.backend.pojo.CashActivDO;
import com.trading.backend.http.request.aceup.IssueListReq;
import com.trading.backend.pojo.ExpireDO;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.pojo.WorthGrantDO;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponPossessDao {

    PossessDO getByPossessId(@Param("possessId") Long possessId);

    List<PossessDO> getUserPossess(@Param("uid") String uid, @Param("exprFrom") LocalDateTime exprFrom);

    List<PossessDO> getUserPossBySource(@Param("uid") String uid, @Param("source") Integer source);

    List<PossessDO> getValidPossess(@Param("uid") String uid);

    List<PossessDO> getInvalidPossess(@Param("uid") String uid, @Param("exprFrom") LocalDateTime exprFrom);

    List<PossessDO> getTypesPossess(@Param("uid") String uid, @Param("types") List<Integer> types);

    List<PossessDO> getIdsPossess(@Param("uid") String uid, @Param("possessIds") List<Long> possessIds);

    List<PossessDO> getAppyScenePossess(@Param("uid") String uid, @Param("scene") Integer scene);

    List<PossessDO> getAllAppyScenePossess(@Param("uid") String uid, @Param("scene") Integer scene);

    PossessDO getOrderedPossessSingle(@Param("uid") String uid, @Param("scene") Integer scene);

    int batchInsert(List<CouponPossess> records);

    int batchInsertWithoutId(List<CouponPossess> records);

    int batchUpdate(List<CouponPossess> records);

    List<PossessDO> getClubCoupons(@Param("couponIds") List<Long> couponIds);

    List<PossessDO> getClubPossess(
            @Param("uid") String uid, @Param("couponIds") List<Long> couponIds,
            @Param("monthStart") LocalDateTime monthStart, @Param("monthEnd") LocalDateTime monthEnd);

    List<PossessDO> getPeriodPossess(
            @Param("uid") String uid, @Param("couponIds") List<Long> couponIds,
            @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end);


    List<PossessDO> getBwcPeriodPossess(
            @Param("uid") String uid, @Param("couponIds") List<Long> couponIds,
            @Param("begin") LocalDateTime begin, @Param("end") LocalDateTime end
    );

    List<WorthGrantDO> getWorthGrants(
            @Param("couponType") Integer couponType,
            // @Param("nowTime") LocalDateTime nowTime,
            @Param("nowTimeOffset") LocalDateTime nowTimeOffset);


    CashActivDO getPendingCashActivDO(String uid, String businessId);

    List<IssueListVo> getIssueList(IssueListReq param);

    List<ExpireDO> getExprs(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}