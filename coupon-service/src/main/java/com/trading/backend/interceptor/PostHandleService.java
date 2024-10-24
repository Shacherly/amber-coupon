package com.trading.backend.interceptor;


import com.trading.backend.annotation.Handler;
import com.trading.backend.bo.BasalExportPossessBO;
import com.trading.backend.common.util.Functions;
import com.trading.backend.service.ICouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;


/**
 * @author ~~ trading.s
 * @date 15:36 01/24/22
 */
@Component
public class PostHandleService {

    @Autowired
    private ICouponService couponService;

    public <T, R> Function<T, R> getMethodPostInvoke(Handler handler, Class<?> returnCla) {
        if (handler == Handler.NULL) {
            return null;
        }
        else if (handler == Handler.COUPON_ACTIVATE) {
            Function<List<String>, Integer> function = uids -> couponService.cashKycReactiv(uids);
            if (returnCla.isAssignableFrom(List.class)) {
                Function<List<BasalExportPossessBO>, List<String>> compose = possBos -> Functions.toList(possBos, BasalExportPossessBO::getUid);
                return (Function<T, R>) function.compose(compose);
            }
            else {
                Function<BasalExportPossessBO, List<String>> compose = possBo -> Collections.singletonList(possBo.getUid());
                return (Function<T, R>) function.compose(compose);
            }
        }
        return null;
    }
}
