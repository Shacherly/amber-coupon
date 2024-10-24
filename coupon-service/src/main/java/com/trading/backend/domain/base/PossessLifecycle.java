package com.trading.backend.domain.base;

import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.enums.PossessBusinesStageEnum;
import com.trading.backend.common.enums.PossessStageEnum;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.pojo.CouponContributeParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;


// @SuppressWarnings("all")
public interface PossessLifecycle {

    CouponPossess setPossessStage(Integer stage);

    CouponPossess setBusinessStage(Integer stage);

    CouponPossess setConsumeTime(LocalDateTime time);

    CouponPossess setExptEndTime(LocalDateTime time);

    CouponPossess setBusinessId(String businessId);

    CouponPossess setBusinessCoin(String coin);

    CouponPossess setExptDiscount(BigDecimal discount);

    CouponPossess setActualEndAt(LocalDateTime time);

    Integer getCouponType();

    default void born() {
        setPossessStage(PossessStageEnum.ENABLE.getCode());
        setBusinessStage(PossessBusinesStageEnum.DEFAULT_UNUSED.getCode());
    }


    default void expire() {
        setPossessStage(PossessStageEnum.DISABLE.getCode());
        setBusinessStage(PossessBusinesStageEnum.EXPIRED.getCode());
    }

    default void contribute(CouponContributeParam param, BigDecimal discount) {
        LocalDateTime now = LocalDateTime.now();
        CouponTypeEnum typeEnum = CouponTypeEnum.getByCode(getCouponType());
        if (typeEnum == CouponTypeEnum.CASHRETURN_TYPE) {
            setBusinessStage(PossessBusinesStageEnum.CONTRIBUTING.getCode());
        }
        else {
            setPossessStage(PossessStageEnum.DISABLE.getCode());
            setBusinessStage(PossessBusinesStageEnum.COMPLETE.getCode());
        }
        setConsumeTime(now);
        setBusinessId(param.getBusinessId());
        setBusinessCoin(param.getBusinessCoin());
        setExptDiscount(discount);
        setExptEndTime(now.plus(param.getDuration(), param.getUnit()));
    }

    default void breakOff() {
        LocalDateTime now = LocalDateTime.now();
        setPossessStage(PossessStageEnum.DISABLE.getCode());
        setBusinessStage(PossessBusinesStageEnum.BREAK_OFF.getCode());
        setActualEndAt(now);
    }

    default void accomplish() {
        LocalDateTime now = LocalDateTime.now();
        setPossessStage(PossessStageEnum.DISABLE.getCode());
        setBusinessStage(PossessBusinesStageEnum.COMPLETE.getCode());
        setActualEndAt(now);
    }
}
