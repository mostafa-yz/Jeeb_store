package com.example.jeebapi.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(@Autowired val jwtProperties: JwtProperties) {

    private val key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

        fun generateToken(username: String): String {
            val now = Date()
            val expiryDate = Date(now.time + jwtProperties.expiration)

            return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
        }

    fun extractUsername(token: String): String? =
        try {
            Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .body.subject
        } catch (e: Exception) {
            null
        }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val extractedUsername = extractUsername(token)
        val expired = isTokenExpired(token)
        println("🔍 Token check: extracted = $extractedUsername, expected = ${userDetails.username}, expired = $expired")
        return extractedUsername == userDetails.username && !expired
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val expiration = Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).body.expiration
            expiration.before(Date())
        } catch (e: Exception) {
            true
        }
    }
}
