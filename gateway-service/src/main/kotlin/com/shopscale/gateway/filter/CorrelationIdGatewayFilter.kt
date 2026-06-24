package com.shopscale.gateway.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class CorrelationIdGatewayFilter : GlobalFilter, Ordered {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getOrder(): Int = -2  // Run before JWT filter

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val correlationId = exchange.request.headers.getFirst(CORRELATION_ID_HEADER)
            ?.takeIf { it.isNotBlank() }
            ?: UUID.randomUUID().toString()

        log.debug("CorrelationId: $correlationId for path=${exchange.request.uri.path}")

        val mutatedRequest = exchange.request.mutate()
            .header(CORRELATION_ID_HEADER, correlationId)
            .build()

        val mutatedResponse = exchange.response.beforeCommit {
            Mono.fromRunnable {
                exchange.response.headers.set(CORRELATION_ID_HEADER, correlationId)
            }
        }

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
    }

    companion object {
        private const val CORRELATION_ID_HEADER = "X-Correlation-Id"
    }
}
