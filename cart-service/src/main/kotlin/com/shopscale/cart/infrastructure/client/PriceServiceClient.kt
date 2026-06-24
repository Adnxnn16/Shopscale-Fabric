package com.shopscale.cart.infrastructure.client

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class PriceServiceClient(
    private val restTemplate: RestTemplate,
    @Value("\${services.price-service.url:http://price-service:8085}") private val priceServiceUrl: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "priceService", fallbackMethod = "getPriceFallback")
    fun getPrice(productId: String): PriceFetchResult {
        val response = restTemplate.getForObject(
            "$priceServiceUrl/prices/$productId",
            PriceApiResponse::class.java
        ) ?: throw IllegalStateException("Empty response from price-service for $productId")

        return PriceFetchResult(
            productId = productId,
            price = response.price,
            currency = response.currency,
            available = true
        )
    }

    @Suppress("UNUSED_PARAMETER") // fallbackMethod signature must match getPrice + Throwable
    fun getPriceFallback(productId: String, ex: Throwable): PriceFetchResult {
        log.warn(
            "Circuit breaker fallback for productId=$productId, " +
                "reason=${ex.javaClass.simpleName}: ${ex.message}"
        )
        return PriceFetchResult(
            productId = productId,
            price = null,
            currency = "INR",
            available = false
        )
    }

    /** Internal DTO mapping price-service JSON response. */
    data class PriceApiResponse(
        val productId: String = "",
        val price: Int = 0,
        val currency: String = "INR"
    )
}
