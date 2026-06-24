package com.shopscale.cart.domain.entity

data class CartItem(
    val productId: String,
    var quantity: Int,
    val unitPrice: Int?,
    val totalPrice: Int?,
    val priceAvailable: Boolean
)
