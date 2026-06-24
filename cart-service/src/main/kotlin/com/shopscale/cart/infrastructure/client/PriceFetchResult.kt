package com.shopscale.cart.infrastructure.client

/**
 * Transfer object used by PriceServiceClient to return price data,
 * including a fallback state when the circuit breaker is open.
 */
data class PriceFetchResult(
    val productId: String,
    val price: Int?,
    val currency: String,
    val available: Boolean
)
