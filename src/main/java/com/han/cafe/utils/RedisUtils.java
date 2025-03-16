package com.han.cafe.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * Redis工具类
 */
@Component
public class RedisUtils {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置过期时间
     * @param key 键
     * @param time 时间(秒)
     * @return 是否成功
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取过期时间
     * @param key 键
     * @return 时间(秒) 返回0代表永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个或多个值
     */
    public void delete(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete((Collection<String>) List.of(key));
            }
        }
    }

    // ============================String类型操作=============================

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     * @param key 键
     * @param delta 递增因子(大于0)
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     * @param key 键
     * @param delta 递减因子(大于0)
     * @return 递减后的值
     */
    public long decrement(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    // ============================Hash类型操作=============================

    /**
     * Hash获取指定key对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Hash获取指定key对应的field的值
     * @param key 键
     * @param field 项
     * @return 值
     */
    public Object hashGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * Hash设置一个field的值
     * @param key 键
     * @param field 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hashSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hash设置一个field的值并设置过期时间
     * @param key 键
     * @param field 项
     * @param value 值
     * @param time 时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true成功 false失败
     */
    public boolean hashSet(String key, String field, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hash批量设置多个field的值
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hashMultiSet(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hash删除字段
     * @param key 键
     * @param fields 项
     */
    public void hashDelete(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键
     * @param field 项
     * @return true 存在 false不存在
     */
    public boolean hashHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ============================Set类型操作=============================

    /**
     * 根据key获取Set中的所有值
     * @param key 键
     * @return Set集合
     */
    public Set<Object> setMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 向Set中添加值
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功数量
     */
    public long setAdd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // ============================ZSet（有序集合）类型操作=============================

    /**
     * 向有序集合添加一个成员，或者更新已存在成员的分数
     * @param key 键
     * @param value 值
     * @param score 分数
     * @return 是否成功
     */
    public boolean zAdd(String key, String value, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取有序集合的成员数
     * @param key 键
     * @return 成员数
     */
    public long zSize(String key) {
        try {
            Long size = redisTemplate.opsForZSet().size(key);
            return size == null ? 0 : size;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 有序集合中对指定成员的分数加上增量increment
     * @param key 键
     * @param value 值
     * @param increment 增量
     * @return 新分数
     */
    public Double zIncrementScore(String key, String value, double increment) {
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, increment);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回有序集中指定区间内的成员，通过索引，分数从高到低
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引
     * @return 成员集合
     */
    public Set<Object> zReverseRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 返回有序集中指定区间内的成员和分数，通过索引，分数从高到低
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引
     * @return 成员和分数集合
     */
    public Set<Object> zReverseRangeWithScores(String key, long start, long end) {
        try {
            return (Set) redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 移除有序集合中的一个或多个成员
     * @param key 键
     * @param values 值（一个或多个）
     * @return 移除的成员数量
     */
    public long zRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForZSet().remove(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取有序集合中指定成员的分数
     * @param key 键
     * @param value 成员
     * @return 分数
     */
    public Double zScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取有序集合中成员的排名（从0开始计数，按分数从小到大排序）
     * @param key 键
     * @param value 成员
     * @return 排名，从0开始
     */
    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取有序集合中成员的排名（从0开始计数，按分数从大到小排序）
     * @param key 键
     * @param value 成员
     * @return 排名，从0开始
     */
    public Long zReverseRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().reverseRank(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 