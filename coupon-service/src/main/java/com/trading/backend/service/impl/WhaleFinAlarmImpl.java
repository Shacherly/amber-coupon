package com.trading.backend.service.impl;


import com.trading.backend.config.CouponAlarmProperty;
import com.trading.backend.service.ItradingAlarm;
import com.trading.backend.util.RemoteCaller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ~~ trading.s
 * @date 16:05 11/18/21
 */
@Service @Slf4j
public class tradingAlarmImpl implements ItradingAlarm {

    @Autowired
    private RemoteCaller caller;
    @Autowired
    private CouponAlarmProperty alarmProperty;



    @Override
    public void alarm(CouponAlarmProperty.AlarmBody alarmBody) {
        String url = alarmProperty.getUrl();
        caller.post4PageItems(url, alarmBody, null);
    }
}
