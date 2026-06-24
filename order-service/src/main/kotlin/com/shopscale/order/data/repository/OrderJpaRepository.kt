package com.shopscale.order.data.repository

import com.shopscale.order.data.model.OrderJpa
import org.springframework.data.jpa.repository.JpaRepository

interface OrderJpaRepository : JpaRepository<OrderJpa, Long> {
    fun findByUserId(userId: String): List<OrderJpa>
}
