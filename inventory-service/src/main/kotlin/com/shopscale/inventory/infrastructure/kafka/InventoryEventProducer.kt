package com.shopscale.inventory.infrastructure.kafka

import com.shopscale.inventory.domain.event.InventoryReservationFailedEvent
import com.shopscale.inventory.domain.event.InventoryReservedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class InventoryEventProducer(
    private val reservedTemplate: KafkaTemplate<String, InventoryReservedEvent>,
    private val failedTemplate: KafkaTemplate<String, InventoryReservationFailedEvent>
) {
    fun publishReserved(event: InventoryReservedEvent) {
        reservedTemplate.send("inventory", event.orderId.toString(), event)
    }

    fun publishFailed(event: InventoryReservationFailedEvent) {
        failedTemplate.send("inventory", event.orderId.toString(), event)
    }
}
