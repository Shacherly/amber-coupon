package com.trading.backend.kafka.subscriber;


import cn.hutool.json.JSONUtil;
import com.trading.backend.annotation.Traceable;
import com.trading.backend.common.cache.RedisService;
import com.trading.backend.config.ExecutorConfigurer;
import com.trading.backend.config.tradingCoins;
import com.trading.backend.kafka.message.DepositInfoModel;
import com.trading.backend.kafka.message.EarnConsumeModel;
import com.trading.backend.kafka.message.KycStageModel;
import com.trading.backend.kafka.message.UserRegistryModel;
import com.trading.backend.service.ICashCouponService;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * @author ~~ trading.s
 * @date 18:16 10/09/21
 */
@Slf4j
@Component
@Profile({"dev", "sit", "uat", "prod"})
public class ActivateSubscriber {

    @Autowired
    private IPossesService possesService;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private ICashCouponService iCashCoupService;
    @Autowired
    private ExecutorConfigurer executor;
    @Autowired
    private RedisService redis;

    @Value("${spring.profiles.active}")
    private String springEnv;

    private boolean localEnv() {
        return springEnv.contains("local");
    }


    /**
     * registry topic用户发放cash券
     * @param record
     */
    @Traceable
    @KafkaListener(topics = "${topics.registry}", groupId = "trading-trading-coupon-registry-prod")
    public void onUserRegistry(ConsumerRecord<?, ?> record) {
        Optional<?> value = Optional.ofNullable(record.value());
        if (value.isPresent()) {
            UserRegistryModel registry = JSONUtil.toBean(value.get().toString(), UserRegistryModel.class);
            log.info("onUserRegistry = {}", registry);
            try {
                if (localEnv())
                    TimeUnit.SECONDS.sleep(1);
                executor.getExecutor().execute(() -> iCashCoupService.grantOnRegistry(registry));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }


    /**
     * kyc topic用于激活cash
     * @param record
     */
    @Traceable
    @KafkaListener(topics = "${topics.kyc}", groupId = "trading-trading-coupon-kyc-prod")
    public void onUserKycAdvance(ConsumerRecord<?, ?> record) {
        Optional<?> value = Optional.ofNullable(record.value());
        if (value.isPresent()) {
            KycStageModel kycStageModel = JSONUtil.toBean(value.get().toString(), KycStageModel.class);
            log.info("onUserKycAdvance = {}", kycStageModel);
            try {
                if (localEnv())
                    TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            iCashCoupService.activOnKycSubmit(kycStageModel, iCashCoupService.postback());

        }

    }


    /**
     * 充值topic用于激活cash
     * @param record
     */
    @Traceable
    @KafkaListener(topics = "${topics.deposit}", groupId = "trading-trading-coupon-deposit-prod")
    public void onUserDepositSettle(ConsumerRecord<?, ?> record) {
        Optional<?> value = Optional.ofNullable(record.value());
        if (value.isPresent()) {
            DepositInfoModel depositModel = JSONUtil.toBean(value.get().toString(), DepositInfoModel.class);
            depositActivate(depositModel);
        }
    }

    /**
     * 线上买币
     * @param record
     */
    @Traceable
    @KafkaListener(topics = "${topics.purchase}", groupId = "trading-trading-coupon-buycrypto-prod")
    public void onUserPurchaseSettle(ConsumerRecord<?, ?> record) {
        Optional<?> value = Optional.ofNullable(record.value());
        if (value.isPresent()) {
            DepositInfoModel depositModel = JSONUtil.toBean(value.get().toString(), DepositInfoModel.class);
            depositActivate(depositModel);
        }
    }

    private void depositActivate(DepositInfoModel model) {
        log.info("onUserDepositSettle = {}", model);
        try {
            if (localEnv())
                TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        model.setDepositCoin(tradingCoins.getCoin(model.getDepositCoin()));
        iCashCoupService.activOnDepositSettle(model, iCashCoupService.postback());
    }


    /**
     * 理财topic用于激活cash
     * @param record
     */
    @Traceable
    @KafkaListener(topics = "${topics.earn}", groupId = "trading-trading-coupon-earn-prod")
    public void onUserSubscribe(ConsumerRecord<?, ?> record) {
        Optional<?> value = Optional.ofNullable(record.value());
        if (value.isPresent()) {
            EarnConsumeModel earnModel = JSONUtil.toBean(value.get().toString(), EarnConsumeModel.class);
            log.info("onUserSubscribe = {}", earnModel);
            try {
                if (localEnv())
                    TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            executor.getExecutor().execute(
                    () -> iCashCoupService.activOnUserSubscribe(earnModel, iCashCoupService.postback()));

            executor.getExecutor().execute(
                    () -> iCashCoupService.onRedeemRefreshCashStage(earnModel, iCashCoupService.postback()));

        }

    }
}
