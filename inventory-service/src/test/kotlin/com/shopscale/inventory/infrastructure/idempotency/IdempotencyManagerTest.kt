package com.shopscale.inventory.infrastructure.idempotency

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class IdempotencyManagerTest {

    @Test
    fun `isProcessed reflects repository existence`() {
        val repository = mock<ProcessedEventRepository> {
            on { existsById("evt-1") } doReturn true
            on { existsById("evt-2") } doReturn false
        }
        val manager = IdempotencyManager(repository)

        assertTrue(manager.isProcessed("evt-1"))
        assertFalse(manager.isProcessed("evt-2"))
    }

    @Test
    fun `markProcessed persists processed event`() {
        val repository = mock<ProcessedEventRepository>()
        val manager = IdempotencyManager(repository)

        manager.markProcessed("evt-3")

        verify(repository).save(org.mockito.kotlin.check { saved ->
            assertTrue(saved.eventId == "evt-3")
        })
    }
}
