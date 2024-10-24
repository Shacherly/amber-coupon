package com.trading.backend.service;

import com.trading.backend.common.domain.BaseEntity;
import com.trading.backend.common.mapper.BaseMapper;

public interface IServiceTemplte {

    <ENTITY extends BaseEntity> ENTITY saveOrUpdate(ENTITY source, BaseMapper<ENTITY> baseMapper);
}
