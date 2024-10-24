package com.trading.backend.service.impl;


import com.trading.backend.http.request.loan.LoanMatchParam;
import com.trading.backend.http.response.loan.LoanPossessVO;
import com.trading.backend.http.response.loan.LoanMatchedResponse;
import com.trading.backend.mapper.CouponMapper;
import com.trading.backend.service.ICouponLoanService;
import com.trading.backend.service.ICouponService;
import com.trading.backend.service.IPossesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ~~ trading.s
 * @date 18:21 09/29/21
 */
@Slf4j
@Service
public class CouponLoanServiceImpl implements ICouponLoanService {

    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private ICouponService couponService;
    @Autowired
    private IPossesService possesService;


    @Override
    public LoanMatchedResponse getMathCoupons(LoanMatchParam param) {
        List<LoanPossessVO> unMatch = new ArrayList<>();
        List<LoanPossessVO> match = possesService.getLoanMatchs(param, unMatch);
        return new LoanMatchedResponse(match, unMatch);
    }

}
