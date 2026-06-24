package com.shopscale.order.data.repository

import com.shopscale.order.data.model.OrderJpa
import com.shopscale.order.domain.entity.Order
import com.shopscale.order.domain.entity.OrderStatus
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.Instant
import java.util.Optional

class OrderRepositoryImplTest {

    @Test
    fun `save maps domain to jpa and returns domain`() = runBlocking {
        val jpaRepository = mock<OrderJpaRepository> {
            on { save(any<OrderJpa>()) } doReturn orderJpa(1L)
        }
        val repository = OrderRepositoryImpl(jpaRepository)

        val saved = repository.save(orderDomain(null))

        assertEquals(1L, saved.id)
        assertEquals(OrderStatus.PENDING, saved.status)
    }

    @Test
    fun `findById returns null for missing entity`() = runBlocking {
        val jpaRepository = mock<OrderJpaRepository> {
            on { findById(99L) } doReturn Optional.empty()
        }
        val repository = OrderRepositoryImpl(jpaRepository)

        val missing = repository.findById(99L)

        assertNull(missing)
    }

    @Test
    fun `findByUserId maps all entities`() = runBlocking {
        val jpaRepository = mock<OrderJpaRepository> {
            on { findByUserId("u-1") } doReturn listOf(orderJpa(1L), orderJpa(2L))
        }
        val repository = OrderRepositoryImpl(jpaRepository)

        val orders = repository.findByUserId("u-1")

        assertEquals(2, orders.size)
        assertEquals("u-1", orders.first().userId)
    }

    private fun orderJpa(id: Long) = OrderJpa(
        id = id,
        userId = "u-1",
        productId = "p-1",
        quantity = 1,
        status = OrderStatus.PENDING,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z")
    )

    private fun orderDomain(id: Long?) = Order(
        id = id,
        userId = "u-1",
        productId = "p-1",
        quantity = 1,
        status = OrderStatus.PENDING,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z")
    )
}
