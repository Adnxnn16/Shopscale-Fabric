package com.shopscale.product.data.model

import com.shopscale.product.domain.entity.FeaturedGroup
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "featured_groups")
data class FeaturedGroupDocument(
    @Id
    val id: String? = null,
    val name: String,
    val description: String,
    val productIds: List<String>
)

fun FeaturedGroupDocument.toDomain(): FeaturedGroup = FeaturedGroup(id, name, description, productIds)
fun FeaturedGroup.toDocument(): FeaturedGroupDocument = FeaturedGroupDocument(id, name, description, productIds)
