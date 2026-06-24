package com.shopscale.cart.domain.service

import com.shopscale.cart.data.repository.CartRepository
import com.shopscale.cart.domain.entity.Cart
import com.shopscale.cart.domain.entity.CartItem
import com.shopscale.cart.infrastructure.client.PriceServiceClient
import com.shopscale.cart.presentation.dto.AddToCartRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CartServiceImpl(
    private val cartRepository: CartRepository,
    private val priceServiceClient: PriceServiceClient
) : CartService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun addItem(userId: String, request: AddToCartRequest): Cart {
        val cart = cartRepository.findById(userId).orElse(Cart(userId))

        val fetchedPrice = priceServiceClient.getPrice(request.productId)
        log.info(
            "Adding item to cart: userId=$userId, productId=${request.productId}, " +
                "priceAvailable=${fetchedPrice.available}"
        )

        val unitPrice = fetchedPrice.price
        val totalPrice = unitPrice?.times(request.quantity)

        // Update existing item quantity or add new item
        val existingIndex = cart.items.indexOfFirst { it.productId == request.productId }
        if (existingIndex >= 0) {
            val existing = cart.items[existingIndex]
            val newQty = existing.quantity + request.quantity
            val newTotal = unitPrice?.times(newQty)
            cart.items[existingIndex] = CartItem(
                productId = request.productId,
                quantity = newQty,
                unitPrice = unitPrice,
                totalPrice = newTotal,
                priceAvailable = fetchedPrice.available
            )
        } else {
            cart.items.add(
                CartItem(
                    productId = request.productId,
                    quantity = request.quantity,
                    unitPrice = unitPrice,
                    totalPrice = totalPrice,
                    priceAvailable = fetchedPrice.available
                )
            )
        }

        return cartRepository.save(cart)
    }

    override fun getCart(userId: String): Cart = cartRepository.findById(userId).orElse(Cart(userId))

    override fun removeItem(userId: String, productId: String): Cart {
        val cart = cartRepository.findById(userId).orElse(Cart(userId))
        cart.items.removeIf { it.productId == productId }
        return cartRepository.save(cart)
    }

    override fun clearCart(userId: String) {
        cartRepository.deleteById(userId)
    }
}
