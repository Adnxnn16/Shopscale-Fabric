package com.shopscale.order.data.repository

import com.shopscale.order.data.model.toDomain
import com.shopscale.order.data.model.toJpa
import com.shopscale.order.domain.entity.Order
import com.shopscale.order.domain.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(private val orderJpaRepository: OrderJpaRepository) : OrderRepository {
    override fun save(order: Order): Order {
        return orderJpaRepository.save(order.toJpa()).toDomain()
    }

    override fun findById(id: Long): Order? {
        return orderJpaRepository.findById(id).orElse(null)?.toDomain()
    }

    override fun findByUserId(userId: String): List<Order> {
        return orderJpaRepository.findByUserId(userId).map { it.toDomain() }
    }
}
