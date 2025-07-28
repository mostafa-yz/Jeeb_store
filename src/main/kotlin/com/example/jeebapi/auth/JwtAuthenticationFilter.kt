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

@Component // This annotation is crucial!
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 2. Remove header logic, get JWT from the cookie instead
        val jwt = getJwtFromCookie(request)

        if (jwt != null) {
            val username = jwtService.extractUsername(jwt)

            if (SecurityContextHolder.getContext().authentication == null) {
                val userDetails = this.userDetailsService.loadUserByUsername(username)

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                    println("✅ Authenticated user from cookie: $username")
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    // 3. Add this helper function to read the cookie
    private fun getJwtFromCookie(request: HttpServletRequest): String? {
        // Return null if no cookies are present
        val cookies = request.cookies ?: return null
        // Find the cookie with the name "accessToken"
        return cookies.find { it.name == "accessToken" }?.value
    }
}

