package com.shopscale.notification.domain.service

import com.shopscale.notification.data.repository.NotificationLogRepository
import com.shopscale.notification.domain.entity.NotificationStatus
import com.shopscale.notification.domain.event.OrderPlacedEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.Instant
import java.util.UUID

class NotificationServiceImplTest {

    private val repository: NotificationLogRepository = mock()
    private lateinit var service: NotificationServiceImpl

    @BeforeEach
    fun setup() {
        service = NotificationServiceImpl(repository)
    }

    @Test
    fun `sendOrderConfirmation stores notification log with SENT status`() {
        val event = buildEvent(orderId = 101L)

        service.sendOrderConfirmation(event)

        val logs = repository.findAll()
        assertEquals(1, logs.size)
        assertEquals(101L, logs[0].orderId)
        assertEquals(NotificationStatus.SENT, logs[0].status)
    }

    @Test
    fun `getSentCount returns correct count after multiple notifications`() {
        whenever(repository.count()).thenReturn(3L)
        
        assertEquals(3, service.getSentCount())
    }

    @Test
    fun `sendOrderConfirmation uses userId to construct email address`() {
        val event = buildEvent(orderId = 99L, userId = "user42")

        service.sendOrderConfirmation(event)

        val log = repository.findAll().first()
        assertTrue(log.email.contains("user42"), "Email should contain userId")
    }

    @Test
    fun `repository existsByOrderId returns true for already-processed order`() {
        service.sendOrderConfirmation(buildEvent(orderId = 55L))

        assertTrue(repository.existsByOrderId(55L))
    }

    private fun buildEvent(orderId: Long, userId: String = "user1") = OrderPlacedEvent(
        eventId = UUID.randomUUID(),
        orderId = orderId,
        userId = userId,
        productId = "prod-001",
        quantity = 2,
        timestamp = Instant.now()
    )
}
