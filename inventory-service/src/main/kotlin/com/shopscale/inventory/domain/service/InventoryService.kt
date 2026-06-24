package com.shopscale.inventory.domain.service

import com.shopscale.inventory.domain.entity.Inventory
import com.shopscale.inventory.domain.event.InventoryReservationFailedEvent
import com.shopscale.inventory.domain.event.InventoryReservedEvent
import com.shopscale.inventory.domain.event.OrderPlacedEvent
import com.shopscale.inventory.domain.repository.InventoryRepository
import com.shopscale.inventory.infrastructure.idempotency.IdempotencyManager
import com.shopscale.inventory.infrastructure.kafka.InventoryEventProducer
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class InventoryService(
    private val inventoryRepository: InventoryRepository,
    private val idempotencyManager: IdempotencyManager,
    private val eventProducer: InventoryEventProducer
) {
    fun getByProductId(productId: String): Inventory? = inventoryRepository.findByProductId(productId)

    fun processOrder(event: OrderPlacedEvent) {
        if (idempotencyManager.isProcessed(event.eventId.toString())) {
            return
        }

        val current = inventoryRepository.findByProductId(event.productId)
            ?: Inventory(null, event.productId, 0, 0, Instant.now())

        if (current.stock >= event.quantity) {
            val updated = current.copy(
                stock = current.stock - event.quantity,
                reserved = current.reserved + event.quantity,
                updatedAt = Instant.now()
            )
            inventoryRepository.save(updated)
            eventProducer.publishReserved(
                InventoryReservedEvent(event.eventId, event.orderId, event.productId, event.quantity, Instant.now())
            )
        } else {
            eventProducer.publishFailed(
                InventoryReservationFailedEvent(event.eventId, event.orderId, event.productId, "Insufficient stock", Instant.now())
            )
        }

        idempotencyManager.markProcessed(event.eventId.toString())
    }
}
