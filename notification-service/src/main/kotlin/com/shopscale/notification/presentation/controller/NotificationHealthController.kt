package com.shopscale.notification.presentation.controller

import com.shopscale.notification.domain.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/notifications")
class NotificationHealthController(private val notificationService: NotificationService) {

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, Any>> {
        val body = mapOf(
            "status" to "UP",
            "sentCount" to notificationService.getSentCount()
        )
        return ResponseEntity.ok(body)
    }
}
