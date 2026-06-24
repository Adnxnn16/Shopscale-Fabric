package com.shopscale.cart.presentation.controller

import com.shopscale.cart.domain.entity.Cart
import com.shopscale.cart.domain.service.CartService
import com.shopscale.cart.presentation.dto.AddToCartRequest
import com.shopscale.cart.presentation.dto.CartItemResponse
import com.shopscale.cart.presentation.dto.CartResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Cart", description = "Cart management APIs")
@RestController
@RequestMapping("/api/cart")
class CartController(private val cartService: CartService) {

    @Operation(summary = "Add an item to the cart")
    @PostMapping("/{userId}/items")
    fun addItem(
        @PathVariable userId: String,
        @RequestBody request: AddToCartRequest
    ): CartResponse = cartService.addItem(userId, request).toResponse()

    @Operation(summary = "Get a user's cart")
    @GetMapping("/{userId}")
    fun getCart(@PathVariable userId: String): CartResponse =
        cartService.getCart(userId).toResponse()

    @Operation(summary = "Remove an item from the cart")
    @DeleteMapping("/{userId}/items/{productId}")
    fun removeItem(
        @PathVariable userId: String,
        @PathVariable productId: String
    ): CartResponse = cartService.removeItem(userId, productId).toResponse()

    @Operation(summary = "Clear a user's cart")
    @DeleteMapping("/{userId}")
    fun clearCart(@PathVariable userId: String): ResponseEntity<Void> {
        cartService.clearCart(userId)
        return ResponseEntity.noContent().build()
    }

    private fun Cart.toResponse(): CartResponse = CartResponse(
        userId = this.userId,
        items = this.items.map { item ->
            CartItemResponse(
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                totalPrice = item.totalPrice,
                priceAvailable = item.priceAvailable
            )
        },
        subtotal = this.subtotal,
        priceAvailable = this.priceAvailable
    )
}
