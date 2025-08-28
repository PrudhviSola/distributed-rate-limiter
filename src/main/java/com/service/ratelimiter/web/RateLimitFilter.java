package com.service.ratelimiter.web;

import com.service.ratelimiter.limiter.RateLimitBackend;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitBackend backend;
    private final MeterRegistry meterRegistry;

    private final int capacity;
    private final double refillPerSecond;
    private final String keyHeader;

    public RateLimitFilter(
            RateLimitBackend backend,
            MeterRegistry meterRegistry,
            @Value("${ratelimiter.capacity}") int capacity,
            @Value("${ratelimiter.refillPerSecond}") double refillPerSecond,
            @Value("${ratelimiter.keyHeader}") String keyHeader
    ) {
            this.backend = backend;
            this.meterRegistry = meterRegistry;
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
            this.keyHeader = keyHeader;
    }

    private String resolveKey(HttpServletRequest request) {
        String key = request.getHeader(keyHeader);
        if (key == null || key.isBlank()) {
            key = request.getRemoteAddr(); // fallback
        }
        return key;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Do NOT rate-limit actuator & health endpoints
        String path = request.getRequestURI();
        return path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String key = resolveKey(request);
        boolean allowed = backend.allow(key, capacity, refillPerSecond);

        meterRegistry.counter("ratelimiter_requests_total",
                "outcome", allowed ? "allowed" : "blocked").increment();

        if (!allowed) {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        chain.doFilter(request, response);
    }
}
