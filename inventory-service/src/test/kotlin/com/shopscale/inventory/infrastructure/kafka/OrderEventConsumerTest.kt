package com.shopscale.inventory.infrastructure.kafka

import com.shopscale.inventory.domain.event.OrderPlacedEvent
import com.shopscale.inventory.domain.service.InventoryService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.UUID

class OrderEventConsumerTest {
    @Test
    fun `consumer delegates to inventory service`() = runBlocking {
        val svc = mock<InventoryService>()
        val consumer = OrderEventConsumer(svc)
        val event = OrderPlacedEvent(UUID.randomUUID(), 1L, "u1", "p1", 1, Instant.now())
        
        consumer.onOrderPlaced(event)
        
        verify(svc).processOrder(event)
    }
}
