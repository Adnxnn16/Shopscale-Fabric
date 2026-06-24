package com.shopscale.inventory.domain.entity

import java.time.Instant

data class Inventory(
    val id: Long?,
    val productId: String,
    val stock: Int,
    val reserved: Int,
    val updatedAt: Instant
)
