package com.service.ratelimiter.limiter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;

public class RedisTokenBucket implements RateLimitBackend{

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> script;

    public RedisTokenBucket(StringRedisTemplate redis) {
        this.redis = redis;
        this.script = new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("redis/token_bucket.lua"));
        this.script.setResultType(Long.class);
    }

    @Override
    public boolean allow(String key, int capacity, double refillPerSecond) {
        String bucketKey = "bucket:" + key;
        long nowMs = System.currentTimeMillis();

        Long allowed = redis.execute(
                script,
                Collections.singletonList(bucketKey),
                String.valueOf(capacity),
                String.valueOf(refillPerSecond),
                String.valueOf(nowMs)
        );

        return allowed == 1L;
    }
}
