package com.trading.backend.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.trading.backend.config.GlobalSystemProperty;
import com.trading.backend.constant.Constant;
import com.trading.backend.domain.InterCouponRule;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.exception.VisibleException;
import com.trading.backend.http.request.CouponCreateParam;
import com.trading.backend.service.IValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * @author ~~ trading.s
 * @date 13:19 11/17/21
 */
@Slf4j
@Service
public class Validator implements IValidator {

    @Autowired
    private GlobalSystemProperty property;


    @Override
    public void validate(CouponCreateParam param) throws VisibleException {
        if (getByCode(param.getType()) == INTEREST_TYPE) {
            InterCouponRule rule = param.getInterCouponRule();
            if (rule.getInterDays() > rule.getMinSubscrDays())
                throw new VisibleException(ExceptionEnum.ILLEGAL_INTER_PERIOD);
        }
        else if (getByCode(param.getType()) == DEDUCTION_TYPE) {

        }
        else if (getByCode(param.getType()) == CASHRETURN_TYPE) {
            Optional.ofNullable(param.getWorthCoin())
                    .map(String::toUpperCase)
                    .filter(coin -> property.support(coin))
                    .orElseThrow(() -> new VisibleException(ExceptionEnum.UUNSUPPORTED_COIN, "worth_coin[" + param.getWorthCoin() + "]"));
        }
    }


    @Override
    public void validateMultiLang(JSONObject json) throws VisibleException {
        Optional.ofNullable(json)
                .map(var -> var.getString(Constant.LANG_EN_US))
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new VisibleException(null));
    }
}
