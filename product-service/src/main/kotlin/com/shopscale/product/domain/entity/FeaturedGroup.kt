package com.shopscale.product.domain.entity

data class FeaturedGroup(
    val id: String?,
    val name: String,
    val description: String,
    val productIds: List<String>
)
