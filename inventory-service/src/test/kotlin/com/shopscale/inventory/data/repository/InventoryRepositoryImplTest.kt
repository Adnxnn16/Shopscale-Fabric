package com.shopscale.inventory.data.repository

import com.shopscale.inventory.data.model.InventoryJpa
import com.shopscale.inventory.domain.entity.Inventory
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.Instant

class InventoryRepositoryImplTest {

    @Test
    fun `findByProductId returns mapped domain entity`() = runBlocking {
        val jpaRepository = mock<InventoryJpaRepository> {
            on { findByProductId("p-1") } doReturn InventoryJpa(1L, "p-1", 10, 1, Instant.now())
        }
        val repository = InventoryRepositoryImpl(jpaRepository)

        val inventory = repository.findByProductId("p-1")

        assertEquals("p-1", inventory?.productId)
        assertEquals(10, inventory?.stock)
    }

    @Test
    fun `findByProductId returns null when not present`() = runBlocking {
        val jpaRepository = mock<InventoryJpaRepository> {
            on { findByProductId("missing") } doReturn null
        }
        val repository = InventoryRepositoryImpl(jpaRepository)

        val inventory = repository.findByProductId("missing")

        assertNull(inventory)
    }

    @Test
    fun `save maps domain to jpa and back`() = runBlocking {
        val jpaRepository = mock<InventoryJpaRepository> {
            on { save(any<InventoryJpa>()) } doReturn InventoryJpa(1L, "p-1", 8, 2, Instant.now())
        }
        val repository = InventoryRepositoryImpl(jpaRepository)

        val saved = repository.save(Inventory(null, "p-1", 8, 2, Instant.now()))

        assertEquals(1L, saved.id)
        assertEquals(2, saved.reserved)
    }
}
