package com.trading.backend.kafka.subscriber;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author ~~ trading.s
 * @date 18:16 10/09/21
 */
@Slf4j
@Component
public class UserActionSubscriber {

    @Value("${spring.profiles.active}")
    private String springEnv;

    private boolean localEnv() {
        return springEnv.contains("local");
    }


    // @KafkaListener(topics = "${topics.login}")
    public void onUserLogin(ConsumerRecord<?, ?> record) {
        Optional<?> value = Optional.ofNullable(record.value());
        if (value.isPresent()) {

        }
    }
}
