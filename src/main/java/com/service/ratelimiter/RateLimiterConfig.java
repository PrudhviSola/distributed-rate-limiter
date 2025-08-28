package com.service.ratelimiter;

import com.service.ratelimiter.limiter.InMemoryTokenBucket;
import com.service.ratelimiter.limiter.RateLimitBackend;
import com.service.ratelimiter.limiter.RedisTokenBucket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RateLimiterConfig {
    @Bean
    public RateLimitBackend rateLimitBackend(
            StringRedisTemplate redis,
            @Value("${ratelimiter.useRedis:true}") boolean useRedis
    ) {
        return useRedis ? new RedisTokenBucket(redis) : new InMemoryTokenBucket();
    }
}