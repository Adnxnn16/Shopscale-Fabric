package com.shopscale.order.domain.service

import com.shopscale.order.domain.entity.Order
import com.shopscale.order.domain.entity.OrderStatus
import com.shopscale.order.domain.repository.OrderRepository
import com.shopscale.order.infrastructure.kafka.OrderEventProducer
import com.shopscale.order.presentation.dto.CreateOrderRequest
import com.shopscale.order.presentation.exception.OrderNotFoundException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.time.Instant

class OrderServiceTest {

    @Test
    fun `createOrder saves order and publishes event when id exists`() = runBlocking {
        val repository = mock<OrderRepository> {
            onBlocking { save(any()) } doReturn sampleOrder(id = 10L)
        }
        val producer = mock<OrderEventProducer>()
        val service = OrderService(repository, producer)

        val created = service.createOrder(CreateOrderRequest("u-1", "p-1", 2))

        assertEquals(10L, created.id)
        assertEquals(OrderStatus.PENDING, created.status)
        verify(producer).publish(any())
    }

    @Test
    fun `createOrder does not publish event when id is null`() = runBlocking {
        val repository = mock<OrderRepository> {
            onBlocking { save(any()) } doReturn sampleOrder(id = null)
        }
        val producer = mock<OrderEventProducer>()
        val service = OrderService(repository, producer)

        val created = service.createOrder(CreateOrderRequest("u-1", "p-1", 2))

        assertEquals(null, created.id)
        verify(producer, never()).publish(any())
    }

    @Test
    fun `getOrder throws when id missing`() {
        val repository = mock<OrderRepository> {
            onBlocking { findById(999L) } doReturn null
        }
        val producer = mock<OrderEventProducer>()
        val service = OrderService(repository, producer)

        assertThrows(OrderNotFoundException::class.java) {
            runBlocking { service.getOrder(999L) }
        }
    }

    @Test
    fun `getOrdersByUser delegates to repository`() = runBlocking {
        val repository = mock<OrderRepository> {
            onBlocking { findByUserId("u-1") } doReturn listOf(sampleOrder(id = 1L), sampleOrder(id = 2L))
        }
        val producer = mock<OrderEventProducer>()
        val service = OrderService(repository, producer)

        val orders = service.getOrdersByUser("u-1")

        assertEquals(2, orders.size)
    }

    @Test
    fun `getOrdersByUser returns empty list when user has no orders`() = runBlocking {
        val repository = mock<OrderRepository> {
            onBlocking { findByUserId("new-user") } doReturn emptyList()
        }
        val producer = mock<OrderEventProducer>()
        val service = OrderService(repository, producer)

        val result = service.getOrdersByUser("new-user")

        assertEquals(0, result.size)
    }

    private fun sampleOrder(id: Long?) = Order(
        id = id,
        userId = "u-1",
        productId = "p-1",
        quantity = 2,
        status = OrderStatus.PENDING,
        createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-01-01T00:00:00Z")
    )
}
