package com.trading.backend.util;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import com.alibaba.fastjson.JSONObject;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.bo.WorthToGrantBO;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.ExportableCoinRule;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.domain.ProfitCouponRule;
import com.trading.backend.domain.TrialCouponRule;
import com.trading.backend.domain.base.CouponCoinRule;
import com.trading.backend.domain.base.CouponRuleBase;
import com.trading.backend.domain.vo.CashCouponRuleVO;
import com.trading.backend.domain.vo.CouponPolymericRuleVO;
import com.trading.backend.domain.vo.DeductCouponRuleVO;
import com.trading.backend.domain.vo.InterCouponRuleVO;
import com.trading.backend.domain.vo.ProfitCouponRuleVO;
import com.trading.backend.domain.vo.TrialCouponRuleVO;
import com.trading.backend.http.response.ConcisePossessVO;
import com.trading.backend.http.response.PossesStatusVO;
import com.trading.backend.http.response.PossessAvailabeVO;
import com.trading.backend.http.response.dual.DualPossessVO;
import com.trading.backend.http.response.earn.EarnPossessVO;
import com.trading.backend.http.response.endpoint.FullScaleCouponVO;
import com.trading.backend.http.response.endpoint.FullScalePossessVO;
import com.trading.backend.http.response.loan.LoanPossessVO;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.pojo.WorthGrantDO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * @author ~~ trading mu
 * @date 10:46 03/03/22
 */
public class BeanMapper {

    private static final Map<Class<?>, Supplier<CouponCoinRule>> COUPON_RULEVO_BUILDER;

    static {
        COUPON_RULEVO_BUILDER = new HashMap<>(16);
        COUPON_RULEVO_BUILDER.put(InterCouponRule.class, () -> ReflectUtil.newInstance(InterCouponRuleVO.class));
        COUPON_RULEVO_BUILDER.put(CashCouponRule.class, () -> ReflectUtil.newInstance(CashCouponRuleVO.class));
        COUPON_RULEVO_BUILDER.put(DeductCouponRule.class, () -> ReflectUtil.newInstance(DeductCouponRuleVO.class));
        COUPON_RULEVO_BUILDER.put(TrialCouponRule.class, () -> ReflectUtil.newInstance(TrialCouponRuleVO.class));
        COUPON_RULEVO_BUILDER.put(ProfitCouponRule.class, () -> ReflectUtil.newInstance(ProfitCouponRuleVO.class));
    }


    // private
    // public static PossessBO map(PossessDO source) {
    //     PossessBO bo = new PossessBO();
    //     // rule 从json -> Obj
    //     // TODO: 10/03/21
    //     return bo;
    // }
    private static Builder<BasalExportPossessBO> possessBuilder(
            Supplier<? extends BasalExportPossessBO> instantiator, PossessDO source) {

        Builder<BasalExportPossessBO> builder = Builder.of(instantiator);
        builder.with(BasalExportPossessBO::setCouponId, source.getCouponId())
               .with(BasalExportPossessBO::setPossesssId, source.getPossessId())
               .with(BasalExportPossessBO::setCouponName, source.getCouponName())
               .with(BasalExportPossessBO::setCouponTitle, source.toLocalized(source.getCouponTitle()))
               .with(BasalExportPossessBO::setCouponDescr, source.toLocalized(source.getCouponDescr()))
               .with(BasalExportPossessBO::setDescr, source.toLocalized(source.getCouponDescr()))
               .with(BasalExportPossessBO::setCouponType, source.getCouponType())
               // .with(BasalExportPossessBO::setPossessStage, source.getPossessStage())
               .with(BasalExportPossessBO::setBusinessStage, source.getBusinessStage())
               .with(BasalExportPossessBO::setAvailableStart, source.toEpochMilli(source.getAvailableStart()))
               .with(BasalExportPossessBO::setAvailableEnd, source.toEpochMilli(source.getAvailableEnd()))
               .with(BasalExportPossessBO::setOverlay, source.getOverlay())
               .with(BasalExportPossessBO::setWorth, NumberCriteria.stripTrailing(source.getWorth()))
               .with(BasalExportPossessBO::setWorthCoin, StringUtils.upperCase(source.getWorthCoin()))
               .with(BasalExportPossessBO::setReceiveTime, source.toEpochMilli(source.getReceiveTime()))
               .with(BasalExportPossessBO::setRedirectUrl, source.getRedirectUrl());

        return builder;
    }

