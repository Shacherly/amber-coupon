package com.trading.backend.config;


import com.trading.backend.client.ICoinServiceApi;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * @author ~~ trading.s
 * @date 11:20 10/15/21
 */
@Slf4j
@Order(1)
@Component @Profile({"dev", "sit", "uat", "prod"})
public class PostProcessor implements ApplicationRunner {

    @Value("${spring.profiles.active}")
    private String env;
    @Autowired
    private ICoinServiceApi coinServiceApi;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // SerializeConfig serializeConfig = SerializeConfig.globalInstance;
        // serializeConfig.put(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        // fastJsonConfig.setSerializeConfig(serializeConfig);
        Sentry.init(options -> {
            options.setDsn("http://be6b6e622836460fae140f59c222ab2f@10.140.34.30:9000/23");
            options.setTracesSampleRate(1.0);
            options.setEnvironment(env);
            options.setDebug(true);
        });

        Set<String> coins = coinServiceApi.cacheSupportCoins();
        log.info("PostProcessor, SUPPORTED_COINS = {}", coins);
    }
}
