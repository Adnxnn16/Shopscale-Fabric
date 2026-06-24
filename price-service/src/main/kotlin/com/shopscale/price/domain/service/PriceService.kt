package com.shopscale.price.domain.service

import com.shopscale.price.domain.entity.ProductPrice
import java.util.Optional

interface PriceService {
    fun getPrice(productId: String): Optional<ProductPrice>
    fun getAllPrices(): List<ProductPrice>
}
