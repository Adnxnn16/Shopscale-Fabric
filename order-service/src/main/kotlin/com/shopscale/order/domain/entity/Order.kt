package com.shopscale.order.domain.entity

import java.time.Instant

enum class OrderStatus { PENDING, CONFIRMED, FAILED, CANCELLED }

data class Order(
    val id: Long?,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val status: OrderStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)
