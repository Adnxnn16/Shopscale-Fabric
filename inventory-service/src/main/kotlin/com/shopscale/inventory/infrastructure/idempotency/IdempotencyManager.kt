package com.shopscale.inventory.infrastructure.idempotency

import org.springframework.stereotype.Component
import java.time.Instant

@Component
class IdempotencyManager(private val processedEventRepository: ProcessedEventRepository) {
    fun isProcessed(eventId: String): Boolean = processedEventRepository.existsById(eventId)

    fun markProcessed(eventId: String) {
        processedEventRepository.save(ProcessedEventJpa(eventId = eventId, processedAt = Instant.now()))
    }
}
