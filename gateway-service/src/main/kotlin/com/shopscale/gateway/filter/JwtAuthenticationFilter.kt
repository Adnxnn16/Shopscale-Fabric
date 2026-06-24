package com.shopscale.gateway.filter

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationFilter(
    @Value("\${jwt.secret:shopscale-secret-key-minimum-256-bits-long-for-hs256-algorithm}")
    private val jwtSecret: String
) : GlobalFilter, Ordered {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getOrder(): Int = -1  // Run before routing filters

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val path = exchange.request.uri.path

        // Skip JWT validation for actuator and auth endpoints (public)
        if (path.startsWith("/actuator") || path.startsWith("/api/auth")) {
            return chain.filter(exchange)
        }

        val authHeader = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path=$path")
            return onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED)
        }

        val token = authHeader.substring(7)
        return try {
            val claims = validateToken(token)
            val userId = claims.subject
            val roles = claims["roles"]?.toString() ?: ""

            // Propagate user identity to downstream services as headers
            val mutatedRequest = exchange.request.mutate()
                .header("X-User-Id", userId)
                .header("X-User-Roles", roles)
                .build()

            chain.filter(exchange.mutate().request(mutatedRequest).build())
        } catch (ex: JwtException) {
            log.warn("JWT validation failed: ${ex.message}")
            onError(exchange, "Invalid JWT: ${ex.message}", HttpStatus.UNAUTHORIZED)
        } catch (ex: IllegalArgumentException) {
            log.warn("JWT argument error: ${ex.message}")
            onError(exchange, "Invalid JWT format", HttpStatus.UNAUTHORIZED)
        }
    }

    private fun validateToken(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body
    }

    private fun onError(
        exchange: ServerWebExchange,
        message: String,
        status: HttpStatus
    ): Mono<Void> {
        val response = exchange.response
        response.statusCode = status
        response.headers.contentType = MediaType.APPLICATION_JSON
        val body = """{"error":"UNAUTHORIZED","message":"$message"}"""
        val buffer = response.bufferFactory().wrap(body.toByteArray())
        return response.writeWith(Mono.just(buffer))
    }
}
