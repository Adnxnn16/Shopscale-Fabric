package com.shopscale.notification.domain.service

import com.shopscale.notification.data.repository.NotificationLogRepository
import com.shopscale.notification.domain.entity.EmailNotification
import com.shopscale.notification.domain.entity.NotificationStatus
import com.shopscale.notification.domain.event.OrderPlacedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

import java.util.concurrent.CompletableFuture

@Service
class NotificationServiceImpl(
    private val notificationLogRepository: NotificationLogRepository
) : NotificationService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun sendOrderConfirmation(event: OrderPlacedEvent) {
        // Mock email send — in a real system this would call an email provider
        log.info("Email sent (mock) for orderId: ${event.orderId}, userId: ${event.userId}")

        val notification = EmailNotification(
            id = UUID.randomUUID().toString(),
            orderId = event.orderId,
            email = "user-${event.userId}@shopscale.local",
            status = NotificationStatus.SENT,
            sentAt = Instant.now()
        )
        
        // Non-blocking write to MongoDB
        CompletableFuture.runAsync {
            try {
                notificationLogRepository.save(notification)
            } catch (e: Exception) {
                log.error("Failed to write notification to DB", e)
            }
        }
    }

    override fun getSentCount(): Int = notificationLogRepository.count().toInt()
}
