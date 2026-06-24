package com.shopscale.product.data.datasource

import org.slf4j.LoggerFactory
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class MongoDbDataSource {
    private val logger = LoggerFactory.getLogger(MongoDbDataSource::class.java)

    @EventListener(ContextClosedEvent::class)
    fun onShutdown() {
        logger.info("Product service shutting down gracefully")
    }
}