    private static Builder<BasalExportPossessBO> possessBuilder(
            Supplier<? extends BasalExportPossessBO> instantiator, Coupon source) {

        Builder<BasalExportPossessBO> builder = Builder.of(instantiator);
        builder.with(BasalExportPossessBO::setCouponId, source.getId())
               .with(BasalExportPossessBO::setCouponName, source.getName())
               .with(BasalExportPossessBO::setCouponTitle, source.toLocalized(source.getTitle()))
               .with(BasalExportPossessBO::setCouponDescr, source.toLocalized(source.getDescr()))
               .with(BasalExportPossessBO::setDescr, source.toLocalized(source.getDescr()))
               .with(BasalExportPossessBO::setCouponType, source.getType())
               .with(BasalExportPossessBO::setOverlay, source.getOverlay())
               .with(BasalExportPossessBO::setWorth, NumberCriteria.stripTrailing(source.getWorth()))
               .with(BasalExportPossessBO::setWorthCoin, StringUtils.upperCase(source.getWorthCoin()))
               .with(BasalExportPossessBO::setRedirectUrl, source.getRedirectUrl());

        return builder;
    }


    public static BasalExportPossessBO toBasalPossessVo(PossessDO source) {
        Builder<BasalExportPossessBO> builder = possessBuilder(BasalExportPossessBO::new, source);
        return builder.build();
    }

    public static ConcisePossessVO toConcisePossessVo(PossessDO source) {
        Builder<BasalExportPossessBO> builder = possessBuilder(ConcisePossessVO::new, source);
        return (ConcisePossessVO) builder.build();
    }

    public static FullScalePossessVO toFullScalePossessVo(PossessDO source) {
        Builder<BasalExportPossessBO> builder = possessBuilder(FullScalePossessVO::new, source);
        FullScalePossessVO possessVo = (FullScalePossessVO) builder.build();
        CouponPolymericRuleVO couponRule = Converter.polymericDeserialize(source.getCouponRule());
        possessVo.setCouponRule(couponRule);
        possessVo.setReddotAppear(!source.getHasRead());
        possessVo.setExptEndTime(source.toEpochMilli(source.getExptEndTime()));
        possessVo.setConsumeTime(source.toEpochMilli(source.getConsumeTime()));
        possessVo.setApplyScene(source.getApplyScene());
        return possessVo;
    }

    public static FullScalePossessVO toFullScalePossessVo(Coupon source) {
        Builder<BasalExportPossessBO> builder = possessBuilder(FullScalePossessVO::new, source);
        FullScalePossessVO possessVo = (FullScalePossessVO) builder.build();
        CouponPolymericRuleVO couponRule = Converter.polymericDeserialize(source.getRule());
        possessVo.setCouponRule(couponRule);
        possessVo.setExptEndTime(source.toEpochMilli(source.getExprAtEnd()));
        possessVo.setApplyScene(source.getApplyScene());
        return possessVo;
    }

    public static EarnPossessVO toEarnPossessVo(PossessDO source) {
        Builder<BasalExportPossessBO> builder = possessBuilder(EarnPossessVO::new, source);
        EarnPossessVO buildVo = (EarnPossessVO) builder.build();
        InterCouponRuleVO couponRule = JSONObject.toJavaObject(source.getCouponRule(), InterCouponRuleVO.class);
        buildVo.setCouponRule(couponRule);
        return buildVo;
    }

    public static LoanPossessVO toLoanPossessVo(PossessDO source) {
        Builder<BasalExportPossessBO> builder = possessBuilder(LoanPossessVO::new, source);
        LoanPossessVO buildVo = (LoanPossessVO) builder.build();
        DeductCouponRuleVO couponRule = JSONObject.toJavaObject(source.getCouponRule(), DeductCouponRuleVO.class);
        buildVo.setCouponRule(couponRule);
        return buildVo;
    }

    public static DualPossessVO toDualPossessVo(PossessDO source) {
        Builder<BasalExportPossessBO> builder = possessBuilder(DualPossessVO::new, source);
        DualPossessVO buildVo = (DualPossessVO) builder.build();
        ProfitCouponRuleVO couponRule = JSONObject.toJavaObject(source.getCouponRule(), ProfitCouponRuleVO.class);
        buildVo.setCouponRule(couponRule);
        return buildVo;
    }

