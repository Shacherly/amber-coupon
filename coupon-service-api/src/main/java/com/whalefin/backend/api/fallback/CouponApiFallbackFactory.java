package com.trading.backend.api.fallback;

import com.trading.backend.api.CouponServiceApi;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author ~~ trading mu
 * @date 16:05 04/07/22
 */
@Slf4j
@Component
public class CouponApiFallbackFactory implements FallbackFactory<CouponServiceApi> {


    @Override
    public CouponServiceApi create(Throwable throwable) {
        throw new RuntimeException("Fallback need to be overridden!");
    }


}
