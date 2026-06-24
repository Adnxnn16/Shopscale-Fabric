package com.shopscale.product.domain.repository

import com.shopscale.product.domain.entity.Product

interface ProductRepository {
    suspend fun findAll(): List<Product>
    suspend fun findById(id: String): Product?
    suspend fun save(product: Product): Product
    suspend fun deleteById(id: String)
}
