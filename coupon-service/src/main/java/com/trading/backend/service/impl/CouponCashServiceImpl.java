package com.trading.backend.service.impl;


import com.trading.backend.client.IEarnServiceApi;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.kafka.message.EarnConsumeModel;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.service.ICouponCashService;
import com.trading.backend.service.IPossesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author ~~ trading.s
 * @date 12:56 10/16/21
 */
@Slf4j
@Service
public class CouponCashServiceImpl implements ICouponCashService {

    @Autowired
    private IPossesService possesService;
    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private IEarnServiceApi earnServiceApi;
    @Autowired
    private RedisService redis;

    @Override
    public void onRedeemRefreshCashStage(EarnConsumeModel model) {


    }
}
