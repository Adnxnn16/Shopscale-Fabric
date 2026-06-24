package com.shopscale.inventory.domain.service

import com.shopscale.inventory.domain.entity.Inventory
import com.shopscale.inventory.domain.event.OrderPlacedEvent
import com.shopscale.inventory.domain.repository.InventoryRepository
import com.shopscale.inventory.infrastructure.idempotency.IdempotencyManager
import com.shopscale.inventory.infrastructure.kafka.InventoryEventProducer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.UUID

class InventoryServiceBehaviorTest {

    @Test
    fun `processOrder reserves stock and publishes success`() = runBlocking {
        val event = sampleEvent(quantity = 2)
        val repository = mock<InventoryRepository> {
            onBlocking { findByProductId("p-1") } doReturn Inventory(1L, "p-1", 10, 1, Instant.now())
            onBlocking { save(any()) } doReturn Inventory(1L, "p-1", 8, 3, Instant.now())
        }
        val idempotency = mock<IdempotencyManager> {
            on { isProcessed(event.eventId.toString()) } doReturn false
        }
        val producer = mock<InventoryEventProducer>()
        val service = InventoryService(repository, idempotency, producer)

        service.processOrder(event)

        verify(repository).save(any())
        verify(producer).publishReserved(any())
        verify(idempotency).markProcessed(event.eventId.toString())
    }

    @Test
    fun `processOrder publishes failure for insufficient stock`() = runBlocking {
        val event = sampleEvent(quantity = 12)
        val repository = mock<InventoryRepository> {
            onBlocking { findByProductId("p-1") } doReturn Inventory(1L, "p-1", 5, 0, Instant.now())
        }
        val idempotency = mock<IdempotencyManager> {
            on { isProcessed(event.eventId.toString()) } doReturn false
        }
        val producer = mock<InventoryEventProducer>()
        val service = InventoryService(repository, idempotency, producer)

        service.processOrder(event)

        verify(repository, never()).save(any())
        verify(producer).publishFailed(any())
        verify(idempotency).markProcessed(event.eventId.toString())
    }

    @Test
    fun `processOrder is idempotent and skips duplicate events`() = runBlocking {
        val event = sampleEvent(quantity = 1)
        val repository = mock<InventoryRepository>()
        val idempotency = mock<IdempotencyManager> {
            on { isProcessed(event.eventId.toString()) } doReturn true
        }
        val producer = mock<InventoryEventProducer>()
        val service = InventoryService(repository, idempotency, producer)

        service.processOrder(event)

        verify(repository, never()).findByProductId(any())
        verify(producer, never()).publishReserved(any())
        verify(producer, never()).publishFailed(any())
        verify(idempotency, never()).markProcessed(any())
    }

    private fun sampleEvent(quantity: Int) = OrderPlacedEvent(
        eventId = UUID.randomUUID(),
        orderId = 100L,
        userId = "u-1",
        productId = "p-1",
        quantity = quantity,
        timestamp = Instant.now()
    )
}
