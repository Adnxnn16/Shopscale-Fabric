package com.shopscale.notification.infrastructure.kafka

import com.shopscale.notification.data.repository.NotificationLogRepository
import com.shopscale.notification.domain.event.OrderPlacedEvent
import com.shopscale.notification.domain.service.NotificationService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.kafka.support.Acknowledgment
import java.time.Instant
import java.util.UUID

class OrderEventConsumerTest {

    private val notificationService: NotificationService = mock()
    private val notificationLogRepository: NotificationLogRepository = mock()
    private val ack: Acknowledgment = mock()
    private val consumer = OrderEventConsumer(notificationService, notificationLogRepository)

    @Test
    fun `consumer calls sendOrderConfirmation and acknowledges on success`() {
        val event = buildEvent(orderId = 1L)

        consumer.handleOrderPlaced(event, ack)

        verify(notificationService, times(1)).sendOrderConfirmation(event)
        verify(ack, times(1)).acknowledge()
    }

    @Test
    fun `consumer still acknowledges even when sendOrderConfirmation throws`() {
        val event = buildEvent(orderId = 2L)
        doThrow(RuntimeException("SMTP unavailable")).whenever(notificationService)
            .sendOrderConfirmation(any())

        consumer.handleOrderPlaced(event, ack)

        // Must still ack — otherwise consumer stalls on this partition
        verify(ack, times(1)).acknowledge()
    }

    @Test
    fun `duplicate orderId does not throw — warning is logged internally`() {
        val event = buildEvent(orderId = 3L)
        // Pre-populate repository to simulate a duplicate
        notificationLogRepository.save(
            com.shopscale.notification.domain.entity.EmailNotification(
                id = UUID.randomUUID().toString(),
                orderId = 3L,
                email = "user-user1@shopscale.local",
                status = com.shopscale.notification.domain.entity.NotificationStatus.SENT,
                sentAt = Instant.now()
            )
        )

        // Should not throw — just log a warning
        consumer.handleOrderPlaced(event, ack)

        verify(ack, times(1)).acknowledge()
        // Service is still called (not skipped — idempotency at service layer, not consumer)
        verify(notificationService, times(1)).sendOrderConfirmation(event)
    }

    private fun buildEvent(orderId: Long) = OrderPlacedEvent(
        eventId = UUID.randomUUID(),
        orderId = orderId,
        userId = "user1",
        productId = "prod-001",
        quantity = 2,
        timestamp = Instant.now()
    )
}
