package com.example.jeebapi.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {


            val authHeader = request.getHeader("Authorization")
            val token = authHeader?.takeIf { it.startsWith("Bearer ") }?.substring(7)

            token?.let {
                val username = jwtService.extractUsername(it)
                println("🔓 Extracted username: $username")

                if (username != null && SecurityContextHolder.getContext().authentication == null) {
                    val userDetails = userDetailsService.loadUserByUsername(username)

                    if (jwtService.isTokenValid(it, userDetails)) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken

                        println("✅ Authenticated user: ${userDetails.username}")
                    } else {
                        println("❌ Token invalid for user: $username")
                    }
                }
            }

            filterChain.doFilter(request, response)

        } catch (e: Exception) {
        }
    }
}

