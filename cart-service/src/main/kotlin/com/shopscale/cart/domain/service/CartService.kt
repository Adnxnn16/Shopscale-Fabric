package com.shopscale.cart.domain.service

import com.shopscale.cart.domain.entity.Cart
import com.shopscale.cart.presentation.dto.AddToCartRequest

interface CartService {
    fun addItem(userId: String, request: AddToCartRequest): Cart
    fun getCart(userId: String): Cart
    fun removeItem(userId: String, productId: String): Cart
    fun clearCart(userId: String)
}
