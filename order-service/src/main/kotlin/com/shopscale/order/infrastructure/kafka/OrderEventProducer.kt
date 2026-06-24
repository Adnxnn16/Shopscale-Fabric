package com.shopscale.order.infrastructure.kafka

import com.shopscale.order.domain.event.OrderPlacedEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderEventProducer(private val kafkaTemplate: KafkaTemplate<String, OrderPlacedEvent>) {
    private val logger = LoggerFactory.getLogger(OrderEventProducer::class.java)

    fun publish(event: OrderPlacedEvent) {
        kafkaTemplate.send("orders", event.orderId.toString(), event)
            .whenComplete { _, ex ->
                if (ex != null) {
                    logger.error("Failed to publish OrderPlacedEvent for orderId={}", event.orderId, ex)
                } else {
                    logger.info("Published OrderPlacedEvent for orderId={}", event.orderId)
                }
            }
    }
}
