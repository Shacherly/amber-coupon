package com.trading.backend.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.trading.backend.client.IUserServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.common.enums.CouponTypeEnum;
import com.trading.backend.common.http.PageResult;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.constant.RedisKey;
import com.trading.backend.domain.CashCouponRule;
import com.trading.backend.domain.Coupon;
import com.trading.backend.domain.CouponDescrTemplate;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.domain.DeductCouponRule;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.domain.ProfitCouponRule;
import com.trading.backend.domain.TrialCouponRule;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.exception.VisibleException;
import com.trading.backend.http.request.aceup.CouponListReq;
import com.trading.backend.http.request.aceup.IssueListReq;
import com.trading.backend.http.request.event.EventCreateParam;
import com.trading.backend.http.response.aceup.CouponDetatilRes;
import com.trading.backend.http.response.aceup.CouponListRes;
import com.trading.backend.http.response.aceup.IssueDetailRes;
import com.trading.backend.http.response.aceup.IssueListRes;
import com.trading.backend.http.response.aceup.IssueListVo;
import com.trading.backend.http.response.desctemplate.DescrTemplateVO;
import com.trading.backend.mapper.CouponDescrTemplateMapper;
import com.trading.backend.mapper.CouponMapper;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.service.ICouponAdminService;
import com.trading.backend.service.IServiceTemplte;
import com.trading.backend.util.PageContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


/**
 * @author ~~ trading.s
 * @date 13:21 09/24/21
 */
@Slf4j
@Service
public class CouponAdminServiceImpl implements ICouponAdminService {

    @Autowired
    private CouponMapper baseCouponMapper;

    @Autowired
    private CouponPossessMapper couponPossessMapper;

    @Autowired
    private CouponPossessDao couponPossessDao;

    @Autowired
    private IUserServiceApi userServiceApi;

    @Autowired
    private RedisService redis;
    @Autowired
    private IServiceTemplte serviceTemplte;
    @Autowired
    private CouponDescrTemplateMapper templateMapper;

    @Override
    public Long saveOrUpdate(Coupon source) {
        Optional.ofNullable(source)
                .orElseThrow(() -> new BusinessException(ExceptionEnum.ARGUMENT_NULL, "source"));
        if (Objects.isNull(source.getId())) {
            log.info("InsertCoupon = {}", source);
            save(source);
            return source.getId();
        }
        log.info("UpdateCoupon = {}", source);
        update(source);
        return source.getId();
    }

    @Override
    public void absoluteSave(Coupon source) {
        // baseCouponMapper.
    }

    @Override
    public PageResult<CouponListRes> queryCouponList(CouponListReq param) {
        Example example = new Example(Coupon.class);
        Example.Criteria criteria = example.createCriteria();
        if (!Objects.isNull(param.getId())){
            criteria.andEqualTo("id",param.getId());
        }
        if (!StringUtils.isEmpty(param.getName())){
            criteria.andLike("name","%"+param.getName()+"%");
        }
        if (!StringUtils.isEmpty(param.getTitle())){
            criteria.andLike("title","%"+param.getTitle()+"%");
        }
        if (!Objects.isNull(param.getStatus())){
            criteria.andEqualTo("status",param.getStatus());
        }
        if (!Objects.isNull(param.getType())){
            criteria.andEqualTo("type",param.getType());
        }
        if (!Objects.isNull(param.getGrant_approval())){
            criteria.andEqualTo("grantApproval",param.getGrant_approval());
        }
        example.setOrderByClause(" id desc ");

        PageResult<Coupon> couponResult = PageContext.selectPage(() -> baseCouponMapper.selectByExample(example));
        List<CouponListRes> couponListResList = new ArrayList<>();
        couponResult.getItems().forEach(coupon -> {
            CouponListRes temp = new CouponListRes();
            BeanUtils.copyProperties(coupon,temp);
            //设置时间
            if (!Objects.isNull(coupon.getExprAtStart())){
                temp.setExprAtStart(TemporalUtil.toEpochMilli(coupon.getExprAtStart()));
            }
            if (!Objects.isNull(coupon.getExprAtEnd())){
                temp.setExprAtEnd(TemporalUtil.toEpochMilli(coupon.getExprAtEnd()));
            }
            //多语言标题
            if (!StringUtils.isEmpty(coupon.getTitle())){
                temp.setTitle(JSONObject.toJavaObject(JSONObject.parseObject(coupon.getTitle()),Map.class));
            }
            //已发行量
            temp.setReleased(coupon.getIssue());
            //未发行量
            temp.setNotReleased(coupon.getTotal() - coupon.getIssue());
            //已使用量
            temp.setUsed((long)getUsedCount(coupon.getId()));;
            //未使用量 = 已发行量 - 已使用量
            temp.setNotUsed(temp.getReleased() - temp.getUsed());
            //创建时间
            temp.setCreateTime(TemporalUtil.toEpochMilli(coupon.getCtime()));

            couponListResList.add(temp);
        });

        //repackage result
        PageResult<CouponListRes> result = new PageResult<>();
        result.setItems(couponListResList);
        result.setPage(couponResult.getPage());
        result.setPage_size(couponResult.getPage_size());
        result.setCount(couponResult.getCount());
        result.setTotal_pages(couponResult.getTotal_pages());
        result.generateTotalPages();
        return result;
    }

