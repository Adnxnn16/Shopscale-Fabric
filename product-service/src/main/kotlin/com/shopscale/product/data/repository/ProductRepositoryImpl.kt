package com.shopscale.product.data.repository

import com.shopscale.product.data.model.toDocument
import com.shopscale.product.data.model.toDomain
import com.shopscale.product.domain.entity.Product
import com.shopscale.product.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val productMongoRepository: ProductMongoRepository
) : ProductRepository {
    override suspend fun findAll(): List<Product> = withContext(Dispatchers.IO) {
        productMongoRepository.findAll()
            .map { it.toDomain() }
            .sortedByDescending { it.createdAt }
    }

    override suspend fun findById(id: String): Product? = withContext(Dispatchers.IO) {
        productMongoRepository.findById(id).orElse(null)?.toDomain()
    }

    override suspend fun save(product: Product): Product = withContext(Dispatchers.IO) {
        productMongoRepository.save(product.toDocument()).toDomain()
    }

    override suspend fun deleteById(id: String) = withContext(Dispatchers.IO) {
        productMongoRepository.deleteById(id)
    }
}
