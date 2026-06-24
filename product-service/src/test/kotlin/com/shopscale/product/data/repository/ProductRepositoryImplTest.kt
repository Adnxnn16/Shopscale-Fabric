package com.shopscale.product.data.repository

import com.shopscale.product.data.model.ProductDocument
import com.shopscale.product.data.model.toDomain
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.Instant
import java.util.Optional

class ProductRepositoryImplTest {

    @Test
    fun `findAll maps mongo docs to domain`() = runBlocking {
        val mongoRepository = mock<ProductMongoRepository> {
            on { findAll() } doReturn listOf(doc("p-1"), doc("p-2"))
        }
        val repository = ProductRepositoryImpl(mongoRepository)

        val products = repository.findAll()

        assertEquals(2, products.size)
        assertEquals("p-1", products.first().id)
    }

    @Test
    fun `findById returns null when missing`() = runBlocking {
        val mongoRepository = mock<ProductMongoRepository> {
            on { findById("missing") } doReturn Optional.empty()
        }
        val repository = ProductRepositoryImpl(mongoRepository)

        val product = repository.findById("missing")

        assertNull(product)
    }

    @Test
    fun `save and delete delegate to mongo repository`() = runBlocking {
        val mongoRepository = mock<ProductMongoRepository> {
            on { save(any<ProductDocument>()) } doReturn doc("saved")
        }
        val repository = ProductRepositoryImpl(mongoRepository)

        val saved = repository.save(doc("draft").toDomain())
        repository.deleteById("saved")

        assertEquals("saved", saved.id)
        verify(mongoRepository).deleteById("saved")
    }

    @Test
    fun `findAll handles null createdAt and sorts descending`() = runBlocking {
        val mongoRepository = mock<ProductMongoRepository> {
            on { findAll() } doReturn listOf(
                doc("p-old", Instant.parse("2026-01-01T00:00:00Z")),
                doc("p-null", null),
                doc("p-new", Instant.parse("2026-12-31T00:00:00Z"))
            )
        }
        val repository = ProductRepositoryImpl(mongoRepository)

        val products = repository.findAll()

        assertEquals(3, products.size)
        // Descending order: new -> old -> null (which maps to EPOCH, the oldest)
        assertEquals("p-new", products[0].id)
        assertEquals("p-old", products[1].id)
        assertEquals("p-null", products[2].id)
    }

    private fun doc(id: String, createdAt: Instant? = Instant.parse("2026-01-01T00:00:00Z")) = ProductDocument(
        id = id,
        name = "Test",
        description = "Desc",
        price = 9990,
        category = "cat1",
        rating = 4.5,
        inventoryStatus = com.shopscale.product.domain.entity.InventoryStatus.IN_STOCK,
        imageUrl = "http://img.png",
        createdAt = createdAt
    )
}
