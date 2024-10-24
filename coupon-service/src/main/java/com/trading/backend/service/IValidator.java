package com.trading.backend.service;

import com.alibaba.fastjson.JSONObject;
import com.trading.backend.exception.VisibleException;
import com.trading.backend.http.request.CouponCreateParam;

public interface IValidator {

    void validate(CouponCreateParam param) throws VisibleException;

    void validateMultiLang(JSONObject json) throws VisibleException;
}
