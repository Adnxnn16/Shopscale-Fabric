package com.shopscale.cart.presentation.dto

data class AddToCartRequest(
    val productId: String,
    val quantity: Int
)

data class CartItemResponse(
    val productId: String,
    val quantity: Int,
    val unitPrice: Int?,
    val totalPrice: Int?,
    val priceAvailable: Boolean
)

data class CartResponse(
    val userId: String,
    val items: List<CartItemResponse>,
    val subtotal: Int,
    val priceAvailable: Boolean
)
