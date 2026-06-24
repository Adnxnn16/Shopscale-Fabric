package com.shopscale.notification.domain.event

import java.time.Instant
import java.util.UUID

/**
 * Local replica of the OrderPlacedEvent schema produced by order-service.
 * This data class must remain in sync with the canonical schema in order-service.
 * Do NOT modify the field names or types without coordinating with order-service.
 */
data class OrderPlacedEvent(
    val eventId: UUID,
    val orderId: Long,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val timestamp: Instant
)
