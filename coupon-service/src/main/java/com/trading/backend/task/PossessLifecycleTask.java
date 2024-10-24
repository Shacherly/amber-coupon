package com.trading.backend.task;


import com.trading.backend.annotation.Traceable;
import com.trading.backend.common.enums.CouponApplySceneEnum;
import com.trading.backend.common.enums.PossessBusinesStageEnum;
import com.trading.backend.common.enums.PossessStageEnum;
import com.trading.backend.config.ExecutorConfigurer;
import com.trading.backend.domain.CouponPossess;
import com.trading.backend.mapper.CouponPossessMapper;
import com.trading.backend.service.ICouponService;
import com.trading.backend.util.PageContext;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author ~~ trading.s
 * @date 12:55 10/15/21
 */
@Slf4j
@Component
public class PossessLifecycleTask {

    @Autowired
    private CouponPossessMapper possessMapper;
    @Autowired
    private ExecutorConfigurer executor;
    @Autowired
    private ICouponService couponService;



    @XxlJob("reActivate") @Traceable
    public void reActivate() {

        Example example = new Example(CouponPossess.class);
        example.createCriteria()
               .andEqualTo("applyScene", CouponApplySceneEnum.KYC_VERIFY_CASH.getCode())
               .andEqualTo("possessStage", PossessStageEnum.ENABLE.getCode())
               .andEqualTo("businessStage", PossessBusinesStageEnum.DEFAULT_UNUSED.getCode());

        long total = possessMapper.selectCountByExample(example);

        BiFunction<Integer, Integer, List<CouponPossess>> biProducer =
                (page, pageSize) -> PageContext.selectList(() -> possessMapper.selectByExample(example), page, pageSize, "id asc");
        Function<List<String>, Integer> consumer = possesses -> couponService.cashKycReactiv(possesses);
        executor.serial(total, 40, biProducer, CouponPossess::getUid, consumer);

    }
}
