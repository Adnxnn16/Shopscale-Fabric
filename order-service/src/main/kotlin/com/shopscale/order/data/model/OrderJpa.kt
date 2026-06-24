package com.shopscale.order.data.model

import com.shopscale.order.domain.entity.Order
import com.shopscale.order.domain.entity.OrderStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "orders")
data class OrderJpa(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "user_id", nullable = false)
    val userId: String,
    @Column(name = "product_id", nullable = false)
    val productId: String,
    @Column(nullable = false)
    val quantity: Int,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: OrderStatus,
    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant
)

fun OrderJpa.toDomain(): Order = Order(id, userId, productId, quantity, status, createdAt, updatedAt)
fun Order.toJpa(): OrderJpa = OrderJpa(id, userId, productId, quantity, status, createdAt, updatedAt)
