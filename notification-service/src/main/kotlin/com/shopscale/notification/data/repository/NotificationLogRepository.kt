package com.shopscale.notification.data.repository

import com.shopscale.notification.domain.entity.EmailNotification
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationLogRepository : MongoRepository<EmailNotification, String> {
    fun existsByOrderId(orderId: Long): Boolean
}
