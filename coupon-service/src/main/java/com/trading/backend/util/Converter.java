package com.trading.backend.util;


import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.bo.TaskStatusBo;
import com.trading.backend.bo.TaskTimeBo;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.enums.EventStageEnum;
import com.trading.backend.common.enums.EventTypeEnum;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.util.Functions;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.CouponDescrTemplate;
import com.trading.backend.domain.CouponEvent;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.domain.ProfitCouponRule;
import com.trading.backend.domain.TrialCouponRule;
import com.trading.backend.domain.vo.CouponPolymericRuleVO;
import com.trading.backend.http.request.CouponCreateParam;
import com.trading.backend.http.request.CouponEditParam;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.base.CouponRuleBase;
import com.trading.backend.http.request.aceup.DescrTemplateCreateParam;
import com.trading.backend.http.request.aceup.DescrTemplateUpdateParam;
import com.trading.backend.http.request.event.EventCreateParam;
import com.trading.backend.http.response.TaskProgressAggreRes;
import com.trading.backend.http.response.TaskProgressRes;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author ~~ trading.s
 * @date 13:38 09/24/21
 */
public class Converter {

    final static private Map<CouponTypeEnum, Function<CouponCreateParam, CouponRuleBase>> COUPON_RULE_GETTER;

    final static private Map<CouponTypeEnum, Function<JSONObject, CouponRuleBase>> COUPON_RULE_BUILDER;
    final static private BigDecimal HUNDRED = new BigDecimal("100");

    static {
        COUPON_RULE_GETTER = new HashMap<>(16);
        COUPON_RULE_GETTER.put(INTEREST_TYPE, CouponCreateParam::getInterCouponRule);
        COUPON_RULE_GETTER.put(DEDUCTION_TYPE, CouponCreateParam::getDeductCouponRule);
        COUPON_RULE_GETTER.put(CASHRETURN_TYPE, CouponCreateParam::getCashCouponRule);
        COUPON_RULE_GETTER.put(TRIALFUND_TYPE, CouponCreateParam::getTrialCouponRule);
        COUPON_RULE_GETTER.put(PROFITINCRE_TYPE, CouponCreateParam::getProfitCouponRule);

        COUPON_RULE_BUILDER = new HashMap<>(16);
        COUPON_RULE_BUILDER.put(INTEREST_TYPE, json -> JSON.toJavaObject(json, InterCouponRule.class));
        COUPON_RULE_BUILDER.put(DEDUCTION_TYPE, json -> JSON.toJavaObject(json, DeductCouponRule.class));
        COUPON_RULE_BUILDER.put(CASHRETURN_TYPE, json -> JSON.toJavaObject(json, CashCouponRule.class));
        COUPON_RULE_BUILDER.put(TRIALFUND_TYPE, json -> JSON.toJavaObject(json, TrialCouponRule.class));
        COUPON_RULE_BUILDER.put(PROFITINCRE_TYPE, json -> JSON.toJavaObject(json, ProfitCouponRule.class));
    }


    private static LocalDateTime ofEpochMilli(Long millis) {
        if (Objects.isNull(millis)) return null;
        return DateUtil.toLocalDateTime(Instant.ofEpochMilli(millis));
    }


    private static CouponRuleBase ofCreateParamRule(CouponCreateParam param) {
        return COUPON_RULE_GETTER.get(getByCode(param.getType())).apply(param);
    }

    private static JSONObject withJSONRule(CouponCreateParam param) {
        return (JSONObject) JSONObject.toJSON(ofCreateParamRule(param));
    }