    @Override
    public CouponDetatilRes queryCouponDetail(Long id) {
        Coupon coupon = baseCouponMapper.selectByPrimaryKey(id);
        CouponDetatilRes res = new CouponDetatilRes();
        BeanUtils.copyProperties(coupon,res);
        //处理特殊字段
        if (!Objects.isNull(coupon.getExprAtStart())){
            res.setExprAtStart(TemporalUtil.toEpochMilli(coupon.getExprAtStart()));
        }
        if (!Objects.isNull(coupon.getExprAtEnd())){
            res.setExprAtEnd(TemporalUtil.toEpochMilli(coupon.getExprAtEnd()));
        }
        if (!Objects.isNull(coupon.getTitle())){
            res.setMultiLanTitle(JSONObject.toJavaObject(JSON.parseObject(coupon.getTitle()), Map.class));
        }
        if (!Objects.isNull(coupon.getDescr())){
            res.setMultiLanDesc(JSONObject.toJavaObject(JSON.parseObject(coupon.getDescr()), Map.class));
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(coupon.getWorthCoin())){
            res.setWorthCoin(coupon.getWorthCoin());
        }
        //设置优惠券名称
        res.setCouponName(coupon.getName());
        //设置worth
        CouponTypeEnum typeEnum = CouponTypeEnum.getByCode(coupon.getType());
        if (typeEnum == CouponTypeEnum.DEDUCTION_TYPE && coupon.getRule().getInteger("deduct_way") == 1) {
            res.setWorth(BigDecimal.ONE.subtract(coupon.getWorth()).toPlainString());
        }
        else {
            res.setWorth(Objects.isNull(coupon.getWorth()) ? null : coupon.getWorth().toPlainString());
        }

        //设置rule
        if (CouponTypeEnum.INTEREST_TYPE.equals(typeEnum)){
            //加息
            res.setInterCouponRule(JSONObject.toJavaObject(coupon.getRule(), InterCouponRule.class));
        }else if (CouponTypeEnum.DEDUCTION_TYPE.equals(typeEnum)){
            //减息
            res.setDeductCouponRule(JSONObject.toJavaObject(coupon.getRule(), DeductCouponRule.class));
        }else if (CouponTypeEnum.CASHRETURN_TYPE.equals(typeEnum)){
            //资产
            res.setCashCouponRule(JSONObject.toJavaObject(coupon.getRule(), CashCouponRule.class));
        }
        else if (CouponTypeEnum.PROFITINCRE_TYPE.equals(typeEnum)) {
            res.setProfitCouponRule(JSONObject.toJavaObject(coupon.getRule(), ProfitCouponRule.class));
        }
        else if (CouponTypeEnum.TRIALFUND_TYPE.equals(typeEnum)) {
            res.setTrialCouponRule(JSONObject.toJavaObject(coupon.getRule(), TrialCouponRule.class));
        }

        return res;
    }

    @Override
    public PageResult<IssueListRes> queryCouponPossessList(IssueListReq param) {
        PageResult<IssueListVo> possessResult = PageContext.selectPage(() ->  couponPossessDao.getIssueList(param), "ctime desc, id desc");
        List<IssueListRes> res = new ArrayList<>();

        possessResult.getItems().forEach(possess ->{
            IssueListRes issueListRes = new IssueListRes();
            BeanUtils.copyProperties(possess,issueListRes);
            if (!Objects.isNull(possess.getUsableTime())){
                issueListRes.setUsableTime(TemporalUtil.toEpochMilli(possess.getUsableTime()));
            }
            if (!Objects.isNull(possess.getExprTime())){
                issueListRes.setExprTime(TemporalUtil.toEpochMilli(possess.getExprTime()));
            }
            if (!Objects.isNull(possess.getCtime())){
                issueListRes.setCtime(TemporalUtil.toEpochMilli(possess.getCtime()));
            }
            if (!Objects.isNull(possess.getConsumeTime())){
                issueListRes.setConsumeTime(TemporalUtil.toEpochMilli(possess.getConsumeTime()));
            }
            res.add(issueListRes);
        });

        PageResult<IssueListRes> result = new PageResult<>();
        result.setItems(res);
        result.setPage(possessResult.getPage());
        result.setPage_size(possessResult.getPage_size());
        result.setCount(possessResult.getCount());
        result.setTotal_pages(possessResult.getTotal_pages());
        result.generateTotalPages();
        return result;
    }

