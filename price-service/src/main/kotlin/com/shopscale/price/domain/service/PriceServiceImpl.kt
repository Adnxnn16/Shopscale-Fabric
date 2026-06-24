package com.shopscale.price.domain.service

import com.shopscale.price.data.repository.PriceRepository
import com.shopscale.price.domain.entity.ProductPrice
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class PriceServiceImpl(
    private val priceRepository: PriceRepository,
    @Value("\${price.simulation.max-delay-ms:0}") private val maxDelayMs: Long
) : PriceService {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getPrice(productId: String): Optional<ProductPrice> {
        simulateLatency()
        return priceRepository.findById(productId)
    }

    override fun getAllPrices(): List<ProductPrice> = priceRepository.findAll().toList()

    /**
     * Simulates variable network/processing latency for circuit breaker testing.
     * Only active when PRICE_MAX_DELAY_MS env var is set to a value > 0.
     * IMPORTANT: Uses Thread.sleep — acceptable here because this is a test simulation
     * helper, NOT production business logic. In production, maxDelayMs defaults to 0.
     */
    private fun simulateLatency() {
        if (maxDelayMs > 0) {
            val delay = (Math.random() * maxDelayMs).toLong()
            log.debug("Simulating latency of {}ms", delay)
            Thread.sleep(delay)
        }
    }
}
