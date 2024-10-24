package com.trading.backend.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.client.IUserServiceApi;
import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.enums.PossessSourceEnum;
import com.trading.backend.common.enums.PossessStageEnum;
import com.trading.backend.common.util.Functions;
import com.trading.backend.common.util.Maps;
import com.trading.backend.common.util.Predicator;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
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
import com.trading.backend.mapper.CouponEventMapper;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.pojo.PossessDO;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import com.trading.backend.util.BeanMapper;
import com.trading.backend.common.util.TemporalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * @author ~~ trading.s
 * @date 19:39 09/29/21
 */
@Slf4j
@Service
// @CacheConfig(cacheNames = "buffered:possess:")
public class PossesServiceImpl implements IPossesService {

    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private CouponPossessMapper possessMapper;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IUserServiceApi userServiceApi;
    @Autowired
    private CouponEventMapper eventMapper;

    public static final ThreadLocal<DateTimeFormatter> FORMATTER_1 = ThreadLocal.withInitial(() -> DateTimeFormatter.ofPattern("MM/dd/yyyy"));

    static DeductCouponRule type = new DeductCouponRule();

    static final public Map<String, Comparator<PossessDO>> POSSESS_LIST_SORT;

    private static final Map<String, String> interType;
    private static final Map<String, String> deductType;
    private static final Map<String, String> cashType;

    static {
        POSSESS_LIST_SORT = new HashMap<>();
        POSSESS_LIST_SORT.put("RECV_TIME_ASC", Comparator.comparing(PossessDO::getReceiveTime));
        POSSESS_LIST_SORT.put("RECV_TIME_DESC", Comparator.comparing(PossessDO::getReceiveTime).reversed());
        POSSESS_LIST_SORT.put("EXPR_TIME_ASC", Comparator.comparing(PossessDO::getAvailableEnd));
        POSSESS_LIST_SORT.put("EXPR_TIME_DESC", Comparator.comparing(PossessDO::getAvailableEnd).reversed());
        POSSESS_LIST_SORT.put("DEFAULT", Comparator.comparing(PossessDO::getAvailableEnd).reversed()
                                                   .thenComparing(Comparator.comparing(PossessDO::getAvailableStart).reversed()));

        interType = Maps.of("zh-CN", "加息券", "zh-TW", "加息券", "en-US", "bonus interest coupon");
        deductType = Maps.of("zh-CN", "减息券", "zh-TW", "減息券", "en-US", "interest discount coupon");
        cashType = Maps.of("zh-CN", "资产券", "zh-TW", "資產券", "en-US", "cash coupon");
    }


    // @Override
    // public List<PossessBO> getAvailablePossess(String uid) {
    //     List<PossessDO> possList = possessDao.getValidPossess(uid);
    //     return possList.stream().map(BeanMapper::map).collect(Collectors.toList());
    // }

    // key或keyGenerator其中一个即可
    // cacheManager，cacheResolver 指定哪个缓存管理器，使用其中一个参数即可
    // @Cacheable(/*key = "#p0"*/keyGenerator = "fromIdPrefix"/*, cacheNames = "xxxx"*/)
    // @CachePut(key = "#p0", cacheNames = "possess_id"/*, condition = "#p0.id != null"*/)
    @Override
    public PossessDO getByPossessId(Long possessId) {
        return possessDao.getByPossessId(possessId);
    }

    @Override
    public List<PossessDO> getTypedPossess(String uid, CouponTypeEnum typeEnum) {
        return possessDao.getTypesPossess(uid, Collections.singletonList(typeEnum.getCode()));
    }

    @Override
    public List<PossessDO> getTypedPossess(String uid, List<Integer> types) {
        if (CollectionUtil.isEmpty(types))
            return possessDao.getValidPossess(uid);

        return possessDao.getTypesPossess(uid, new ArrayList<>(Sets.newHashSet(types)));
    }

    @Override
    public List<PossessDO> getAppyScenePossess(String uid, CouponApplySceneEnum applyEnum) {
        return possessDao.getAppyScenePossess(uid, applyEnum.getCode());
    }

    @Override
    public List<PossessDO> getAllAppyScenePossess(String uid, CouponApplySceneEnum applyEnum) {
        return possessDao.getAllAppyScenePossess(uid, applyEnum.getCode());
    }

