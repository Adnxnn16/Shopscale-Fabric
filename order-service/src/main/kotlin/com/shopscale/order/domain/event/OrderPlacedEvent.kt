package com.shopscale.order.domain.event

import java.time.Instant
import java.util.UUID

data class OrderPlacedEvent(
    val eventId: UUID,
    val orderId: Long,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val timestamp: Instant
)
