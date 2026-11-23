package cn.edu.hbpu.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRateLimitStoreTest {
    @Test
    void enforcesSlidingWindowLimit() {
        RateLimitStore store = new InMemoryRateLimitStore();
        assertTrue(store.allow("127.0.0.1", 60_000, 3));
        assertTrue(store.allow("127.0.0.1", 60_000, 3));
        assertTrue(store.allow("127.0.0.1", 60_000, 3));
        assertFalse(store.allow("127.0.0.1", 60_000, 3));
        assertTrue(store.allow("127.0.0.2", 60_000, 3));
    }
}