    @Override
    public IssueDetailRes queryConsumeDetail(Long id) {
        IssueDetailRes result = new IssueDetailRes();
        CouponPossess couponPossess = couponPossessMapper.selectByPrimaryKey(id);
        //设置possess参数
        result.setSource(couponPossess.getSource());
        if (!Objects.isNull(couponPossess.getExprTime())){
            result.setExprTime(TemporalUtil.toEpochMilli(couponPossess.getExprTime()));
        }
        if (!Objects.isNull(couponPossess.getCtime())){
            result.setCtime(TemporalUtil.toEpochMilli(couponPossess.getCtime()));
        }
        if (!Objects.isNull(couponPossess.getConsumeTime())){
            result.setConsumeTime(TemporalUtil.toEpochMilli(couponPossess.getConsumeTime()));
        }
        result.setPossessStage(couponPossess.getPossessStage());

        //设置关联的优惠券参数
        Coupon coupon = baseCouponMapper.selectByPrimaryKey(couponPossess.getCouponId());
        result.setCouponId(coupon.getId());
        result.setCouponType(coupon.getType());
        result.setMultiLanTitle(JSONObject.toJavaObject(JSONObject.parseObject(coupon.getTitle()),Map.class));
        result.setPossessLimit(coupon.getPossessLimit());
        result.setExprInDays(coupon.getExprInDays());

        if (!Objects.isNull(coupon.getExprAtStart())){
            result.setExprAtStart(TemporalUtil.toEpochMilli(coupon.getExprAtStart()));
        }
        if (!Objects.isNull(coupon.getExprAtEnd())){
            result.setExprAtEnd(TemporalUtil.toEpochMilli(coupon.getExprAtEnd()));
        }
        result.setRedirectUrl(coupon.getRedirectUrl());
        result.setRemark(coupon.getRemark());

        //设置rule
        if (CouponTypeEnum.INTEREST_TYPE.equals(CouponTypeEnum.getByCode(coupon.getType()))){
            //加息
            result.setInterCouponRule(JSONObject.toJavaObject(coupon.getRule(), InterCouponRule.class));
        }else if (CouponTypeEnum.DEDUCTION_TYPE.equals(CouponTypeEnum.getByCode(coupon.getType()))){
            //减息
            result.setDeductCouponRule(JSONObject.toJavaObject(coupon.getRule(), DeductCouponRule.class));
        }else if (CouponTypeEnum.CASHRETURN_TYPE.equals(CouponTypeEnum.getByCode(coupon.getType()))){
            //资产
            result.setCashCouponRule(JSONObject.toJavaObject(coupon.getRule(), CashCouponRule.class));
        }

        return result;
    }

    @Override
    public List<CouponDetatilRes> queryCouponDetails(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }

        List<CouponDetatilRes> resList = new ArrayList<>();

        Example example = new Example(Coupon.class);
        example.createCriteria().andIn("id",ids);
        List<Coupon> couponList = baseCouponMapper.selectByExample(example);
        couponList.forEach(coupon -> {
            CouponDetatilRes res = new CouponDetatilRes();
            BeanUtils.copyProperties(coupon,res);
            //处理特殊字段
            if (!Objects.isNull(coupon.getExprAtStart())){
                res.setExprAtStart(TemporalUtil.toEpochMilli(coupon.getExprAtStart()));
            }
            if (!Objects.isNull(coupon.getExprAtEnd())){
                res.setExprAtEnd(TemporalUtil.toEpochMilli(coupon.getExprAtEnd()));
            }
            if (!Objects.isNull(coupon.getTitle())){
                res.setMultiLanTitle(JSONObject.toJavaObject(JSON.parseObject(coupon.getTitle()), Map.class));
            }
            if (!Objects.isNull(coupon.getDescr())){
                res.setMultiLanDesc(JSONObject.toJavaObject(JSON.parseObject(coupon.getDescr()), Map.class));
            }
            if (!Objects.isNull(coupon.getWorth())){
                res.setWorthCoin(coupon.getWorth().toPlainString());
            }
            //设置优惠券名称
            res.setCouponName(coupon.getName());
            //设置worth
            res.setWorth(Objects.isNull(coupon.getWorth()) ? null : coupon.getWorth().toPlainString());

            //设置rule
            if (CouponTypeEnum.INTEREST_TYPE.equals(CouponTypeEnum.getByCode(coupon.getType()))){
                //加息
                res.setInterCouponRule(JSONObject.toJavaObject(coupon.getRule(), InterCouponRule.class));
            }else if (CouponTypeEnum.DEDUCTION_TYPE.equals(CouponTypeEnum.getByCode(coupon.getType()))){
                //减息
                res.setDeductCouponRule(JSONObject.toJavaObject(coupon.getRule(), DeductCouponRule.class));
            }else if (CouponTypeEnum.CASHRETURN_TYPE.equals(CouponTypeEnum.getByCode(coupon.getType()))){
                //资产
                res.setCashCouponRule(JSONObject.toJavaObject(coupon.getRule(), CashCouponRule.class));
            }

            resList.add(res);

        });

