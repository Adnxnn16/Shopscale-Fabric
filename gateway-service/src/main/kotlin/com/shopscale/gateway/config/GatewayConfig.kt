package com.shopscale.gateway.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
class GatewayConfig {

    @Bean
    fun routes(builder: RouteLocatorBuilder, rateLimiter: RedisRateLimiter): RouteLocator {
        val keyResolver = ipKeyResolver()
        return builder.routes()
            .route("product-service") { r ->
                r.path("/api/products/**")
                    .filters { f ->
                        f.requestRateLimiter { c ->
                                c.setRateLimiter(rateLimiter)
                                c.setKeyResolver(keyResolver)
                            }
                    }
                    .uri("lb://product-service")
            }
            .route("order-service") { r ->
                r.path("/api/orders/**")
                    .filters { f ->
                        f.requestRateLimiter { c ->
                                c.setRateLimiter(rateLimiter)
                                c.setKeyResolver(keyResolver)
                            }
                    }
                    .uri("lb://order-service")
            }
            .route("cart-service") { r ->
                r.path("/api/cart/**")
                    .filters { f ->
                        f.requestRateLimiter { c ->
                                c.setRateLimiter(rateLimiter)
                                c.setKeyResolver(keyResolver)
                            }
                    }
                    .uri("lb://cart-service")
            }
            .route("inventory-service") { r ->
                r.path("/api/inventory/**")

                    .uri("lb://inventory-service")
            }
            .route("price-service") { r ->
                r.path("/api/prices/**")

                    .uri("lb://price-service")
            }
            .build()
    }

    @Bean
    fun ipKeyResolver(): KeyResolver =
        KeyResolver { exchange ->
            Mono.just(
                exchange.request.remoteAddress?.address?.hostAddress ?: "unknown"
            )
        }
}
