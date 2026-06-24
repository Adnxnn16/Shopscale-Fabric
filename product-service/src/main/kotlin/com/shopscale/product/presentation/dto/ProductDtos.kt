package com.shopscale.product.presentation.dto

import com.shopscale.product.domain.entity.Product
import com.shopscale.product.domain.entity.InventoryStatus
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

data class ProductRequest(
    @field:NotBlank @field:Size(max = 120) val name: String,
    @field:NotBlank @field:Size(max = 1000) val description: String,
    @field:Min(0) val price: Int,
    @field:NotBlank @field:Size(max = 80) val category: String,
    @field:DecimalMin("4.2") @field:DecimalMax("5.0") val rating: Double,
    @field:NotNull val inventoryStatus: InventoryStatus,
    val imageUrl: String?
)

data class ProductResponse(
    val id: String?,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val rating: Double,
    val inventoryStatus: InventoryStatus,
    val imageUrl: String?,
    val createdAt: Instant
)

fun Product.toResponse(): ProductResponse =
    ProductResponse(id, name, description, price, category, rating, inventoryStatus, imageUrl, createdAt)
