package com.trading.backend.common.mapper;

import tk.mybatis.mapper.common.ConditionMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author ~~ trading.s
 * @date 15:33 09/21/21
 */
public interface BaseMapper<T> extends Mapper<T>, ConditionMapper<T> {
}
