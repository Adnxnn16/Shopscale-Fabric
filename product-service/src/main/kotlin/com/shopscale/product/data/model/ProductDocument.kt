package com.shopscale.product.data.model

import com.shopscale.product.domain.entity.Product
import com.shopscale.product.domain.entity.InventoryStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "products")
data class ProductDocument(
    @Id
    val id: String? = null,
    val name: String,
    val description: String,
    val price: Int,
    val category: String,
    val rating: Double,
    val inventoryStatus: InventoryStatus,
    val imageUrl: String?,
    val createdAt: Instant? = null
)

fun ProductDocument.toDomain(): Product = Product(id, name, description, price, category, rating, inventoryStatus, imageUrl, createdAt ?: Instant.EPOCH)
fun Product.toDocument(): ProductDocument = ProductDocument(id, name, description, price, category, rating, inventoryStatus, imageUrl, createdAt)