    public static Coupon fromRequest(CouponCreateParam param) {
        CouponTypeEnum typeEnum = getByCode(param.getType());
        Builder<Coupon> builder = Builder.of(Coupon::new);
        builder.with(Coupon::setName, param.getCouponName())
               .with(Coupon::setTitle, JSONObject.toJSONString(param.getMultiLanTitle()))
               .with(Coupon::setStatus, param.getStatus())
               .with(Coupon::setType, param.getType())
               .with(Coupon::setDescr, JSONObject.toJSONString(param.getMultiLanDesc()))
               .with(Coupon::setOverlay, param.getOverlay())
               .with(Coupon::setTotal, param.getTotal())
               .with(Coupon::setPossessLimit, param.getPossessLimit())
               // .with(Coupon::setExprInDays, param.getExprInDays())
               // .with(Coupon::setExprAtStart, ofEpochMilli(param.getExprAtStart()))
               // .with(Coupon::setExprAtEnd, ofEpochMilli(param.getExprAtEnd()))
               .with(Coupon::setRedirectUrl, param.getRedirectUrl())
               .with(Coupon::setRule, withJSONRule(param))
               .with(Coupon::setWorthCoin, StringUtils.upperCase(param.getWorthCoin()))
               // .with(Coupon::setWorth, NumberCriteria.defaultScale(param.getWorth()))
               .with(Coupon::setRemark, param.getRemark())
               .with(Coupon::setGrantApproval, param.getGrantApproval())
               .with(Coupon::setApplyScene, param.getApplyScene());
        Optional.ofNullable(param.getExprInDays())
                .map(val -> {
                    builder.with(Coupon::setExprInDays, val);
                    return 1;
                })
                .orElseGet(() -> {
                    Optional.ofNullable(param.getExprAtStart())
                            .ifPresent(val -> {
                                builder.with(Coupon::setExprAtStart, ofEpochMilli(val))
                                       .with(Coupon::setExprAtEnd, ofEpochMilli(param.getExprAtEnd()));
                            });
                    return 1;
                });
        BigDecimal worth = NumberCriteria.defaultScale(param.getWorth());
        if (typeEnum == DEDUCTION_TYPE && param.getDeductCouponRule().getDeductWay() == 1) {
            builder.with(Coupon::setWorth, BigDecimal.ONE.subtract(worth));
        }
        else {
            builder.with(Coupon::setWorth, worth);
        }
        return builder.build();
    }

    public static CouponDescrTemplate fromRequest(DescrTemplateCreateParam template) {
        Builder<CouponDescrTemplate> builder = Builder.of(CouponDescrTemplate::new);
        builder.with(CouponDescrTemplate::setApplyScene, template.getApplyScene())
               .with(CouponDescrTemplate::setCouponDescr, template.getTemplate());

        return builder.build();
    }

    public static CouponDescrTemplate fromRequest(DescrTemplateUpdateParam template) {
        Builder<CouponDescrTemplate> builder = Builder.of(CouponDescrTemplate::new);
        builder.with(CouponDescrTemplate::setId, template.getId())
               .with(CouponDescrTemplate::setCouponDescr, template.getTemplate());

        return builder.build();
    }

    public static Coupon fromRequest(CouponEditParam param) {
        Builder<Coupon> builder = Builder.of(Coupon::new);
        builder.with(Coupon::setId, param.getCouponId())
               .with(Coupon::setName, param.getCouponName())
               .with(Coupon::setTitle, JSONObject.toJSONString(param.getMultiLanTitle()))
               .with(Coupon::setStatus, param.getStatus())
               .with(Coupon::setDescr, JSONObject.toJSONString(param.getMultiLanDesc()))
               .with(Coupon::setRedirectUrl, param.getRedirectUrl())
               .with(Coupon::setGrantApproval, param.getGrantApproval())
               .with(Coupon::setTotal, param.getTotal())
               .with(Coupon::setPossessLimit, param.getPossessLimit())
               .with(Coupon::setRemark, param.getRemark());
        return builder.build();
    }

    public static CouponPossess fromCoupon(String uid, Coupon coupon, Long sourceId, PossessSourceEnum sourceEnum, boolean preGrant) {
        LocalDateTime now = LocalDateTime.now();
        Builder<CouponPossess> builder = Builder.of(CouponPossess::new);
        builder.with(CouponPossess::setUid, uid)
               .with(CouponPossess::setCouponId, coupon.getId())
               .with(CouponPossess::setCouponType, coupon.getType())
               .with(CouponPossess::setApplyScene, coupon.getApplyScene())
               .with(CouponPossess::setPossessStage, preGrant ? PRE_POSSESS.getCode() : ENABLE.getCode())
               .with(CouponPossess::setBusinessStage, DEFAULT_UNUSED.getCode())
               .with(CouponPossess::setHasRead, false)
               .with(CouponPossess::setSourceId, sourceId)
               .with(CouponPossess::setSource, sourceEnum.getCode())
               .with(CouponPossess::setCtime, now)
               .with(CouponPossess::setUsableTime,
                       Optional.ofNullable(coupon.getExprAtStart()).orElse(now))
               .with(CouponPossess::setExprTime,
                       Optional.ofNullable(coupon.getExprAtEnd()).orElseGet(() -> now.plusDays(coupon.getExprInDays())));

        return builder.build();
    }

