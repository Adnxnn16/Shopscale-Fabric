package com.shopscale.inventory.data.repository

import com.shopscale.inventory.data.model.InventoryJpa
import org.springframework.data.jpa.repository.JpaRepository

interface InventoryJpaRepository : JpaRepository<InventoryJpa, Long> {
    fun findByProductId(productId: String): InventoryJpa?
}
