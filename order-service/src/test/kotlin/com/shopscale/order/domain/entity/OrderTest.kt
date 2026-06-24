package com.shopscale.order.domain.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class OrderTest {
    @Test
    fun shouldCreatePendingOrder() {
        val now = Instant.now()
        val order = Order(null, "u1", "p1", 2, OrderStatus.PENDING, now, now)
        assertEquals(OrderStatus.PENDING, order.status)
    }
}
