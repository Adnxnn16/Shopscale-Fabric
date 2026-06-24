package com.shopscale.product.data.repository

import com.shopscale.product.data.model.ProductDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductMongoRepository : MongoRepository<ProductDocument, String>
