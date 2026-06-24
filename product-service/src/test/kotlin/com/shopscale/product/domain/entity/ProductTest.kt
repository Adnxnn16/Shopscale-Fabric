package com.shopscale.product.domain.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class ProductTest {
    @Test
    fun shouldBuildImmutableProduct() {
        val product = Product(
            id = "p-1",
            name = "Shirt",
            description = "Cotton shirt",
            price = 9999,
            category = "fashion",
            rating = 4.5,
            inventoryStatus = InventoryStatus.IN_STOCK,
            imageUrl = null,
            createdAt = Instant.parse("2026-01-01T00:00:00Z")
        )
        assertEquals("Shirt", product.name)
        assertEquals(9999, product.price)
    }
}
