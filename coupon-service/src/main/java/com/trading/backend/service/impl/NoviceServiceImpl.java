package com.trading.backend.service.impl;


import com.trading.backend.http.response.TaskProgressRes;
import com.trading.backend.kafka.message.NoviceValidUserModel;
import com.trading.backend.kafka.publisher.KafkaPublisher;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.INoviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



/**
 * @author ~~ trading.s
 * @date 16:02 11/02/21
 */
@Slf4j
@Service
public class NoviceServiceImpl implements INoviceService {

    @Autowired
    private ICouponService couponService;
    @Autowired
    private KafkaPublisher kafkaPublisher;
    @Value("${topics.valid-novice}")
    private String validNoviceTopic;


    @Override
    public void updateNoviceProgress(String uid) {
        TaskProgressRes taskProgress = couponService.taskProgress(uid);
        log.info("taskProgress={}", taskProgress);
        if (taskProgress.getKycTaskStatus() == 3
                && taskProgress.getAmountTaskStatus() == 3
                && taskProgress.getEarnTaskStatus() == 3) {

            kafkaPublisher.publish(validNoviceTopic, new NoviceValidUserModel(uid));
        }
    }
}
