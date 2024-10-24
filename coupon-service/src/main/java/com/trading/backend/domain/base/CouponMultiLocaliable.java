package com.trading.backend.domain.base;


import com.alibaba.fastjson.JSON;
import com.trading.backend.http.ContextHeader;
import com.trading.backend.util.ContextHolder;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * @author ~~ trading.s
 * @date 11:31 10/06/21
 */
public interface CouponMultiLocaliable {
    //
    // default String getCouponTitle() {return null;};
    //
    // default String getCouponDescr() {return null;};
    //
    // default String getTitle() {return null;};
    //
    // default String getDescr() {return null;};

    default String toLocalized(String multi) {
        if (StringUtils.isBlank(multi)) return null;
        ContextHeader client = ContextHolder.get();
        String clientLang = client.getClientLanguage();
        String adapaterKey = clientLang.replaceAll("-", "_");
        return Optional.ofNullable(JSON.parseObject(multi))
                       .map(val -> Optional.ofNullable(val.getString(clientLang))
                                           .orElseGet(
                                                   () -> Optional.ofNullable(val.getString(adapaterKey))
                                                                 .orElseGet(() -> Optional.ofNullable(val.getString("en-US")).orElseGet(() -> val.getString("en_US")))
                                           )
                       )
                       .orElse(null);
    }

    // default String getTitleLocalized() {
    //     return getLocalized(Optional.ofNullable(getCouponTitle()).orElseGet(this::getTitle));
    // }

    // default String getDescrLocalized() {
    //     return getLocalized(Optional.ofNullable(getCouponDescr()).orElseGet(this::getDescr));
    // }
}
