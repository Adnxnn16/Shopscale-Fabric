package com.shopscale.inventory.infrastructure.idempotency

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "processed_events")
data class ProcessedEventJpa(
    @Id
    @Column(name = "event_id", nullable = false)
    val eventId: String,
    @Column(name = "processed_at", nullable = false)
    val processedAt: Instant
)
