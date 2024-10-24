package com.trading.backend.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.trading.backend.client.IEarnServiceApi;
import com.trading.backend.common.http.PageResult;
import com.trading.backend.common.util.Functions;
import com.trading.backend.config.RemoteServerProperty;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.http.Response;
import com.trading.backend.http.request.earn.opponent.CashEarnAcquireParam;
import com.trading.backend.http.response.earn.CashEarnAcquireVO;
import com.trading.backend.http.response.earn.PositionVO;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.http.request.earn.opponent.PositionAcquireParam;
import com.trading.backend.util.RemoteCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author ~~ trading.s
 * @date 12:34 10/15/21
 */
@Service @Slf4j
public class EarnServiceApiImpl implements IEarnServiceApi {

    @Autowired
    private RemoteCaller caller;
    @Autowired
    private RemoteServerProperty serverProperty;

    @Override
    public List<PositionVO> getPositions(PositionAcquireParam param) {
        if (Objects.isNull(param) || param.allNull())
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "param");
        String url = serverProperty.getDomain() + serverProperty.getEarnServer().getPositionList();
        JSONObject ignoreNullParam = JSONObject.parseObject(JSONObject.toJSONString(param));
        Response<PageResult<PositionVO>> pageResponse = caller.postForEntity(url, ignoreNullParam, null, new TypeReference<Response<PageResult<PositionVO>>>() {});
        return pageResponse.getData().getItems();
    }

    @Override
    public CashEarnAcquireVO getCashEarnAcquireVO(CashEarnAcquireParam param) {
        if (Objects.isNull(param) || param.hasAnyNull())
            throw new BusinessException(ExceptionEnum.ARGUMENT_NULL, "param");

        PositionAcquireParam request = new PositionAcquireParam();
        request.setUser_ids(Collections.singletonList(param.getUser_id()));
        request.setCoins(Functions.toList(param.getCoin_rule(), CashEarnAcquireParam.EarnCoinRule::getHolding_coin, String::toUpperCase));
        request.setStatus(Lists.newArrayList("HOLDING", "FINISH"));// FINISH HOLDING

        List<PositionVO> positions = this.getPositions(request);
        Predicate<PositionVO> filter = posVo -> posVo.eligibleHoldingOrEnded(param);
        PositionVO eligiblePosition =
                positions.stream().filter(filter).min(Comparator.comparing(PositionVO::getCreated_time)).orElse(null);
        return Optional.ofNullable(eligiblePosition)
                       .map(pos -> new CashEarnAcquireVO(pos.getPosition_id(), pos.getCreated_time()))
                       .orElse(null);
    }
}
