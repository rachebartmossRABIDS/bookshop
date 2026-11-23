package cn.edu.hbpu.configuration;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRateLimitStore implements RateLimitStore {
    private static final int MAX_KEYS = 1024;
    private final Map<String, Deque<Long>> hits = new ConcurrentHashMap<>();

    @Override
    public boolean allow(String key, long windowMillis, int maxRequests) {
        long now = System.currentTimeMillis();
        Deque<Long> queue = hits.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (queue) {
            while (!queue.isEmpty() && now - queue.peekFirst() > windowMillis) {
                queue.pollFirst();
            }
            if (queue.size() >= maxRequests) {
                return false;
            }
            queue.addLast(now);
        }
        if (hits.size() > MAX_KEYS) {
            sweep(now, windowMillis);
        }
        return true;
    }

    private void sweep(long now, long windowMillis) {
        Iterator<Map.Entry<String, Deque<Long>>> iterator = hits.entrySet().iterator();
        while (iterator.hasNext()) {
            Deque<Long> queue = iterator.next().getValue();
            synchronized (queue) {
                while (!queue.isEmpty() && now - queue.peekFirst() > windowMillis) {
                    queue.pollFirst();
                }
                if (queue.isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }
}
