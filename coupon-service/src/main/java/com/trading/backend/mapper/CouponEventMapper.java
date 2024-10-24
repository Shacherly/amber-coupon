package com.trading.backend.mapper;

import com.trading.backend.common.mapper.BaseMapper;
import com.trading.backend.domain.CouponEvent;

import java.util.List;

public interface CouponEventMapper extends BaseMapper<CouponEvent> {

    int batchInsert(List<CouponEvent> records);

}