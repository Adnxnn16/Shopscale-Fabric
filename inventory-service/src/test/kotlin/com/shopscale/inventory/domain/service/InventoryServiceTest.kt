package com.shopscale.inventory.domain.service

import com.shopscale.inventory.domain.entity.Inventory
import com.shopscale.inventory.domain.event.InventoryReservationFailedEvent
import com.shopscale.inventory.domain.event.InventoryReservedEvent
import com.shopscale.inventory.domain.event.OrderPlacedEvent
import com.shopscale.inventory.domain.repository.InventoryRepository
import com.shopscale.inventory.infrastructure.idempotency.IdempotencyManager
import com.shopscale.inventory.infrastructure.kafka.InventoryEventProducer
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Instant
import java.util.UUID

class InventoryServiceTest {
    @Test
    fun inventoryMathShouldBeDeterministic() {
        val current = Inventory(1L, "p1", 5, 1, Instant.now())
        val updated = current.copy(stock = current.stock - 2, reserved = current.reserved + 2)
        assertEquals(3, updated.stock)
        assertEquals(3, updated.reserved)
    }

    @Test
    fun `processOrder skips if already processed`() = runBlocking {
        val repo = mock<InventoryRepository>()
        val idem = mock<IdempotencyManager> {
            on { isProcessed(any()) } doReturn true
        }
        val prod = mock<InventoryEventProducer>()
        val svc = InventoryService(repo, idem, prod)

        svc.processOrder(OrderPlacedEvent(UUID.randomUUID(), 1L, "u1", "p1", 2, Instant.now()))

        verify(repo, never()).findByProductId(any())
        verify(idem, never()).markProcessed(any())
    }

    @Test
    fun `processOrder saves and publishes reserved when stock is sufficient`() = runBlocking {
        val repo = mock<InventoryRepository> {
            onBlocking { findByProductId("p1") } doReturn Inventory(1L, "p1", 10, 0, Instant.now())
        }
        val idem = mock<IdempotencyManager> {
            on { isProcessed(any()) } doReturn false
        }
        val prod = mock<InventoryEventProducer>()
        val svc = InventoryService(repo, idem, prod)

        val eventId = UUID.randomUUID()
        svc.processOrder(OrderPlacedEvent(eventId, 1L, "u1", "p1", 2, Instant.now()))

        verify(repo).save(argThat { stock == 8 && reserved == 2 })
        verify(prod).publishReserved(any())
        verify(idem).markProcessed(eventId.toString())
    }

    @Test
    fun `processOrder publishes failed when stock is insufficient`() = runBlocking {
        val repo = mock<InventoryRepository> {
            onBlocking { findByProductId("p1") } doReturn Inventory(1L, "p1", 1, 0, Instant.now())
        }
        val idem = mock<IdempotencyManager> {
            on { isProcessed(any()) } doReturn false
        }
        val prod = mock<InventoryEventProducer>()
        val svc = InventoryService(repo, idem, prod)

        val eventId = UUID.randomUUID()
        svc.processOrder(OrderPlacedEvent(eventId, 1L, "u1", "p1", 2, Instant.now()))

        verify(repo, never()).save(any())
        verify(prod).publishFailed(any())
        verify(idem).markProcessed(eventId.toString())
    }

    @Test
    fun `processOrder creates default inventory if product not found`() = runBlocking {
        val repo = mock<InventoryRepository> {
            onBlocking { findByProductId("p_missing") } doReturn null
        }
        val idem = mock<IdempotencyManager> {
            on { isProcessed(any()) } doReturn false
        }
        val prod = mock<InventoryEventProducer>()
        val svc = InventoryService(repo, idem, prod)

        svc.processOrder(OrderPlacedEvent(UUID.randomUUID(), 1L, "u1", "p_missing", 2, Instant.now()))

        verify(prod).publishFailed(any())
    }
}
