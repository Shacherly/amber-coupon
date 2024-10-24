package com.trading.backend.service;


import com.trading.backend.losermapper.PfAssetCouponRuleMapper;
import com.trading.backend.losermapper.PfCouponMapper;
import com.trading.backend.losermapper.PfDeductCouponRuleMapper;
import com.trading.backend.losermapper.PfInterestCouponRuleMapper;
import com.trading.backend.domain.PfAssetCouponRule;
import com.trading.backend.domain.PfCoupon;
import com.trading.backend.domain.PfDeductCouponRule;
import com.trading.backend.domain.PfInterestCouponRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Slf4j
@Service
public class OriginSourceService {

    @Autowired(required = false)
    private PfCouponMapper pfCouponMapper;
    @Autowired(required = false)
    private PfDeductCouponRuleMapper deductRuleMapper;
    @Autowired(required = false)
    private PfInterestCouponRuleMapper interestRuleMapper;
    @Autowired(required = false)
    private PfAssetCouponRuleMapper assetRuleMapper;

    public List<PfCoupon> getOriginCoupons() {
        Example example = new Example(PfCoupon.class);
        // example.createCriteria().andEqualTo("type", 0);
        // List<PfCoupon> list = PageContext.seletList(() -> pfCouponMapper.selectAll());
        List<PfCoupon> list = pfCouponMapper.selectAll();
        return list;
    }

    public List<PfDeductCouponRule> getOriginDeductRules() {
        return deductRuleMapper.selectAll();
    }

    public List<PfInterestCouponRule> getOriginInteresRules() {
        return interestRuleMapper.selectAll();
    }

    public List<PfAssetCouponRule> getOriginAssetRules() {
        return assetRuleMapper.selectAll();
    }
}
