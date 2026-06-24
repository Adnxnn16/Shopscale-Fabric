package com.shopscale.gateway.controller

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.Date

data class LoginRequest(val email: String = "", val password: String = "")
data class SignupRequest(val name: String = "", val email: String = "", val password: String = "")
data class AuthResponse(val token: String, val userId: String, val email: String, val name: String)

@CrossOrigin(origins = ["*"], allowedHeaders = ["*"], methods = [
    org.springframework.web.bind.annotation.RequestMethod.GET,
    org.springframework.web.bind.annotation.RequestMethod.POST,
    org.springframework.web.bind.annotation.RequestMethod.OPTIONS
])
@RestController
@RequestMapping("/api/auth")
class AuthController(
    @Value("\${jwt.secret:shopscale-secret-key-minimum-256-bits-long-for-hs256-algorithm}")
    private val jwtSecret: String
) {

    /**
     * Mock login — accepts any email/password and returns a signed JWT.
     * The userId is derived from the email prefix so it is consistent per user.
     */
    @PostMapping("/login")
    fun login(@RequestBody body: LoginRequest): ResponseEntity<Any> {
        if (body.email.isBlank() || body.password.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Email and password are required"))
        }
        val userId = body.email.substringBefore("@").replace(Regex("[^a-zA-Z0-9]"), "-")
        val name   = userId.replaceFirstChar { it.uppercase() }
        val token  = generateToken(userId, body.email, listOf("USER"))
        return ResponseEntity.ok(AuthResponse(token, userId, body.email, name))
    }

    /**
     * Mock signup — registers a new user (in-memory / stateless demo).
     * Returns the same JWT so the user is immediately logged in.
     */
    @PostMapping("/signup")
    fun signup(@RequestBody body: SignupRequest): ResponseEntity<Any> {
        if (body.email.isBlank() || body.password.isBlank() || body.name.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Name, email and password are required"))
        }
        if (body.password.length < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(mapOf("error" to "Password must be at least 6 characters"))
        }
        val userId = body.email.substringBefore("@").replace(Regex("[^a-zA-Z0-9]"), "-")
        val token  = generateToken(userId, body.email, listOf("USER"))
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(AuthResponse(token, userId, body.email, body.name))
    }

    private fun generateToken(userId: String, email: String, roles: List<String>): String {
        val key = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        return Jwts.builder()
            .setSubject(userId)
            .claim("email", email)
            .claim("roles", roles.joinToString(","))
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 h
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }
}
