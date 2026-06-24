package com.shopscale.inventory.domain.repository

import com.shopscale.inventory.domain.entity.Inventory

interface InventoryRepository {
    fun findByProductId(productId: String): Inventory?
    fun save(inventory: Inventory): Inventory
}
