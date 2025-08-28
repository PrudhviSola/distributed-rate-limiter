package com.service.ratelimiter.limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTokenBucket implements RateLimitBackend{

    private static final class Bucket {
        double tokens;
        long lastRefillMs;

        Bucket(double tokens, long lastRefillMs) {
            this.tokens = tokens;
            this.lastRefillMs = lastRefillMs;
        }
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean allow(String key, int capacity, double refillPerSecond) {
        long now = System.currentTimeMillis();
        Bucket current = buckets.computeIfAbsent(key, k -> new Bucket(capacity, now));

        synchronized (current) {
            long deltaMs = Math.max(0, now - current.lastRefillMs);
            double refill = (deltaMs / 1000.0) * refillPerSecond;
            current.tokens = Math.min(capacity, current.tokens + refill);
            current.lastRefillMs = now;

            if (current.tokens >= 1.0) {
                current.tokens -= 1.0;
                return true;
            }
            return false;
        }
    }
}
