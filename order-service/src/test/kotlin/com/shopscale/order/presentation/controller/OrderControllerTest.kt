package com.shopscale.order.presentation.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.shopscale.order.domain.service.OrderService
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import com.shopscale.order.domain.entity.Order
import com.shopscale.order.domain.entity.OrderStatus
import kotlinx.coroutines.runBlocking
import java.time.Instant

@WebMvcTest(OrderController::class)
class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var orderService: OrderService

    @Test
    fun shouldCreateOrder() = runBlocking {
        `when`(orderService.createOrder(any())).thenReturn(
            Order(id = 1L, userId = "u1", productId = "p1", quantity = 1, status = OrderStatus.PENDING, createdAt = Instant.now(), updatedAt = Instant.now())
        )
        mockMvc.perform(
            post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"userId":"u1","productId":"p1","quantity":1, "paymentMethod":"CREDIT_CARD", "shippingAddress":"123 Test St"}""")
        ).andExpect(status().isCreated)
    }
}
