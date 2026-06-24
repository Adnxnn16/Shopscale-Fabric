package com.shopscale.order.presentation.controller

import com.shopscale.order.domain.service.OrderService
import com.shopscale.order.presentation.dto.CreateOrderRequest
import com.shopscale.order.presentation.dto.OrderResponse
import com.shopscale.order.presentation.dto.toResponse
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Order", description = "Order management APIs")
@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @Operation(summary = "Create a new order")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: CreateOrderRequest): OrderResponse {
        return orderService.createOrder(request).toResponse()
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): OrderResponse {
        return orderService.getOrder(id).toResponse()
    }

    @Operation(summary = "Get orders by user ID")
    @GetMapping
    fun getByUser(@RequestParam userId: String): List<OrderResponse> {
        return orderService.getOrdersByUser(userId).map { it.toResponse() }
    }
}
