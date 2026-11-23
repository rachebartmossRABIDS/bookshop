package cn.edu.hbpu.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

/**
 * Redis JSON 缓存。所有 Redis 异常均降级为缓存未命中，不阻断数据库查询。
 */
@Service
@RequiredArgsConstructor
public class CacheService {
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    private static final ObjectMapper JSON = new ObjectMapper().findAndRegisterModules();

    private final StringRedisTemplate redisTemplate;
    @Value("${redis.enabled:true}")
    private boolean enabled;

    public <T> T get(String key, Class<T> type) {
        if (!enabled) {
            return null;
        }
        try {
            String value = redisTemplate.opsForValue().get(key);
            return value == null ? null : JSON.readValue(value, type);
        } catch (Exception e) {
            log.debug("Redis 缓存读取失败，降级数据库: {}", e.getMessage());
            return null;
        }
    }

    public <T> T get(String key, TypeReference<T> type) {
        if (!enabled) {
            return null;
        }
        try {
            String value = redisTemplate.opsForValue().get(key);
            return value == null ? null : JSON.readValue(value, type);
        } catch (Exception e) {
            log.debug("Redis 缓存读取失败，降级数据库: {}", e.getMessage());
            return null;
        }
    }

    public void put(String key, Object value, long ttlSeconds) {
        if (!enabled) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(key, JSON.writeValueAsString(value),
                    Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            log.debug("Redis 缓存写入失败，继续使用数据库: {}", e.getMessage());
        }
    }

    public void evict(String pattern) {
        if (!enabled) {
            return;
        }
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.debug("Redis 缓存失效失败: {}", e.getMessage());
        }
    }
}
