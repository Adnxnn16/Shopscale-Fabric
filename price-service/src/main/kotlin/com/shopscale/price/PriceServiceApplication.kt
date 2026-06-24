package com.shopscale.price

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class PriceServiceApplication

fun main(args: Array<String>) {
    runApplication<PriceServiceApplication>(*args)
}
