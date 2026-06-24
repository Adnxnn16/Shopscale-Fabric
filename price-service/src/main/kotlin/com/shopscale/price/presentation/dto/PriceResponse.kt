package com.shopscale.price.presentation.dto

data class PriceResponse(
    val productId: String,
    val price: Int, // Represents integer paise
    val currency: String
)

data class PriceNotFoundResponse(
    val error: String,
    val productId: String
)
