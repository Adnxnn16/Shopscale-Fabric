package com.shopscale.order.domain.service

import com.shopscale.order.domain.entity.Order
import com.shopscale.order.domain.entity.OrderStatus
import com.shopscale.order.domain.event.OrderPlacedEvent
import com.shopscale.order.domain.repository.OrderRepository
import com.shopscale.order.infrastructure.kafka.OrderEventProducer
import com.shopscale.order.presentation.dto.CreateOrderRequest
import com.shopscale.order.presentation.exception.OrderNotFoundException
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderEventProducer: OrderEventProducer
) {
    fun createOrder(request: CreateOrderRequest): Order {
        val now = Instant.now()
        val saved = orderRepository.save(
            Order(
                id = null,
                userId = request.userId,
                productId = request.productId,
                quantity = request.quantity,
                status = OrderStatus.PENDING,
                createdAt = now,
                updatedAt = now
            )
        )

        saved.id?.let {
            orderEventProducer.publish(
                OrderPlacedEvent(UUID.randomUUID(), it, saved.userId, saved.productId, saved.quantity, Instant.now())
            )
        }
        return saved
    }

    fun getOrder(id: Long): Order =
        orderRepository.findById(id) ?: throw OrderNotFoundException("Order not found for id=$id")

    fun getOrdersByUser(userId: String): List<Order> = orderRepository.findByUserId(userId)
}
