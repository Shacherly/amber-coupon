package com.trading.backend.mapper;

import com.trading.backend.common.mapper.BaseMapper;
import com.trading.backend.domain.Coupon;
import com.trading.backend.pojo.CouponRuleDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CouponMapper extends BaseMapper<Coupon> {

    int batchInsert(@Param("records") List<Coupon> records);

    int batchUpdate(@Param("records") List<Coupon> records);

    List<CouponRuleDO> getRulesOnly(@Param("couponIds") List<Long> couponIds);

    int issuesUpdate(@Param("couponIds") List<Long> couponIds, @Param("delta") int delta);

    int issuesPrelokcUpdate(@Param("couponIds") List<Long> couponIds, @Param("delta") int delta);

    int preLockUpdate(@Param("couponIds") List<Long> couponIds, @Param("delta") int delta);

    int releasePrelock(@Param("couponIds") List<Long> couponIds, @Param("delta") int delta);
}