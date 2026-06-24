package com.shopscale.price.domain.service

import com.shopscale.price.data.repository.PriceRepository
import com.shopscale.price.domain.entity.ProductPrice
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Optional

class PriceServiceImplTest {

    private val repository: PriceRepository = mock()
    private val service = PriceServiceImpl(repository, maxDelayMs = 0)

    @Test
    fun `getPrice returns correct price for known productId`() {
        whenever(repository.findById("prod-001")).thenReturn(
            Optional.of(ProductPrice("prod-001", 99900, "INR"))
        )

        val result = service.getPrice("prod-001")

        assertTrue(result.isPresent)
        assertEquals("prod-001", result.get().productId)
        assertEquals(99900, result.get().price)
        assertEquals("INR", result.get().currency)
    }

    @Test
    fun `getPrice returns empty for unknown productId`() {
        whenever(repository.findById("unknown-product-xyz")).thenReturn(Optional.empty())

        val result = service.getPrice("unknown-product-xyz")

        assertFalse(result.isPresent)
    }

    @Test
    fun `getAllPrices returns non-empty list`() {
        whenever(repository.findAll()).thenReturn(listOf(ProductPrice("prod-001", 99900, "INR")))

        val prices = service.getAllPrices()

        assertTrue(prices.isNotEmpty())
        assertTrue(prices.all { it.currency == "INR" })
    }
}
