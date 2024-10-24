package com.trading.backend.losermapper;

import com.trading.backend.common.mapper.BaseMapper;
import com.trading.backend.domain.PfCouponRewardDetail;
import com.trading.backend.domain.RewardDetailWithRuleOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PfCouponRewardDetailMapper extends BaseMapper<PfCouponRewardDetail> {

    List<RewardDetailWithRuleOrder> selectJoinReward(@Param("copnType") Integer copnType);


}