package com.shopscale.product.domain.service

import com.shopscale.product.domain.entity.Product
import com.shopscale.product.domain.repository.ProductRepository
import com.shopscale.product.presentation.dto.ProductRequest
import com.shopscale.product.presentation.exception.ProductNotFoundException
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val featuredGroupRepository: com.shopscale.product.data.repository.FeaturedGroupMongoRepository
) {
    suspend fun getAll(category: String? = null): List<Product> {
        val all = productRepository.findAll()
        return if (category.isNullOrBlank()) all else all.filter { it.category == category }
    }

    suspend fun getFeatured(): List<Product> {
        val groupIds = listOf("perf-collection", "creator-essentials", "digital-builder-pack")
        val groups = featuredGroupRepository.findAllById(groupIds)
        val productIds = groups.flatMap { it.productIds }.distinct()
        // Wait, productRepository.findAllById doesn't exist on our custom ProductRepository right now.
        // Let's implement it or just filter getAll(). For simplicity let's filter getAll() for now
        // if findAllById is not present. I'll just check if findAllById is in ProductRepository.
        // Let's just fetch all and filter.
        val allProducts = productRepository.findAll()
        return allProducts.filter { it.id in productIds }
    }

    suspend fun getById(id: String): Product =
        productRepository.findById(id) ?: throw ProductNotFoundException("Product not found for id=$id")

    suspend fun create(request: ProductRequest): Product =
        productRepository.save(
            Product(
                id = null,
                name = request.name,
                description = request.description,
                price = request.price,
                category = request.category,
                rating = request.rating,
                inventoryStatus = request.inventoryStatus,
                imageUrl = request.imageUrl,
                createdAt = Instant.now()
            )
        )

    suspend fun deleteById(id: String) {
        getById(id)
        productRepository.deleteById(id)
    }
}
