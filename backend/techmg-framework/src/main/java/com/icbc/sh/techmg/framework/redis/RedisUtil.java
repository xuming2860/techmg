package com.icbc.sh.techmg.framework.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ---- basic ops ----

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    public long getExpire(String key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        return expire != null ? expire : -1;
    }

    // ---- list helpers ----

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key) {
        return (List<T>) redisTemplate.opsForValue().get(key);
    }

    // ---- map helpers ----

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getMap(String key) {
        return (Map<String, T>) redisTemplate.opsForValue().get(key);
    }
}