    @Override
    public List<PossessDO> getSortedScenedPossess(String uid, CouponApplySceneEnum applyEnum, Comparator<PossessDO> comparator) {
        Objects.requireNonNull(comparator);
        return Functions.sort(getAppyScenePossess(uid, applyEnum), comparator);
    }

    @Override
    public List<PossessDO> getOrderedPossessSingle(String uid, CouponApplySceneEnum applyEnum) {
        return Collections.singletonList(possessDao.getOrderedPossessSingle(uid, applyEnum.getCode()));
    }

    @Override
    public List<LoanPossessVO> getLoanMatchs(LoanMatchParam param, List<LoanPossessVO> unMatch) {
        List<PossessDO> possess = this.getTypedPossess(param.getUid(), CouponTypeEnum.DEDUCTION_TYPE);
        AtomicReference<Predicate<LoanPossessVO>> reference = new AtomicReference<>(bo -> true);
        Optional.ofNullable(param.getCoin())
                .filter(StringUtils::isNotBlank)
                .ifPresent(v -> reference.getAndUpdate(prev -> prev.and(bo -> bo.getCouponRule().coinUsable(v))));
        Optional.ofNullable(param.getLoanAmount())
                .filter(StringUtils::isNotBlank).map(BigDecimal::new)
                .ifPresent(v -> reference.getAndUpdate(prev -> prev.and(bo -> bo.getCouponRule().amountUsable(v))));
        Optional.ofNullable(param.getLoanTypes())
                .ifPresent(v -> reference.getAndUpdate(prev -> prev.and(bo -> bo.getCouponRule().typesUsable(v))));
        Optional.ofNullable(param.getLoanPeriod())
                .ifPresent(v -> reference.getAndUpdate(prev -> prev.and(bo -> bo.getCouponRule().periodUsable(v))));

        List<LoanPossessVO> matchedVO = new ArrayList<>();
        possess.forEach(ma -> {
            LoanPossessVO loanVo = BeanMapper.toLoanPossessVo(ma);
            if (reference.get().test(loanVo))
                matchedVO.add(loanVo);
            else
                unMatch.add(loanVo);
        });
        return matchedVO;
    }

    @Override
    public List<EarnPossessVO> getEarnMatchs(EarnMatchParam param, List<EarnPossessVO> unMatch) {
        List<PossessDO> possess = this.getTypedPossess(param.getUid(), CouponTypeEnum.INTEREST_TYPE);
        AtomicReference<Predicate<EarnPossessVO>> reference = new AtomicReference<>(bo -> true);

        Optional.ofNullable(param.getCoin()).filter(StringUtils::isNotBlank)
                .ifPresent(v -> reference.getAndUpdate(prev -> prev.and(bo -> bo.getCouponRule().coinUsable(v))));
        Optional.ofNullable(param.getEarnPeriod())
                .ifPresent(v -> reference.getAndUpdate(prev -> prev.and(bo -> bo.getCouponRule().periodUsable(v))));
        Optional.ofNullable(param.getSubscrAmount()).filter(StringUtils::isNotBlank).map(BigDecimal::new)
                .ifPresent(v -> reference.getAndUpdate(prev -> prev.and(bo -> bo.getCouponRule().amountUsable(v))));

        List<EarnPossessVO> matchedVO = new ArrayList<>();
        possess.forEach(pos -> {
            EarnPossessVO earnVo = BeanMapper.toEarnPossessVo(pos);
            if (reference.get().test(earnVo))
                matchedVO.add(earnVo);
            else
                unMatch.add(earnVo);
        });
        return matchedVO;
    }

    @Override
    public List<DualPossessVO> getDualMatchs(String uid) {
        List<PossessDO> possess = this.getTypedPossess(uid, CouponTypeEnum.PROFITINCRE_TYPE);
        return Functions.toList(possess, BeanMapper::toDualPossessVo);
    }

    @Override
    public List<ConcisePossessVO> getConcisePossess(CouponPossessParam param) {
        // tpyes condition
        if (CollectionUtil.isNotEmpty(param.getCoupon_types())) {
            List<PossessDO> possess = this.getTypedPossess(param.getUid(), param.getCoupon_types());
            if (CollectionUtil.isEmpty(possess)) return Collections.emptyList();
            return possess.stream().map(BeanMapper::toConcisePossessVo).collect(Collectors.toList());
        }
        // all possess
        List<PossessDO> valid = possessDao.getValidPossess(param.getUid());
        if (CollectionUtil.isEmpty(valid)) return Collections.emptyList();
        return valid.stream().map(BeanMapper::toConcisePossessVo).collect(Collectors.toList());
    }

