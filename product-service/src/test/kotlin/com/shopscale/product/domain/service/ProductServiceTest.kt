package com.shopscale.product.domain.service

import com.shopscale.product.domain.entity.Product
import com.shopscale.product.domain.repository.ProductRepository
import com.shopscale.product.presentation.dto.ProductRequest
import com.shopscale.product.presentation.exception.ProductNotFoundException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import java.math.BigDecimal
import java.time.Instant

class ProductServiceTest {

    @Test
    fun `getById returns product when found`() = runBlocking {
        val product = sampleProduct(id = "p-1")
        val repository = mock<ProductRepository> {
            onBlocking { findById("p-1") } doReturn product
        }
        val featuredRepo = mock<com.shopscale.product.data.repository.FeaturedGroupMongoRepository>()
        val service = ProductService(repository, featuredRepo)

        val actual = service.getById("p-1")

        assertEquals("p-1", actual.id)
        assertEquals("Shirt", actual.name)
    }

    @Test
    fun `getById throws when product does not exist`() = runBlocking {
        val repository = mock<ProductRepository> {
            onBlocking { findById("missing") } doReturn null
        }
        val featuredRepo = mock<com.shopscale.product.data.repository.FeaturedGroupMongoRepository>()
        val service = ProductService(repository, featuredRepo)

        assertThrows(ProductNotFoundException::class.java) {
            runBlocking { service.getById("missing") }
        }
    }

    @Test
    fun `create maps request and persists product`() = runBlocking {
        val repository = mock<ProductRepository> {
            onBlocking { save(any()) } doReturn sampleProduct(id = "p-2")
        }
        val featuredRepo = mock<com.shopscale.product.data.repository.FeaturedGroupMongoRepository>()
        val service = ProductService(repository, featuredRepo)

        val created = service.create(
            ProductRequest(
                name = "Shirt",
                description = "Cotton shirt",
                price = 9990,
                category = "fashion",
                rating = 4.5,
                inventoryStatus = com.shopscale.product.domain.entity.InventoryStatus.IN_STOCK,
                imageUrl = "https://img.example/p2.png"
            )
        )

        assertEquals("p-2", created.id)
        assertNotNull(created.createdAt)
    }

    @Test
    fun `deleteById deletes only when product exists`() = runBlocking {
        val repository = mock<ProductRepository> {
            onBlocking { findById("p-1") } doReturn sampleProduct(id = "p-1")
        }
        val featuredRepo = mock<com.shopscale.product.data.repository.FeaturedGroupMongoRepository>()
        val service = ProductService(repository, featuredRepo)

        service.deleteById("p-1")

        verify(repository).deleteById("p-1")
    }

    @Test
    fun `deleteById does not delete missing product`() = runBlocking {
        val repository = mock<ProductRepository> {
            onBlocking { findById("missing") } doReturn null
        }
        val featuredRepo = mock<com.shopscale.product.data.repository.FeaturedGroupMongoRepository>()
        val service = ProductService(repository, featuredRepo)

        assertThrows(ProductNotFoundException::class.java) {
            runBlocking { service.deleteById("missing") }
        }
        verify(repository, never()).deleteById(any())
    }

    private fun sampleProduct(id: String?) = Product(
        id = id,
        name = "Shirt",
        description = "Cotton shirt",
        price = 9990,
        category = "fashion",
        rating = 4.5,
        inventoryStatus = com.shopscale.product.domain.entity.InventoryStatus.IN_STOCK,
        imageUrl = "https://img.example/shirt.png",
        createdAt = Instant.parse("2026-01-01T00:00:00Z")
    )
}
