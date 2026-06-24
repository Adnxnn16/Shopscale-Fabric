package com.shopscale.inventory.domain.event

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

data class InventoryReservedEvent(
    val eventId: UUID,
    val orderId: Long,
    val productId: String,
    val quantity: Int,
    val reservedAt: Instant
)

data class InventoryReservationFailedEvent(
    val eventId: UUID,
    val orderId: Long,
    val productId: String,
    val reason: String,
    val failedAt: Instant
)
