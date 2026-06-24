package com.shopscale.cart.data.repository

import com.shopscale.cart.domain.entity.Cart
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : CrudRepository<Cart, String>
