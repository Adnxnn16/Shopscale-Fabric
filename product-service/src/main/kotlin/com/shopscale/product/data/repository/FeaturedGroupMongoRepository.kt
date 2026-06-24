package com.shopscale.product.data.repository

import com.shopscale.product.data.model.FeaturedGroupDocument
import org.springframework.data.mongodb.repository.MongoRepository

interface FeaturedGroupMongoRepository : MongoRepository<FeaturedGroupDocument, String>
