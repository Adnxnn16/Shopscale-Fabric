package com.shopscale.price.domain.entity

/**
 * Pure domain entity representing the price of a product.
 * No framework annotations — this is a domain object.
 */
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "product_prices")
data class ProductPrice(
    @Id
    val productId: String = "",
    val price: Int = 0,
    val currency: String = "INR"
)
