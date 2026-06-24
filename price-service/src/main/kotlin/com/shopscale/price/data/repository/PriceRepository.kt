package com.shopscale.price.data.repository

import com.shopscale.price.domain.entity.ProductPrice
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PriceRepository : CrudRepository<ProductPrice, String>
