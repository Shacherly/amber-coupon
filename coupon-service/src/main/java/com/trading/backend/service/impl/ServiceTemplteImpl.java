package com.trading.backend.service.impl;

import com.trading.backend.common.domain.BaseEntity;
import com.trading.backend.common.mapper.BaseMapper;
import com.trading.backend.exception.BusinessException;
import com.trading.backend.exception.ExceptionEnum;
import com.trading.backend.service.IServiceTemplte;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


/**
 * @author ~~ trading mu
 * @date 15:30 2022/03/21
 */
@Service @Slf4j
public class ServiceTemplteImpl implements IServiceTemplte {


    @Override
    public <ENTITY extends BaseEntity> ENTITY saveOrUpdate(ENTITY entity, BaseMapper<ENTITY> baseMapper) {
        return Optional.ofNullable(entity)
                       .map(arg -> {
                           // check
                           // consumer.accept(entity);
                           return arg;
                       })
                       .map(arg -> Optional
                               .ofNullable(entity.getId())
                               .map(id -> {
                                   log.info("Update entity = {}", entity);
                                   return updateByKey(entity, baseMapper);
                               })
                               .orElseGet(() -> {
                                   log.info("Insert entity = {}", entity);
                                   return save(entity, baseMapper);
                               })
                       )
                       .orElseThrow(() -> new BusinessException(ExceptionEnum.ARGUMENT_NULL, "entity"));
    }


    private <T extends BaseEntity> T updateByKey(T source, BaseMapper<T> baseMapper) {
        source.setUtime(LocalDateTime.now());
        baseMapper.updateByPrimaryKeySelective(source);
        return source;
    }

    private <T extends BaseEntity> T save(T source, BaseMapper<T> baseMapper) {
        baseMapper.insertSelective(source);
        return source;
    }
}
