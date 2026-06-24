package com.shopscale.price.data

import com.shopscale.price.data.repository.PriceRepository
import com.shopscale.price.domain.entity.ProductPrice
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class PriceDataLoader(
    private val priceRepository: PriceRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(PriceDataLoader::class.java)

    override fun run(vararg args: String?) {
        if (priceRepository.count() == 0L) {
            val products = mutableListOf<ProductPrice>()

            // Must match ProductDataLoader exactly — same 40 names in same order
            // so UUID.nameUUIDFromBytes(name.toByteArray()) produces identical IDs.
            data class CatalogEntry(val name: String, val category: String)

            val catalog = listOf(
                // Electronics
                CatalogEntry("MacBook Pro M4",            "electronics"),
                CatalogEntry("Dell XPS 15",               "electronics"),
                CatalogEntry("Samsung Galaxy S25 Ultra",   "electronics"),
                CatalogEntry("Sony WH-1000XM5",           "electronics"),
                CatalogEntry("Apple Watch Ultra 2",        "electronics"),
                CatalogEntry("iPad Pro M4",                "electronics"),
                CatalogEntry("AirPods Pro 2",              "electronics"),
                CatalogEntry("Kindle Paperwhite",          "electronics"),

                // Gaming
                CatalogEntry("PlayStation 5",              "gaming"),
                CatalogEntry("Xbox Series X",              "gaming"),
                CatalogEntry("Gaming Monitor",             "gaming"),
                CatalogEntry("Mechanical Keyboard",        "gaming"),
                CatalogEntry("Gaming Mouse",               "gaming"),
                CatalogEntry("Gaming Chair",               "gaming"),
                CatalogEntry("Nintendo Switch OLED",       "gaming"),
                CatalogEntry("Gaming Headset",             "gaming"),

                // Smart Living
                CatalogEntry("Smart Watch",                "smart-living"),
                CatalogEntry("Smart LED Lamp",             "smart-living"),
                CatalogEntry("Air Purifier",               "smart-living"),
                CatalogEntry("Smart Security Camera",      "smart-living"),
                CatalogEntry("Smart Speaker",              "smart-living"),
                CatalogEntry("Wireless Charger",           "smart-living"),
                CatalogEntry("Power Bank",                 "smart-living"),
                CatalogEntry("Bluetooth Speaker",          "smart-living"),

                // Creator Tools
                CatalogEntry("Stream Deck",                "creator-tools"),
                CatalogEntry("Studio Microphone",          "creator-tools"),
                CatalogEntry("Mirrorless Camera",          "creator-tools"),
                CatalogEntry("SSD Storage",                "creator-tools"),
                CatalogEntry("Ring Light Kit",             "creator-tools"),
                CatalogEntry("Drawing Tablet",             "creator-tools"),
                CatalogEntry("Professional Webcam",        "creator-tools"),
                CatalogEntry("Camera Tripod",              "creator-tools"),

                // Digital Assets
                CatalogEntry("React SaaS Template",        "digital-assets"),
                CatalogEntry("Admin Dashboard Kit",        "digital-assets"),
                CatalogEntry("UI Design System",           "digital-assets"),
                CatalogEntry("Component Library",          "digital-assets"),
                CatalogEntry("Next.js Starter Kit",        "digital-assets"),
                CatalogEntry("Figma Design Kit",           "digital-assets"),
                CatalogEntry("Mobile UI Kit",              "digital-assets"),
                CatalogEntry("Productivity Template Bundle","digital-assets")
            )

            val categoryIndexTracker = mutableMapOf<String, Int>()

            catalog.forEach { entry ->
                val catIndex = categoryIndexTracker.getOrDefault(entry.category, 0)
                categoryIndexTracker[entry.category] = catIndex + 1

                products.add(
                    ProductPrice(
                        productId = java.util.UUID.nameUUIDFromBytes(entry.name.toByteArray()).toString(),
                        price = (999 + catIndex * 500) * 100, // paise — fallback pricing
                        currency = "INR"
                    )
                )
            }

            priceRepository.saveAll(products)
            log.info("Seeded ${products.size} PRD-compliant product prices.")
        }
    }
}
