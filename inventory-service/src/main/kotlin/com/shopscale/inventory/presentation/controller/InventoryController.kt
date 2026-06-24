package com.shopscale.inventory.presentation.controller

import com.shopscale.inventory.domain.service.InventoryService
import kotlinx.coroutines.runBlocking
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Inventory", description = "Inventory management APIs")
@RestController
@RequestMapping("/api/inventory")
class InventoryController(private val inventoryService: InventoryService) {

    @Operation(summary = "Get inventory for a product")
    @GetMapping("/{productId}")
    fun getInventory(@PathVariable productId: String): ResponseEntity<Map<String, Any>> {
        val inv = inventoryService.getByProductId(productId)
        if (inv == null) {
            return ResponseEntity.ok(mapOf("productId" to productId, "stock" to 0, "reserved" to 0))
        } else {
            return ResponseEntity.ok(mapOf("productId" to inv.productId, "stock" to inv.stock, "reserved" to inv.reserved))
        }
    }
}
