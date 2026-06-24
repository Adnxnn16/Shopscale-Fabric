package com.shopscale.notification.domain.service

import com.shopscale.notification.domain.event.OrderPlacedEvent

interface NotificationService {
    fun sendOrderConfirmation(event: OrderPlacedEvent)
    fun getSentCount(): Int
}
