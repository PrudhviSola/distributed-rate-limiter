package com.service.ratelimiter.limiter;

public interface RateLimitBackend {
    /* *
     * Try to consume 1 token for the given key.
     * @param key bucket key (user/ip)
     * @param capacity max tokens in bucket
     * @param refillPerSecond tokens refilled per second (double allowed)
     * @return true if allowed, false if limited
     * */

    boolean allow(String key, int capacity, double refillPerSecond);
}
