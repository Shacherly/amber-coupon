package com.trading.backend.service;

import com.trading.backend.http.request.loan.LoanMatchParam;
import com.trading.backend.http.response.loan.LoanMatchedResponse;

public interface ICouponLoanService {

    LoanMatchedResponse getMathCoupons(LoanMatchParam param);
}