    public static PossessAvailabeVO toPossessAvalVo(PossessDO source) {
        Builder<BasalExportPossessBO> builder = possessBuilder(PossessAvailabeVO::new, source);
        PossessAvailabeVO buildVo = (PossessAvailabeVO) builder.build();
        CouponRuleBase couponRule = Converter.deserialize(source.getCouponRule(), source.getCouponType());

        CouponCoinRule rule1 = COUPON_RULEVO_BUILDER.get(couponRule.getClass()).get();
        Objects.requireNonNull(rule1);
        BeanUtils.copyProperties(couponRule, rule1);
        List<ExportableCoinRule> collect =
                couponRule.getCoinRules().stream().map(coinRule -> {
                              return new ExportableCoinRule()
                                      .setApplyCoin(coinRule.getApplyCoin())
                                      .setMinAmount(coinRule.getMinAmount())
                                      .setMaxAmount(coinRule.getMaxAmount());
                          })
                          .collect(Collectors.toList());
        ReflectUtil.invoke(rule1, "setCoinRules", collect);

        buildVo.setCouponRule(rule1);
        return buildVo;
    }

    // public static ClubPossessVO toPossessClubVo(PossessDO source) {
    //     Builder<ClubPossessVO> builder = Builder.of(ClubPossessVO::new);
    //     builder.with(ClubPossessVO::setCouponId, source.getCouponId())
    //            .with(ClubPossessVO::setCouponType, source.getCouponType())
    //            .with(ClubPossessVO::setWorth, source.toPlainString(source.getWorth()))
    //            .with(ClubPossessVO::setWorthCoin, source.getWorthCoin())
    //            .with(ClubPossessVO::setBusinessStage, source.getBusinessStage())
    //            .with(ClubPossessVO::setPossessStage, source.getPossessStage())
    //            .with(ClubPossessVO::setExprTime, source.toEpochMilli(source.getAvailableEnd()));
    //     return builder.build();
    // }

    // public static ExportPossessVO toCouponVo(PossessBO source) {
    //     ExportPossessVO vo = new ExportPossessVO();
    //     // TODO: 10/03/21
    //     return vo;
    // }

    public static PossesStatusVO toShortDetail(Coupon source) {
        Builder<PossesStatusVO> builder = Builder.of(PossesStatusVO::new);
        builder.with(PossesStatusVO::setCouponId, source.getId())
               .with(PossesStatusVO::setCouponName, source.getName())
               .with(PossesStatusVO::setCouponTitle, source.toLocalized(source.getTitle()))
               .with(PossesStatusVO::setCouponType, source.getType())
               .with(PossesStatusVO::setStatus, source.getStatus())
               .with(PossesStatusVO::setCouponDesrc, source.toLocalized(source.getDescr()))
               .with(PossesStatusVO::setOverlay, source.getOverlay())
               .with(PossesStatusVO::setRedirectUrl, source.getRedirectUrl())
               .with(PossesStatusVO::setWorthCoin, source.getWorthCoin())
               .with(PossesStatusVO::setWorth, source.toPlainString(source.getWorth()))
               .with(PossesStatusVO::setRemark, source.getRemark())
               .with(PossesStatusVO::setCouponRule, source.getRule());
        return builder.build();
    }

    public static WorthToGrantBO toGrantBo(WorthGrantDO source) {
        Builder<WorthToGrantBO> builder = Builder.of(WorthToGrantBO::new);
        builder.with(WorthToGrantBO::setExptEndTime, source.toEpochMilli(source.getExptEndTime()))
               .with(WorthToGrantBO::setApplyScene, source.getApplyScene())
               .with(WorthToGrantBO::setPossessId, source.getPossessId())
               .with(WorthToGrantBO::setUid, source.getUid());
        return builder.build();
    }

    public static FullScaleCouponVO toCouponListVo(Coupon source) {
        Builder<FullScaleCouponVO> builder = Builder.of(FullScaleCouponVO::new);
        builder.with(FullScaleCouponVO::setCouponId, source.getId())
               .with(FullScaleCouponVO::setCouponName, source.getName())
               .with(FullScaleCouponVO::setCouponTitle, source.toLocalized(source.getTitle()))
               .with(FullScaleCouponVO::setCouponDescr, source.toLocalized(source.getDescr()))
               .with(FullScaleCouponVO::setCouponType, source.getType())
               // .with(FullScaleCouponVO::setExprAtBegin, Optional.ofNullable(source.getExprAtStart()).map(TemporalUtil::toEpochMilli).orElse(null))
               // .with(FullScaleCouponVO::setExprAtEnd, Optional.ofNullable(source.getExprAtEnd()).map(TemporalUtil::toEpochMilli).orElse(null))
               .with(FullScaleCouponVO::setWorthCoin, source.getWorthCoin())
               .with(FullScaleCouponVO::setWorth, NumberUtil.toStr(source.getWorth()));
        return builder.build();
    }
}
