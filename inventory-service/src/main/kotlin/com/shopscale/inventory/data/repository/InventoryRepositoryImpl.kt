package com.shopscale.inventory.data.repository

import com.shopscale.inventory.data.model.toDomain
import com.shopscale.inventory.data.model.toJpa
import com.shopscale.inventory.domain.entity.Inventory
import com.shopscale.inventory.domain.repository.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository

@Repository
class InventoryRepositoryImpl(private val jpa: InventoryJpaRepository) : InventoryRepository {
    override fun findByProductId(productId: String): Inventory? {
        return jpa.findByProductId(productId)?.toDomain()
    }

    override fun save(inventory: Inventory): Inventory {
        return jpa.save(inventory.toJpa()).toDomain()
    }
}
