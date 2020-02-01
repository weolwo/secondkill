package com.poplar.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * by poplar created on 2020/2/1
 * redis常用方法工具
 */
@Service
public class RedisHelper {

    @Autowired
    private JedisPool jedisPool;

    //根据key获取值
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz) {
        Jedis jedis = jedisPool.getResource();
        String realKey = prefix.getPrefix() + key;
        String value = jedis.get(realKey);
        jedis.close();
        return strToObject(value, clazz);
    }

    //根据key获取值
    public <T> boolean set(KeyPrefix prefix, String key, T value) {
        Jedis jedis = jedisPool.getResource();
        String realKey = prefix.getPrefix() + key;
        String result = objectToString(value);
        int expireSeconds = prefix.expireSeconds();
        String res;
        if (expireSeconds <= 0) {
            res = jedis.set(realKey, result);
        } else {
            res = jedis.setex(realKey, expireSeconds, result);
        }
        jedis.close();
        return "OK".equalsIgnoreCase(res);
    }

    public <T> boolean exist(KeyPrefix prefix, String key) {
        Jedis jedis = jedisPool.getResource();
        String realKey = prefix.getPrefix() + key;
        jedis.close();
        return jedis.exists(realKey);
    }

    public <T> Long incr(KeyPrefix prefix, String key) {
        Jedis jedis = jedisPool.getResource();
        String realKey = prefix.getPrefix() + key;
        final Long result = jedis.incr(realKey);
        jedis.close();
        return result;
    }

    public <T> Long decr(KeyPrefix prefix, String key) {
        Jedis jedis = jedisPool.getResource();
        String realKey = prefix.getPrefix() + key;
        final Long result = jedis.decr(realKey);
        jedis.close();
        return result;
    }

    private <T> String objectToString(T value) {
        if (value == null) {
            return null;
        }
        final Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return String.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return String.valueOf(value);
        } else if (clazz == String.class) {
            return (String) value;
        } else {
            return JSON.toJSONString(value);
        }
    }

    private <T> T strToObject(String value, Class<T> clazz) {
        if (value == null || value.length() <= 0 || clazz == null) {
            return null;
        } else if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(value);
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(value);
        } else if (clazz == String.class) {
            return (T) value;
        } else {
            return JSON.toJavaObject(JSON.parseObject(value), clazz);
        }
    }


}
