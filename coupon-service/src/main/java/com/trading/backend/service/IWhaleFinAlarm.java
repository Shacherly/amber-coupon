package com.trading.backend.service;

import com.trading.backend.config.CouponAlarmProperty;

public interface ItradingAlarm {

    void alarm(CouponAlarmProperty.AlarmBody alarmBody);

}
