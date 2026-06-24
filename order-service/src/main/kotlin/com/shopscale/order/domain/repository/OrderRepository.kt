package com.shopscale.order.domain.repository

import com.shopscale.order.domain.entity.Order

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: Long): Order?
    fun findByUserId(userId: String): List<Order>
}
