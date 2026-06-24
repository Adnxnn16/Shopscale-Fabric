package com.shopscale.inventory.infrastructure.idempotency

import org.springframework.data.jpa.repository.JpaRepository

interface ProcessedEventRepository : JpaRepository<ProcessedEventJpa, String>
