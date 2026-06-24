package com.shopscale.gateway.filter

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.Date

class JwtAuthenticationFilterTest {

    private val secret = "shopscale-secret-key-minimum-256-bits-long-for-hs256-algorithm"
    private lateinit var filter: JwtAuthenticationFilter
    private val chain: GatewayFilterChain = mock()

    @BeforeEach
    fun setup() {
        filter = JwtAuthenticationFilter(secret)
        // Mock chain to return Mono.empty() (success)
        whenever(chain.filter(org.mockito.kotlin.any())).thenReturn(Mono.empty())
    }

    @Test
    fun `request without Authorization header returns 401`() {
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/api/orders").build()
        )

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete()

        assert(exchange.response.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `request with invalid JWT token returns 401`() {
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token-xyz")
                .build()
        )

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete()

        assert(exchange.response.statusCode == HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `request with valid JWT proceeds to chain`() {
        val token = buildValidToken("user123", listOf("ROLE_USER"))
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.post("/api/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .build()
        )

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete()

        // 200 means chain was called (mock returns Mono.empty())
        assert(exchange.response.statusCode != HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `actuator path bypasses JWT check`() {
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/actuator/health").build()
        )

        StepVerifier.create(filter.filter(exchange, chain))
            .verifyComplete()

        // Should not be UNAUTHORIZED
        assert(exchange.response.statusCode != HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun `filter order is -1`() {
        assert(filter.order == -1)
    }

    private fun buildValidToken(subject: String, roles: List<String>): String {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        return Jwts.builder()
            .setSubject(subject)
            .claim("roles", roles.joinToString(","))
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 3_600_000)) // 1 hour
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}
