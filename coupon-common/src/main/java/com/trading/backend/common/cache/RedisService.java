package com.trading.backend.common.cache;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author ~~ trading.s
 * @date 19:57 10/14/21
 */
// @SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
public class RedisService {
    @Autowired
    public RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setCacheObject(final String key, final T value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }


    public <T> void setCacheObject(final String key, final T value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        return (T) operation.get(key);
    }

    public <T> T getCacheObject(String key, Class<T> tClass) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        return redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     *
     * @param key 缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key, Class<T> tClass) {
        List<Object> range = redisTemplate.opsForList().range(key, 0, -1);
        if (CollectionUtil.isEmpty(range)) return Collections.emptyList();
        return (List<T>) range;
        // return range.stream().map(v -> (T)v).collect(Collectors.toList());
    }

    /**
     * 缓存Set
     *
     * @param key 缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, Object> setCacheSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, Object> setOperation = redisTemplate.boundSetOps(key);
        for (T t : dataSet) {
            setOperation.add(t);
        }
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        return (Set<T>) redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<Object, Object> getCacheMap(final String key) {
        // redisTemplate.opsForHash().multiGet()
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final Object hKey, final T value) {
        redisTemplate.opsForHash().put(key, String.valueOf(hKey), value);
    }

    public <T> long deleteCacheMap(String key, List<Object> hkeys) {
        if (CollectionUtil.isEmpty(hkeys)) return 0;
        return redisTemplate.opsForHash().delete(key, hkeys.toArray());
    }

    /**
     * 删除单个hash key
     */
    public <T> long deleteSingleCacheMap(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.delete(key, hKey);
    }


    /**
     * 获取Hash中的数据
     *
     * @param key Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        List<Object> collect = redisTemplate.opsForHash().multiGet(key, hKeys)
                                            .stream().filter(Predicate.isEqual(null).negate()).collect(Collectors.toList());
        return (List<T>) collect;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    public Double incrment(final String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 返回存入的set size
     * @param key
     * @param tuples
     */
    public void setSortedSet(String key, Set<ZSetOperations.TypedTuple<Object>> tuples) {
        Long add = redisTemplate.opsForZSet().add(key, tuples);
    }

    public void setSortedSet(String key, ZSetOperations.TypedTuple<Object> tuple) {
        Long add = redisTemplate.opsForZSet().add(key, Collections.singleton(tuple));
    }

    public Set<ZSetOperations.TypedTuple<Object>> getSortedSet(String key, double min, double max) {
        Set<ZSetOperations.TypedTuple<Object>> tupleSet = redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
        return tupleSet;
    }

    public Set<Object> getSortedSetPage(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    public long removeZSetByValue(String key, Object... values) {
        return Optional.ofNullable(redisTemplate.opsForZSet().remove(key, values)).orElse(0L);
    }

    public long countZSet(String key, double min, double max) {
        return Optional.ofNullable(redisTemplate.opsForZSet().count(key, min, max)).orElse(0L);
    }

    public void addSet(String key, Collection<Object> values) {
        redisTemplate.opsForSet().add(key, values);
    }

    public void emptySet(String key) {
        Boolean delete = redisTemplate.delete(key);
    }

    public boolean existInSet(String key, String exist) {
        Boolean member = redisTemplate.opsForSet().isMember(key, exist);
        return Optional.ofNullable(member).orElse(false);
    }

}
