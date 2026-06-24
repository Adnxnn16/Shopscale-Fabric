package com.shopscale.cart.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("Cart")
data class Cart(
    @Id val userId: String,
    val items: MutableList<CartItem> = mutableListOf()
) {
    val subtotal: Int
        get() = items
            .mapNotNull { it.totalPrice }
            .fold(0) { acc, price -> acc + price }

    val priceAvailable: Boolean
        get() = items.all { it.priceAvailable }
}
