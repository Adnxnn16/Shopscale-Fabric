package com.shopscale.product.domain.entity

import java.time.Instant

enum class InventoryStatus {
    IN_STOCK, LIMITED_STOCK, BEST_SELLER, NEW_ARRIVAL
}

data class Product(
    val id: String?,
    val name: String,
    val description: String,
    val price: Int, // Stored as integer paise
    val category: String,
    val rating: Double,
    val inventoryStatus: InventoryStatus,
    val imageUrl: String?,
    val createdAt: Instant
)
