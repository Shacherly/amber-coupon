package com.trading.backend.client;

import com.trading.backend.http.request.earn.opponent.CashEarnAcquireParam;
import com.trading.backend.http.response.earn.CashEarnAcquireVO;
import com.trading.backend.http.response.earn.PositionVO;
import com.trading.backend.http.request.earn.opponent.PositionAcquireParam;

import java.util.List;

public interface IEarnServiceApi {

    List<PositionVO> getPositions(PositionAcquireParam param);

    CashEarnAcquireVO getCashEarnAcquireVO(CashEarnAcquireParam param);
}