    @Override
    public List<FullScalePossessVO> getFullScalePossess(FullScalePossessParam param) {
        if (!POSSESS_LIST_SORT.containsKey(param.getSort())) {
            param.setSort("DEFAULT");
        }

        LocalDateTime now = LocalDateTime.now();
        Boolean getValid = Optional.ofNullable(param.getValid()).orElse(false);

        // invalid possess
        if (!getValid) {
            List<PossessDO> invalid = possessDao.getInvalidPossess(param.getHeaderUid(), now.minusDays(90));
            if (CollectionUtil.isEmpty(invalid)) return Collections.emptyList();
            return invalid.stream()
                          .sorted(Comparator.comparing(PossessDO::getConsumeTime, Comparator.nullsFirst(LocalDateTime::compareTo)).reversed()
                                            .thenComparing(Comparator.comparing(PossessDO::getAvailableEnd).reversed()))
                          .map(BeanMapper::toFullScalePossessVo).collect(Collectors.toList());
        }

        Comparator<PossessDO> sort = POSSESS_LIST_SORT.get(param.getSort().toUpperCase());
        // classified possess and valid
        if (CollectionUtil.isNotEmpty(param.getCoupon_types())) {
            List<PossessDO> possess = this.getTypedPossess(param.getHeaderUid(), param.getCoupon_types());
            if (CollectionUtil.isEmpty(possess)) return Collections.emptyList();
            return possess.stream().sorted(sort).map(BeanMapper::toFullScalePossessVo).collect(Collectors.toList());
        }
        // all possess
        List<PossessDO> allValid = possessDao.getValidPossess(param.getHeaderUid());
        if (CollectionUtil.isEmpty(allValid)) return Collections.emptyList();
        return allValid.stream().sorted(sort).map(BeanMapper::toFullScalePossessVo).collect(Collectors.toList());
    }

    @Override
    public List<FullScalePossessVO> getFullScalePossess(FullScaleCouponParam param) {
        List<FullScalePossessVO> fullScales = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(param.getCouponIds())) {
            List<PossessDO> periods = possessDao.getBwcPeriodPossess(param.getUid(), param.getCouponIds(), TemporalUtil.thisMonthStart(), TemporalUtil.thisMonthEnd());

            fullScales.addAll(
                    Functions.filter(periods,
                            Predicator.isEqual(PossessDO::getPossessStage, 0)
                                    .and(Predicator.isEqual(PossessDO::getBusinessStage, 0)),
                            BeanMapper::toFullScalePossessVo)
            );

            List<Long> unPossedIds = Functions.filter(param.getCouponIds(), Predicator.notExist(Functions.toSet(periods, PossessDO::getCouponId)));
            List<Coupon> coupons = couponService.getCoupons(unPossedIds);
            fullScales.addAll(Functions.toList(coupons, BeanMapper::toFullScalePossessVo));
        }

