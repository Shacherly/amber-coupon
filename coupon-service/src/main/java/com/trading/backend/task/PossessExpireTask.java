package com.trading.backend.task;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.trading.backend.annotation.Traceable;
import com.trading.backend.common.util.TemporalUtil;
import com.trading.backend.config.CouponTaskProperty;
import com.trading.backend.config.DisposableUniqueScheduler;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.mapper.CouponPossessDao;
import com.trading.backend.pojo.ExpireDO;
import com.trading.backend.service.IPossesService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author ~~ trading.s
 * @date 12:55 10/15/21
 */
@Slf4j
@Component
public class PossessExpireTask {

    @Autowired
    private IPossesService possesService;
    @Autowired
    private CouponPossessDao possessDao;
    @Autowired
    private DisposableUniqueScheduler scheduler;
    @Autowired
    private CouponTaskProperty couponTaskProperty;


    /***
     * 10min
     */
    @XxlJob("exprPosses") @Traceable
    // @Scheduled(fixedRate = 1000L)
    public void exprPosses() {
        LocalDateTime now = LocalDateTime.now();
        List<ExpireDO> toExprs = possessDao.getExprs(
                TemporalUtil.offSetDaysStart(-1000),
                now.plus(couponTaskProperty.getExprsIntvl(), ChronoUnit.MILLIS)
        );

        if (CollectionUtil.isEmpty(toExprs)) return;

        toExprs.forEach(expr -> {
            Runnable runnable = () -> {
                CouponPossess possess = new CouponPossess();
                possess.expire();
                Example example = new Example(CouponPossess.class);
                example.createCriteria().andEqualTo("id", expr.getId())
                       .andEqualTo("possessStage", 0)
                       .andEqualTo("businessStage", 0);
                possesService.updatePossess(possess, example);
            };
            scheduler.schedule(runnable, String.valueOf(expr.getId()), DateUtil.toInstant(expr.getExprTime()), true);
        });
    }
}
