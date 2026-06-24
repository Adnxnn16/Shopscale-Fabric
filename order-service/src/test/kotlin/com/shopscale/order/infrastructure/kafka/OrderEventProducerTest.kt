package com.shopscale.order.infrastructure.kafka

import com.shopscale.order.domain.event.OrderPlacedEvent
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.kafka.core.KafkaTemplate
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture

class OrderEventProducerTest {
    @Test
    fun `publish sends to kafka template`() {
        val template = mock<KafkaTemplate<String, OrderPlacedEvent>> {
            on { send(any(), any(), any()) } doReturn CompletableFuture.completedFuture(null)
        }
        val producer = OrderEventProducer(template)
        
        producer.publish(OrderPlacedEvent(UUID.randomUUID(), 1L, "u1", "p1", 1, Instant.now()))
        verify(template).send(eq("orders"), eq("1"), any())
    }
}
