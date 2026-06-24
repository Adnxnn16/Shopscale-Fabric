package com.shopscale.inventory.data.model

import com.shopscale.inventory.domain.entity.Inventory
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "inventory")
data class InventoryJpa(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(name = "product_id", nullable = false, unique = true)
    val productId: String,
    @Column(nullable = false)
    val stock: Int,
    @Column(nullable = false)
    val reserved: Int,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant
)

fun InventoryJpa.toDomain(): Inventory = Inventory(id, productId, stock, reserved, updatedAt)
fun Inventory.toJpa(): InventoryJpa = InventoryJpa(id, productId, stock, reserved, updatedAt)
