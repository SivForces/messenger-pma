package com.messenger.backend.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date
import java.time.temporal.ChronoUnit
import java.time.Instant

@Service
class JwtService(
    @Value("\${app.jwt.secret}") secret: String,
    @Value("\${app.jwt.expiration-hours}") private val expirationHours: Long
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generate(userId: Long, username: String): String =
        Jwts.builder()
            .subject(userId.toString())
            .claim("username", username)
            .issuedAt(Date())
            .expiration(Date.from(Instant.now().plus(expirationHours, ChronoUnit.HOURS)))
            .signWith(key)
            .compact()

    fun parseUserId(token: String): Long? = try {
        Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).payload.subject.toLong()
    } catch (e: Exception) {
        null
    }
}