        return resList;
    }

    @Override
    public void revokeCoupon(Long id) {
        //状态为 "未使用" 才可以撤回
        Example queryExample = new Example(CouponPossess.class);
        queryExample.createCriteria().andEqualTo("id",id).andEqualTo("businessStage",0);

        int existRow = couponPossessMapper.selectCountByExample(queryExample);
        if (existRow == 0){
            //记录不存在
            throw new BusinessException(ExceptionEnum.COUPON_NOT_SUPPORT_MODIFY);
        }

        CouponPossess updateCouponPossess = new CouponPossess();
        updateCouponPossess.setPossessStage(-1);//撤回

        int updateRow = couponPossessMapper.updateByExampleSelective(updateCouponPossess,queryExample);
        if (updateRow == 0){
            //撤回失败
            throw new BusinessException(ExceptionEnum.COUPON_NOT_SUPPORT_MODIFY);
        }
    }

    @Override
    public Long createEvent(EventCreateParam param) {

        return null;
    }

    @Override
    public Long saveOrUpdate(CouponDescrTemplate source) {
        if (Objects.isNull(source.getId())
                && descrTemplatExist(source.getApplyScene()))
            throw new VisibleException(ExceptionEnum.TEMPLATE_EXIST, source.getApplyScene());
        return serviceTemplte.saveOrUpdate(source, templateMapper).getId();
    }


    @Override
    public JSONObject getDescrTemplate(Integer applyScene) {
        Example example = new Example(CouponDescrTemplate.class);
        example.createCriteria()
               .andEqualTo("applyScene", applyScene)
               .andEqualTo("status", "ENABLE");
        CouponDescrTemplate descrTemplate = templateMapper.selectOneByExample(example);
        return Optional.ofNullable(descrTemplate)
                       .map(CouponDescrTemplate::getCouponDescr)
                       .orElseGet(JSONObject::new);
    }

    @Override
    public PageResult<DescrTemplateVO> getTemplatePage() {
        PageResult<CouponDescrTemplate> page = PageContext.selectPage(
                () -> templateMapper.selectAll(),
                PageContext.getPageNum(),
                PageContext.getPageSize(),
                "ctime desc"
        );
        return PageResult.fromAnother(page, this::mapping);
    }

    public DescrTemplateVO mapping(CouponDescrTemplate entity) {
        return new DescrTemplateVO(entity.getId(), entity.getApplyScene(), entity.getCouponDescr());
    }

    @Override
    public boolean descrTemplatExist(Integer applyScene) {
        Example example = new Example(CouponDescrTemplate.class);
        example.createCriteria()
               .andEqualTo("status", "ENABLE")
               .andEqualTo("applyScene", applyScene);
        return templateMapper.selectCountByExample(example) > 0;
    }

    private int getUsedCount(Long id) {
        Example example = new Example(CouponPossess.class);
        Example.Criteria criteria = example.createCriteria();
        //business_stage大于0为已使用
        criteria.andEqualTo("couponId",id);
        criteria.andGreaterThan("businessStage",0);

        int count = couponPossessMapper.selectCountByExample(example);
        return count;

    }

    private int getNoUsedCount(Long id) {
        Example example = new Example(CouponPossess.class);
        Example.Criteria criteria = example.createCriteria();
        //business_stage大于0为已使用
        criteria.andEqualTo("couponId",id);
        criteria.andEqualTo("businessStage",0);

        int count = couponPossessMapper.selectCountByExample(example);
        return count;

    }

    private void save(Coupon source) {
        int rows = baseCouponMapper.insertSelective(source);
    }

    private void update(Coupon source) {
        Coupon coupon = baseCouponMapper.selectByPrimaryKey(source.getId());
        //修改的发行总量不能低于已领取的总量+预定锁住的数量
        if (source.getTotal() < (coupon.getIssue() + coupon.getPreLock())){
            throw new BusinessException(ExceptionEnum.COUPON_TOTAL_INVALID);
        }
        //删除缓存
        redis.deleteSingleCacheMap(RedisKey.BUFFERED_COUPONS, coupon.getId().toString());

        int rows = baseCouponMapper.updateByPrimaryKeySelective(source);
    }
}
