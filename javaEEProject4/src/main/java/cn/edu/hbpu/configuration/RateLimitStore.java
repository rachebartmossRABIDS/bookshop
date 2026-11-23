package cn.edu.hbpu.configuration;

public interface RateLimitStore {
    boolean allow(String key, long windowMillis, int maxRequests);
}