    public static BasalExportPossessBO fromNewAcquired(Coupon coupon, CouponPossess possess) {
        Builder<BasalExportPossessBO> builder = Builder.of(BasalExportPossessBO::new);
        builder.with(BasalExportPossessBO::setUid, possess.getUid())
               .with(BasalExportPossessBO::setCouponId, coupon.getId())
               .with(BasalExportPossessBO::setPossesssId, possess.getId())
               .with(BasalExportPossessBO::setCouponName, coupon.getName())
               .with(BasalExportPossessBO::setCouponTitle, coupon.toLocalized(coupon.getTitle()))
               .with(BasalExportPossessBO::setCouponDescr, coupon.toLocalized(coupon.getDescr()))
               .with(BasalExportPossessBO::setCouponType, coupon.getType())
               .with(BasalExportPossessBO::setBusinessStage, possess.getBusinessStage())
               .with(BasalExportPossessBO::setAvailableStart, possess.toEpochMilli(possess.getCtime()))
               .with(BasalExportPossessBO::setAvailableEnd, possess.toEpochMilli(possess.getExprTime()))
               .with(BasalExportPossessBO::setOverlay, coupon.getOverlay())
               .with(BasalExportPossessBO::setWorth, coupon.toPlainString(coupon.getWorth()))
               .with(BasalExportPossessBO::setWorthCoin, coupon.getWorthCoin())
               .with(BasalExportPossessBO::setReceiveTime, possess.toEpochMilli(possess.getCtime()))
               .with(BasalExportPossessBO::setRule, coupon.getRule());
        return builder.build();
    }

    public static CouponRuleBase deserialize(JSONObject rule, Integer typeCode) {
        return COUPON_RULE_BUILDER.get(getByCode(typeCode)).apply(rule);
    }

    public static CouponPolymericRuleVO polymericDeserialize(JSONObject rule) {
        return JSON.toJavaObject(rule, CouponPolymericRuleVO.class);
    }

    public static CouponEvent fromRequest(EventCreateParam param) {
        LocalDateTime now = LocalDateTime.now();
        boolean approval = EventTypeEnum.approval(param.getEventType());
        Builder<CouponEvent> builder = Builder.of(CouponEvent::new);
        builder.with(CouponEvent::setName, param.getEventName())
               .with(CouponEvent::setType, param.getEventType())
               .with(CouponEvent::setApprovalEvent, approval)
               .with(CouponEvent::setCouponIds, String.join(",", Functions.toList(param.getCouponIds(), String::valueOf)))
               .with(CouponEvent::setObjectType, param.getUserRangeType())
               .with(CouponEvent::setObjectAttaches, param.getUserRangeParam())
               .with(CouponEvent::setDescr, param.getEventDescr())
               .with(CouponEvent::setRemark, param.getRemark())
               .with(CouponEvent::setStartTime, Optional.ofNullable(param.getStartTime()).map(TemporalUtil::ofMilli).orElse(now))
               .with(CouponEvent::setEventStage, EventStageEnum.PRE_START.getStage());
        // .with(CouponEvent::setEndTime, TemporalUtil.ofEpochMilli(param.getEndTime()));
        return builder.build();
    }

    public static TaskProgressAggreRes fromTaskProgress(TaskProgressRes taskProgress) {
        TaskStatusBo taskStatusBo = TaskStatusBo.builder().kycTaskStatus(taskProgress.getKycTaskStatus()).amountTaskStatus(taskProgress.getAmountTaskStatus()).earnTaskStatus(taskProgress.getEarnTaskStatus()).build();
        TaskTimeBo taskTimeBo = TaskTimeBo.builder().kycTaskExpireTime(taskProgress.getKycTaskExpireTime()).amountTaskExpireTime(taskProgress.getAmountTaskExpireTime()).earnTaskExpireTime(taskProgress.getEarnTaskExpireTime()).build();
        return TaskProgressAggreRes.builder().taskStatus(taskStatusBo).taskTime(taskTimeBo).build();
    }


    public void test(Function<CouponCreateParam, CouponRuleBase> function, CouponCreateParam param) {
        CouponRuleBase apply = function.apply(param);
    }
}
