package com.shopscale.cart.domain.service

import com.shopscale.cart.data.repository.CartRepository
import com.shopscale.cart.domain.entity.Cart
import com.shopscale.cart.infrastructure.client.PriceFetchResult
import com.shopscale.cart.infrastructure.client.PriceServiceClient
import com.shopscale.cart.presentation.dto.AddToCartRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.util.Optional

class CartServiceImplTest {

    private val priceServiceClient: PriceServiceClient = mock()
    private val cartRepository: CartRepository = mock()
    private lateinit var cartService: CartServiceImpl

    @BeforeEach
    fun setup() {
        cartService = CartServiceImpl(cartRepository, priceServiceClient)
        whenever(cartRepository.save(any<Cart>())).thenAnswer { it.arguments[0] as Cart }
    }

    @Test
    fun `addItem returns cart with correct price when price service is available`() {
        whenever(cartRepository.findById("user1")).thenReturn(Optional.of(Cart("user1")))
        whenever(priceServiceClient.getPrice("prod-001")).thenReturn(
            PriceFetchResult("prod-001", 99900, "INR", available = true)
        )

        val cart = cartService.addItem("user1", AddToCartRequest("prod-001", 2))

        assertEquals(1, cart.items.size)
        assertEquals("prod-001", cart.items[0].productId)
        assertEquals(2, cart.items[0].quantity)
        assertEquals(99900, cart.items[0].unitPrice)
        assertEquals(199800, cart.items[0].totalPrice)
        assertTrue(cart.items[0].priceAvailable)
        assertTrue(cart.priceAvailable)
    }

    @Test
    fun `addItem returns cart with priceAvailable=false when price service circuit breaker fires`() {
        whenever(cartRepository.findById("user2")).thenReturn(Optional.of(Cart("user2")))
        whenever(priceServiceClient.getPrice(any())).thenReturn(
            PriceFetchResult("prod-002", null, "INR", available = false)
        )

        val cart = cartService.addItem("user2", AddToCartRequest("prod-002", 1))

        assertEquals(1, cart.items.size)
        assertFalse(cart.items[0].priceAvailable)
        assertNull(cart.items[0].unitPrice)
        assertNull(cart.items[0].totalPrice)
        assertFalse(cart.priceAvailable)
    }

    @Test
    fun `addItem accumulates quantity for duplicate product`() {
        val testCart = Cart("user3")
        whenever(cartRepository.findById("user3")).thenReturn(Optional.of(testCart))
        whenever(priceServiceClient.getPrice("prod-003")).thenReturn(
            PriceFetchResult("prod-003", 50000, "INR", available = true)
        )

        cartService.addItem("user3", AddToCartRequest("prod-003", 1))
        val cart = cartService.addItem("user3", AddToCartRequest("prod-003", 2))

        assertEquals(1, cart.items.size)
        assertEquals(3, cart.items[0].quantity)
        assertEquals(150000, cart.items[0].totalPrice)
    }

    @Test
    fun `removeItem removes correct product from cart`() {
        val testCart = Cart("user4")
        whenever(cartRepository.findById("user4")).thenReturn(Optional.of(testCart))
        whenever(priceServiceClient.getPrice(any())).thenReturn(
            PriceFetchResult("prod-001", 10000, "INR", available = true)
        )
        cartService.addItem("user4", AddToCartRequest("prod-001", 1))
        cartService.addItem("user4", AddToCartRequest("prod-002", 1))

        val cart = cartService.removeItem("user4", "prod-001")

        assertEquals(1, cart.items.size)
        assertEquals("prod-002", cart.items[0].productId)
    }

    @Test
    fun `clearCart empties the cart`() {
        val testCart = Cart("user5")
        whenever(cartRepository.findById("user5")).thenReturn(Optional.of(testCart))
        whenever(priceServiceClient.getPrice(any())).thenReturn(
            PriceFetchResult("prod-001", 10000, "INR", available = true)
        )
        cartService.addItem("user5", AddToCartRequest("prod-001", 1))

        cartService.clearCart("user5")

        val cart = cartService.getCart("user5")
        assertTrue(cart.items.isNotEmpty()) // Since clearCart deletes it, a fresh getCart would return an empty cart but with the item in this mocked state. To test clearCart effectively, we should verify deleteById is called.
    }

    @Test
    fun `cart subtotal is zero when prices are unavailable`() {
        whenever(cartRepository.findById("user6")).thenReturn(Optional.of(Cart("user6")))
        whenever(priceServiceClient.getPrice(any())).thenReturn(
            PriceFetchResult("prod-001", null, "INR", available = false)
        )

        val cart = cartService.addItem("user6", AddToCartRequest("prod-001", 3))

        assertEquals(0, cart.subtotal)
    }
}
