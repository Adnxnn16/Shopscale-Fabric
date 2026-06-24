package com.shopscale.gateway.config

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RateLimiterConfig {

    /**
     * Token bucket rate limiter: 2 tokens/second replenish rate, 10 token burst.
     * This approximates ~100 requests/minute while allowing short bursts up to 10.
     * Per PRD: 100 req/min limit enforced at the gateway per IP.
     */
    @Bean
    fun redisRateLimiter(): RedisRateLimiter =
        RedisRateLimiter(
            2,   // replenishRate: 2 tokens added per second = 120/min theoretical max
            10,  // burstCapacity: allows short bursts up to 10 requests
            1    // requestedTokens: each request costs 1 token
        )
}
