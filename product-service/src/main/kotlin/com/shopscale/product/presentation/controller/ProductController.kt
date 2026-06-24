package com.shopscale.product.presentation.controller

import com.shopscale.product.domain.service.ProductService
import com.shopscale.product.presentation.dto.ProductRequest
import com.shopscale.product.presentation.dto.ProductResponse
import com.shopscale.product.presentation.dto.toResponse
import jakarta.validation.Valid
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "Product", description = "Product management APIs")
@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @Operation(summary = "Get all products")
    @GetMapping
    fun getAll(@org.springframework.web.bind.annotation.RequestParam(required = false) category: String?): List<ProductResponse> = runBlocking {
        productService.getAll(category).map { it.toResponse() }
    }

    @Operation(summary = "Get product by ID")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): ProductResponse = runBlocking {
        productService.getById(id).toResponse()
    }

    @Operation(summary = "Create a new product")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: ProductRequest): ProductResponse = runBlocking {
        productService.create(request).toResponse()
    }

    @Operation(summary = "Get featured products")
    @GetMapping("/featured")
    fun getFeatured(): List<ProductResponse> = runBlocking {
        productService.getFeatured().map { it.toResponse() }
    }

    @Operation(summary = "Delete product by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) = runBlocking {
        productService.deleteById(id)
    }
}
