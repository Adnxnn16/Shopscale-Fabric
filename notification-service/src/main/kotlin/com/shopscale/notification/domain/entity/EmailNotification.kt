package com.shopscale.notification.domain.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "notifications")
data class EmailNotification(
    @Id val id: String,
    val orderId: Long,
    val email: String,
    val status: NotificationStatus,
    val sentAt: Instant
)

enum class NotificationStatus {
    SENT, FAILED, SKIPPED
}
