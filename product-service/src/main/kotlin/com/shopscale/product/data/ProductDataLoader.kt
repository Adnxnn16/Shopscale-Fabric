package com.shopscale.product.data

import com.shopscale.product.data.model.ProductDocument
import com.shopscale.product.data.repository.ProductMongoRepository
import com.shopscale.product.domain.entity.InventoryStatus
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Component
class ProductDataLoader(
    private val repository: ProductMongoRepository,
    private val featuredGroupRepository: com.shopscale.product.data.repository.FeaturedGroupMongoRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (repository.count() == 0L) {
            val products = mutableListOf<ProductDocument>()
            val now = Instant.now()

            // ---------------------------------------------------------------
            // 40 exact PRD products.
            // Image mapping rules:
            //   • 35 real image files exist in frontend/public/images/<category>/
            //   • Each image assigned to the single closest PRD product by type
            //   • 5 products with no viable image get /images/placeholder.svg
            //   • Images may be cross-folder (URL is just a static file path)
            // ---------------------------------------------------------------
            data class CatalogEntry(val name: String, val category: String, val imageUrl: String)

            val catalog = listOf(
                // ── Electronics (8 products) ──────────────────────────────
                // Type-matched images: headphone, smartwatch, tablet, earbuds
                // No laptop/phone/e-reader images exist → 4 placeholders
                CatalogEntry("MacBook Pro M4",            "electronics",    "/images/electronics/macbook.png"),
                CatalogEntry("Dell XPS 15",               "electronics",    "/images/electronics/dell-xps.png"),
                CatalogEntry("Samsung Galaxy S25 Ultra",   "electronics",    "/images/electronics/samsung.png"),
                CatalogEntry("Sony WH-1000XM5",           "electronics",    "/images/electronics/quantum-wireless-headphone.jpg"),
                CatalogEntry("Apple Watch Ultra 2",        "electronics",    "/images/electronics/apex-smartwatch-pro.jpeg"),
                CatalogEntry("iPad Pro M4",                "electronics",    "/images/electronics/novatab-ultra-10.webp"),
                CatalogEntry("AirPods Pro 2",              "electronics",    "/images/electronics/echobuds-true-wireless.jpg"),
                CatalogEntry("Kindle Paperwhite",          "electronics",    "/images/electronics/kindle.png"),

                // ── Gaming (8 products) ──────────────────────────────────
                // Type-matched: mouse, headset, chair, monitor (cross-folder)
                // Approximate: controller→PS5, RGB-stand→keyboard, vacuum→Switch
                // No console image exists → 1 placeholder (Xbox)
                CatalogEntry("PlayStation 5",              "gaming",         "/images/gaming/pixelplay-controller.png"),
                CatalogEntry("Xbox Series X",              "gaming",         "/images/gaming/xbox.png"),
                CatalogEntry("Gaming Monitor",             "gaming",         "/images/electronics/visionpro-4k-monitor.jpeg"),
                CatalogEntry("Mechanical Keyboard",        "gaming",         "/images/gaming/neon-rgb-headset-stand.png"),
                CatalogEntry("Gaming Mouse",               "gaming",         "/images/gaming/fps-target-gaming-mouse.webp"),
                CatalogEntry("Gaming Chair",               "gaming",         "/images/gaming/titan-pro-gaming-chair.webp"),
                CatalogEntry("Nintendo Switch OLED",       "gaming",         "/images/smart-living/robot-vacuum-cleaner.webp"),
                CatalogEntry("Gaming Headset",             "gaming",         "/images/gaming/phantom-gaming-headset.jpg"),

                // ── Smart Living (8 products) ────────────────────────────
                // Type-matched: LED lamp, purifier, camera, speaker(cross), powerbank(cross)
                // Approximate: thermostat→speaker, door-lock→watch, sensor→charger
                CatalogEntry("Smart Watch",                "smart-living",   "/images/smart-living/smart-door-lock.webp"),
                CatalogEntry("Smart LED Lamp",             "smart-living",   "/images/smart-living/smart-led-bulb-wi-fi.jpg"),
                CatalogEntry("Air Purifier",               "smart-living",   "/images/smart-living/smart-air-purifier.jpg"),
                CatalogEntry("Smart Security Camera",      "smart-living",   "/images/smart-living/smart-security-camera-360.jpg"),
                CatalogEntry("Smart Speaker",              "smart-living",   "/images/smart-living/smart-thermostat.jpg"),
                CatalogEntry("Wireless Charger",           "smart-living",   "/images/smart-living/smart-motion-sensor.jpg"),
                CatalogEntry("Power Bank",                 "smart-living",   "/images/electronics/powervault-20000mah.jpeg"),
                CatalogEntry("Bluetooth Speaker",          "smart-living",   "/images/electronics/boombeat-bluetooth-speaker.webp"),

                // ── Creator Tools (8 products) ───────────────────────────
                // Type-matched: tripod, SSD, ring-light, webcam, mic, stream-deck, camera(cross)
                // Approximate: green-screen→drawing-tablet
                CatalogEntry("Stream Deck",                "creator-tools",  "/images/creator-tools/video-editing-controller.jpg"),
                CatalogEntry("Studio Microphone",          "creator-tools",  "/images/creator-tools/studio-microphone-kit.webp"),
                CatalogEntry("Mirrorless Camera",          "creator-tools",  "/images/electronics/xtreme-action-camera-4k.jpeg"),
                CatalogEntry("SSD Storage",                "creator-tools",  "/images/creator-tools/external-ssd-1tb.jpg"),
                CatalogEntry("Ring Light Kit",             "creator-tools",  "/images/creator-tools/led-ring-light-18-inch.webp"),
                CatalogEntry("Drawing Tablet",             "creator-tools",  "/images/creator-tools/portable-green-screen.webp"),
                CatalogEntry("Professional Webcam",        "creator-tools",  "/images/creator-tools/streaming-webcam-1080p.jpg"),
                CatalogEntry("Camera Tripod",              "creator-tools",  "/images/creator-tools/camera-tripod-pro.jpg"),

                // ── Digital Assets (8 products) ──────────────────────────
                // All digital — 8 images for 8 products, assigned by position
                CatalogEntry("React SaaS Template",        "digital-assets", "/images/digital-assets/youtube-intro-templates.png"),
                CatalogEntry("Admin Dashboard Kit",        "digital-assets", "/images/digital-assets/youtube-thumbnail-pack.webp"),
                CatalogEntry("UI Design System",           "digital-assets", "/images/digital-assets/video-transitions-pack.jpg"),
                CatalogEntry("Component Library",          "digital-assets", "/images/digital-assets/sound-effects-library.webp"),
                CatalogEntry("Next.js Starter Kit",        "digital-assets", "/images/digital-assets/motion-graphics-pack.avif"),
                CatalogEntry("Figma Design Kit",           "digital-assets", "/images/digital-assets/lightroom-presets-pack.jpg"),
                CatalogEntry("Mobile UI Kit",              "digital-assets", "/images/digital-assets/fonts-mega-bundle.jpeg"),
                CatalogEntry("Productivity Template Bundle","digital-assets", "/images/digital-assets/cinematic-lut-pack.webp")
            )

            // Track per-category index for price/status assignment
            val categoryIndexTracker = mutableMapOf<String, Int>()

            catalog.forEachIndexed { globalIndex, entry ->
                val catIndex = categoryIndexTracker.getOrDefault(entry.category, 0)
                categoryIndexTracker[entry.category] = catIndex + 1

                products.add(
                    ProductDocument(
                        id = UUID.nameUUIDFromBytes(entry.name.toByteArray()).toString(),
                        name = entry.name,
                        description = "Premium quality ${entry.name} designed for the best experience. Built with high performance and reliability in mind.",
                        price = (999 + catIndex * 500) * 100, // paise — fallback pricing
                        category = entry.category,
                        rating = 4.5 + (catIndex % 5) * 0.1,
                        inventoryStatus = when (catIndex) {
                            0 -> InventoryStatus.NEW_ARRIVAL
                            1 -> InventoryStatus.BEST_SELLER
                            else -> InventoryStatus.IN_STOCK
                        },
                        imageUrl = entry.imageUrl,
                        createdAt = now.minus((globalIndex * 2).toLong(), ChronoUnit.DAYS)
                    )
                )
            }

            repository.saveAll(products)
            println("Seeded ${products.size} PRD-compliant products.")

            // Featured groups — indices into the flat 40-product list
            //   Electronics:    0..7
            //   Gaming:         8..15
            //   Smart Living:  16..23
            //   Creator Tools: 24..31
            //   Digital Assets:32..39
            val performanceCollection = products.slice(0..5).map { it.id!! }
            val creatorEssentials     = products.slice(24..29).map { it.id!! }
            val digitalBuilderPack    = products.slice(32..37).map { it.id!! }

            featuredGroupRepository.saveAll(listOf(
                com.shopscale.product.data.model.FeaturedGroupDocument(
                    id = "perf-collection",
                    name = "Performance Collection",
                    description = "Engineered for maximum output",
                    productIds = performanceCollection
                ),
                com.shopscale.product.data.model.FeaturedGroupDocument(
                    id = "creator-essentials",
                    name = "Creator Essentials",
                    description = "Must-have tools for modern creators",
                    productIds = creatorEssentials
                ),
                com.shopscale.product.data.model.FeaturedGroupDocument(
                    id = "digital-builder-pack",
                    name = "Digital Builder Pack",
                    description = "Everything you need to build the future",
                    productIds = digitalBuilderPack
                )
            ))
            println("Seeded 3 featured groups.")
        }
    }
}
