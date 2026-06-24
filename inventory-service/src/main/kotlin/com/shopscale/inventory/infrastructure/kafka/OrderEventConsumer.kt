package com.shopscale.inventory.infrastructure.kafka

import com.shopscale.inventory.domain.event.OrderPlacedEvent
import com.shopscale.inventory.domain.service.InventoryService
import kotlinx.coroutines.runBlocking
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderEventConsumer(private val inventoryService: InventoryService) {

    @KafkaListener(topics = ["orders"], groupId = "inventory-group")
    fun onOrderPlaced(event: OrderPlacedEvent) {
        inventoryService.processOrder(event)
    }
}