        List<PossessDO> approvaled = possessDao.getUserPossBySource(param.getUid(), PossessSourceEnum.BWC_APPROVAL_COUPON.getCode());
        List<FullScalePossessVO> approvals = Functions.toList(approvaled, BeanMapper::toFullScalePossessVo);
        fullScales.addAll(approvals);
        return fullScales;
    }

    @Override
    public List<PossessAvailabeVO> getPossAvableWithType(PossessAvailableParam param) {
        List<PossessDO> possess = possessDao.getIdsPossess(param.getUid(), param.getPossessIds());
        if (CollectionUtil.isEmpty(possess)) return Collections.emptyList();
        return possess.stream().map(BeanMapper::toPossessAvalVo).collect(Collectors.toList());
    }

    @Override
    public List<ClubPossessVO> getClubCoupons(String uid, List<Long> couponIds) {
        if (CollectionUtil.isEmpty(couponIds)) return Collections.emptyList();
        List<Coupon> clubCoupons = couponService.getCoupons(couponIds);
        List<PossessDO> clubPossess = possessDao.getClubPossess(uid, couponIds, TemporalUtil.thisMonthStart(), TemporalUtil.thisMonthEnd());
        Map<Long, PossessDO> collect = Functions.toMap(clubPossess, PossessDO::getCouponId, Function.identity());

        List<ClubPossessVO> result = new ArrayList<>();
        for (Coupon clubCoupon : clubCoupons) {
            ClubPossessVO clubPossessVo = new ClubPossessVO();
            // bus_stage和pos_stage都不为null 都有初始状态0
            PossessDO possed = collect.get(clubCoupon.getId());
            Optional<PossessDO> possessOpt = Optional.ofNullable(possed);
            clubPossessVo.setCouponId(clubCoupon.getId());
            clubPossessVo.setCouponTitle(clubCoupon.toLocalized(clubCoupon.getTitle()));
            clubPossessVo.setCouponDescr(clubCoupon.toLocalized(clubCoupon.getDescr()));
            if (Objects.equals(clubCoupon.getType(), CouponTypeEnum.DEDUCTION_TYPE.getCode())) {
                Integer deduct_way = clubCoupon.getRule().getInteger("deduct_way");
                clubPossessVo.setDeductWay(deduct_way);
            }
            clubPossessVo.setCouponType(clubCoupon.getType());
            clubPossessVo.setPossessId(possessOpt.map(PossessDO::getPossessId).orElse(null));
            clubPossessVo.setWorthCoin(clubCoupon.getWorthCoin());
            clubPossessVo.setWorth(clubCoupon.toPlainString(clubCoupon.getWorth()));
            clubPossessVo.setBusinessStage(possessOpt.map(PossessDO::getBusinessStage).orElse(null));
            clubPossessVo.setExprTime(possessOpt.map(pos -> pos.toEpochMilli(pos.getAvailableEnd()))
                                                .orElseGet(() -> {
                                                    return Optional.ofNullable(clubCoupon.getExprAtEnd())
                                                                   .map(clubCoupon::toEpochMilli)
                                                                   .orElse(null);
                                                }));

            if (Objects.isNull(possed) && !clubCoupon.available()) {
                clubPossessVo.setCouponValid(false);
            }
            result.add(clubPossessVo);
        }
        return result;
    }


    // @CacheEvict(keyGenerator = "fromBeanIdPrefix")
    @Override
    public boolean updatePossess(CouponPossess possess, Example example) {
        int rows = possessMapper.updateByExampleSelective(possess, example);
        if (1 != rows)
            throw new BusinessException(ExceptionEnum.UPDATE_ERROR);
        return true;
    }

    @Override
    public void possesStageContributing(Long possessId, CouponPossess toUpdate) {
        Example example = new Example(CouponPossess.class);
        example.createCriteria()
               .andEqualTo("id", possessId)
               .andEqualTo("possessStage", 0)
               .andEqualTo("businessStage", 0);

        int rows = possessMapper.updateByExampleSelective(toUpdate, example);
        if (rows != 1)
            throw new BusinessException(ExceptionEnum.CASH_COUPON_UPDATE_ACTIVATE_FAILED);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releasePrelock(Long eventId, boolean pass) {
        Example example = new Example(CouponPossess.class);
        example.selectProperties("id", "couponId", "uid");
        example.createCriteria()
               .andEqualTo("sourceId", eventId)
               .andEqualTo("possessStage", PossessStageEnum.PRE_POSSESS.getCode());

        List<CouponPossess> preGrantPosses = possessMapper.selectByExample(example);
        Set<Long> eventCoupons = Functions.toSet(preGrantPosses, CouponPossess::getCouponId);
        Set<String> uidSets = Functions.toSet(preGrantPosses, CouponPossess::getUid);
        if (pass) {
            int rows = possessMapper.updateByExampleSelective(
                    new CouponPossess().setPossessStage(PossessStageEnum.ENABLE.getCode()), example);
            couponService.incrIssueReleasePrelock(new ArrayList<>(eventCoupons), uidSets.size());
            return;
        }
        int deleted = possessMapper.deleteByExample(example);
        couponService.releasePrelock(new ArrayList<>(eventCoupons), uidSets.size());
    }

    @Override
    public void issueNotify(List<BasalExportPossessBO> possesses, PossessSourceEnum sourceEnum) {
        Map<String, List<BasalExportPossessBO>> listMap = Functions.groupingBy(possesses, BasalExportPossessBO::getUid);
        Set<String> uids = Functions.toSet(possesses, BasalExportPossessBO::getUid);
        Map<String, JSONObject> profiles = userServiceApi.multiUserProfiles(uids);
        boolean newRegistry = sourceEnum == PossessSourceEnum.NEW_REGISTER;
        if (newRegistry) {
            log.info("issueNotify language {}", profiles);
        }

        listMap.forEach((uid, possesss) -> {
            Map<Integer, List<BasalExportPossessBO>> typedPossess = Functions.groupingBy(possesss, BasalExportPossessBO::getCouponType);
            typedPossess.forEach((type, posses) -> {
                Long aLong = posses.stream().map(BasalExportPossessBO::getAvailableEnd).min(Comparator.comparing(Function.identity())).orElse(Instant.now().toEpochMilli());
                String format = FORMATTER_1.get().format(TemporalUtil.ofDefaultZoneMilli(aLong));
                String language = Optional.ofNullable(profiles)
                                          .filter(CollectionUtil::isNotEmpty)
                                          .map(profile -> profile.get(uid))
                                          .map(json -> json.getString("language"))
                                          .orElse("en-US");
                List<String> couponsContent = possesss.stream().map(this::combineCoupon).collect(Collectors.toList());
                CouponTypeEnum typeEnum = CouponTypeEnum.getByCode(type);
                ArrayList<String> templateList = new ArrayList<>();
                if (typeEnum == CouponTypeEnum.INTEREST_TYPE) {
                    templateList = newRegistry
                            ? Lists.newArrayList("MSG07_000001")
                            : Lists.newArrayList("EML03_000026", "MSG07_000001");
                }
                else if (typeEnum == CouponTypeEnum.DEDUCTION_TYPE) {
                    templateList = newRegistry
                            ? Lists.newArrayList("MSG07_000018")
                            : Lists.newArrayList("EML03_000036", "MSG07_000018");
                }
                else if (typeEnum == CouponTypeEnum.CASHRETURN_TYPE) {
                    templateList = newRegistry
                            ? Lists.newArrayList("MSG07_000017")
                            : Lists.newArrayList("EML03_000035", "MSG07_000017");
                }
                userServiceApi.kafkaNotify(
                        uid, templateList,
                        Maps.of("coupon", " " + String.join(", ", couponsContent), "couponExpireTime", format + " (UTC+8)"),
                        newRegistry ? Lists.newArrayList(3) : Lists.newArrayList(0, 3),
                        language
                );
            });
        });
    }

    @Override
    public List<CouponPossess> selectExample(Supplier<Example> supplier) {
        return possessMapper.selectByExample(supplier.get());
    }

    @Override
    public boolean hasCoupon(String uid, Long couponId) {
        Example example = new Example(CouponPossess.class);
        example.createCriteria()
               .andEqualTo("uid", uid)
               .andEqualTo("couponId", couponId);
        return possessMapper.selectCountByExample(example) > 0;
    }

    private String combineCoupon(BasalExportPossessBO possBo) {
        CouponTypeEnum typeEnum = CouponTypeEnum.getByCode(possBo.getCouponType());
        if (typeEnum == CouponTypeEnum.INTEREST_TYPE) {
            return new BigDecimal(possBo.getWorth())
                    .multiply(BigDecimal.valueOf(100))
                    .stripTrailingZeros()
                    .toPlainString() + "% ";
        }
        else if (typeEnum == CouponTypeEnum.DEDUCTION_TYPE) {
            Integer deductWay = possBo.getRule().getInteger("deduct_way");
            String deduct;
            if (deductWay == 1) {
                deduct = new BigDecimal(possBo.getWorth())
                        .multiply(BigDecimal.valueOf(100))
                        .stripTrailingZeros()
                        .toPlainString() + "% OFF ";
            }
            else {
                deduct = BigDecimal
                        .valueOf(100)
                        .subtract(new BigDecimal(possBo.getWorth()).multiply(BigDecimal.valueOf(100)))
                        .stripTrailingZeros()
                        .toPlainString() + "% OFF ";
            }
            return deduct;
        }
        else if (typeEnum == CouponTypeEnum.CASHRETURN_TYPE) {
            return new BigDecimal(possBo.getWorth())
                    .stripTrailingZeros()
                    .toPlainString()
                    + StringUtils.upperCase(possBo.getWorthCoin()) + " ";
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("1"));
    }
}
