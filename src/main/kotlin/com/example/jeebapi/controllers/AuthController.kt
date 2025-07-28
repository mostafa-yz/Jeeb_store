package com.example.jeebapi.controllers

import com.example.jeebapi.DTO.Userdto
import com.example.jeebapi.auth.JwtService
import com.example.jeebapi.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import kotlin.time.Duration

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
    private val userRepository: UserRepository
) {


    @PostMapping("/login")
    fun login(
        @RequestBody request: AuthRequest,
        response: HttpServletResponse // Inject HttpServletResponse to set headers
    ): ResponseEntity<Userdto> { // Return only the user data in the body
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )

        val user = userRepository.findByEmail(request.username)
            ?: throw Exception("User not found with email: ${request.username}")

        val userDto = Userdto(
            id = user.id,
            name = user.name,
            email = user.email,
            accesslevel = user.accesslevel
        )

        val userDetails = userDetailsService.loadUserByUsername(request.username)
        val token = jwtService.generateToken(userDetails.username)

        // Create a secure, HttpOnly cookie 🍪
        val cookie: ResponseCookie = ResponseCookie.from("accessToken", token)
            .httpOnly(true)       // 🔒 Prevents access from JavaScript
            .secure(true)         // Only send over HTTPS (set to false for local HTTP dev)
            .path("/")            // Available to the entire site
//            .maxAge(Duration.ofHours(1)) // Set cookie expiration (e.g., 1 hour)
            .sameSite("Lax")      // CSRF protection
            .build()

        // Add the cookie to the response headers
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

        // Return the user DTO in the body, token is now in the cookie
        return ResponseEntity.ok(userDto)
    }

    @GetMapping("/me")
    fun getCurrentUser(principal: Principal): ResponseEntity<Userdto> {
        // The Principal is correctly populated from the JWT cookie by Spring Security
        val user = userRepository.findByEmail(principal.name)
            ?: throw Exception("User not found from token")

        val userDto = Userdto(
            id = user.id,
            name = user.name,
            email = user.email,
            accesslevel = user.accesslevel
        )

        // ✅ Return the user data directly
        return ResponseEntity.ok(userDto)
    }













}

// Data classes can be here or in their own files
data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String, val user: Userdto)