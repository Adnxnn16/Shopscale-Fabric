package com.shopscale.price.presentation.controller

import com.shopscale.price.domain.service.PriceService
import com.shopscale.price.presentation.dto.PriceNotFoundResponse
import com.shopscale.price.presentation.dto.PriceResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/prices")
class PriceController(private val priceService: PriceService) {

    @GetMapping("/{productId}")
    fun getPrice(@PathVariable productId: String): ResponseEntity<Any> {
        val priceOpt = priceService.getPrice(productId)
        return if (priceOpt.isPresent) {
            val price = priceOpt.get()
            ResponseEntity.ok(
                PriceResponse(
                    productId = price.productId,
                    price = price.price,
                    currency = price.currency
                )
            )
        } else {
            ResponseEntity.status(404).body(
                PriceNotFoundResponse(
                    error = "PRICE_NOT_FOUND",
                    productId = productId
                )
            )
        }
    }

    @GetMapping
    fun getAllPrices(): List<PriceResponse> =
        priceService.getAllPrices().map { p ->
            PriceResponse(productId = p.productId, price = p.price, currency = p.currency)
        }
}
