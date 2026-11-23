package cn.edu.hbpu.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.UUID;

/**
 * Redis 滑动窗口限流；Redis 异常时自动回退到进程内实现。
 */
public class RedisRateLimitStore implements RateLimitStore {
    private static final Logger log = LoggerFactory.getLogger(RedisRateLimitStore.class);
    private static final String LUA =
            "local key=KEYS[1] "
                    + "local now=tonumber(ARGV[1]) "
                    + "local window=tonumber(ARGV[2]) "
                    + "local max=tonumber(ARGV[3]) "
                    + "redis.call('ZREMRANGEBYSCORE',key,0,now-window) "
                    + "local count=redis.call('ZCARD',key) "
                    + "if count >= max then return 0 end "
                    + "redis.call('ZADD',key,now,ARGV[4]) "
                    + "redis.call('PEXPIRE',key,window) "
                    + "return 1";
    private final StringRedisTemplate redisTemplate;
    private final RateLimitStore fallback;
    private final DefaultRedisScript<Long> script = new DefaultRedisScript<>(LUA, Long.class);

    public RedisRateLimitStore(StringRedisTemplate redisTemplate, RateLimitStore fallback) {
        this.redisTemplate = redisTemplate;
        this.fallback = fallback;
    }

    @Override
    public boolean allow(String key, long windowMillis, int maxRequests) {
        try {
            Long result = redisTemplate.execute(script,
                    Collections.singletonList("bookshop:rate:" + key),
                    String.valueOf(System.currentTimeMillis()),
                    String.valueOf(windowMillis),
                    String.valueOf(maxRequests),
                    UUID.randomUUID().toString());
            return result != null && result == 1L;
        } catch (Exception e) {
            log.debug("Redis 限流不可用，回退内存实现: {}", e.getMessage());
            return fallback.allow(key, windowMillis, maxRequests);
        }
    }
}
