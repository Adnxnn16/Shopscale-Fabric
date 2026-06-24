package com.shopscale.order.presentation.dto

import com.shopscale.order.domain.entity.Order
import com.shopscale.order.domain.entity.OrderStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.Instant

data class CreateOrderRequest(
    @field:NotBlank val userId: String,
    @field:NotBlank val productId: String,
    @field:Min(1) val quantity: Int
)

data class OrderResponse(
    val id: Long?,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val status: OrderStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)

fun Order.toResponse(): OrderResponse =
    OrderResponse(id, userId, productId, quantity, status, createdAt, updatedAt)
