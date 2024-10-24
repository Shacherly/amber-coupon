package com.trading.backend.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.trading.backend.http.request.earn.EarnMatchParam;
import com.trading.backend.http.response.earn.EarnMatchedResponse;
import com.trading.backend.http.response.earn.EarnPossessVO;
import com.trading.backend.service.ICouponEarnService;
import com.trading.backend.service.IPossesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * @author ~~ trading.s
 * @date 18:21 09/29/21
 */
@Slf4j
@Service
public class CouponEarnServiceImpl implements ICouponEarnService {

    @Autowired
    private IPossesService possesService;


    @Override
    public EarnMatchedResponse getMathCoupons(EarnMatchParam param) {
        List<EarnPossessVO> unMatch = new ArrayList<>();
        List<EarnPossessVO> match = possesService.getEarnMatchs(param, unMatch);
        String tips = Optional.ofNullable(match).filter(CollectionUtil::isNotEmpty).map(list -> list.get(0).getWorth()).orElse("");
        return new EarnMatchedResponse(tips, match, unMatch);
    }

}
