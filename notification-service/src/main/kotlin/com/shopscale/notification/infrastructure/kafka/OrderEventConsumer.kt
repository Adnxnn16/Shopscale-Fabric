package com.shopscale.notification.infrastructure.kafka

import com.shopscale.notification.data.repository.NotificationLogRepository
import com.shopscale.notification.domain.event.OrderPlacedEvent
import com.shopscale.notification.domain.service.NotificationService
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class OrderEventConsumer(
    private val notificationService: NotificationService,
    private val notificationLogRepository: NotificationLogRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["\${kafka.topics.order-placed}"],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun handleOrderPlaced(event: OrderPlacedEvent, ack: Acknowledgment) {
        val correlationId = MDC.get("X-Correlation-Id") ?: "n/a"
        log.info(
            "Received OrderPlacedEvent: orderId=${event.orderId}, correlationId=$correlationId"
        )

        // Warn on duplicate orderId — not a full idempotency guard (email sending is idempotent)
        if (notificationLogRepository.existsByOrderId(event.orderId)) {
            log.warn(
                "Duplicate orderId detected: ${event.orderId} — notification may already have been sent"
            )
        }

        try {
            notificationService.sendOrderConfirmation(event)
            log.info("Email sent (mock) for orderId: ${event.orderId}")
        } catch (ex: Exception) {
            // Log error but still acknowledge — dead-letter handling is a P1 follow-up item
            log.error(
                "Failed to send notification for orderId=${event.orderId}: ${ex.message}", ex
            )
        } finally {
            // Manual offset commit — always ack to prevent consumer stall on poison messages
            ack.acknowledge()
        }
    }
}
