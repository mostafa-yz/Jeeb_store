package com.example.jeebapi.controllers

import com.example.jeebapi.DTO.Userdto
import com.example.jeebapi.auth.JwtService
import com.example.jeebapi.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
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
        response: HttpServletResponse
    ): ResponseEntity<Userdto> {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.username, request.password)
            )
        } catch (e: BadCredentialsException) {
            // Return 401 Unauthorized with a specific error message for bad credentials
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(null) // Or a custom error DTO if you have one
        } catch (e: Exception) {
            // Handle other exceptions like a disabled account, etc.
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(null)
        }

        val user = userRepository.findByEmail(request.username)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)

        val userDto = Userdto(
            id = user.id,
            name = user.name,
            email = user.email,
            accesslevel = user.accesslevel
        )

        val userDetails = userDetailsService.loadUserByUsername(request.username)
        val token = jwtService.generateToken(userDetails.username)

        val cookie: ResponseCookie = ResponseCookie.from("accessToken", token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite("Lax")
            .build()

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

        return ResponseEntity.ok(userDto)
    }

    @GetMapping("/me")
    fun getCurrentUser(principal: Principal): ResponseEntity<Userdto> {

        val user = userRepository.findByEmail(principal.name)
            ?: throw Exception("User not found from token")

        val userDto = Userdto(
            id = user.id,
            name = user.name,
            email = user.email,
            accesslevel = user.accesslevel
        )


        return ResponseEntity.ok(userDto)
    }



    @PostMapping("/logout")
    fun logout(response: HttpServletResponse): ResponseEntity<Void> {
        // Create a cookie that expires immediately, instructing the browser to delete it
        val cookie: ResponseCookie = ResponseCookie.from("accessToken", "")
            .httpOnly(true)
            .secure(true) // Should match the setting of your login cookie
            .path("/")
            .maxAge(0) // 🍪 This is the key: it deletes the cookie
            .sameSite("Lax")
            .build()

        // Add the deletion cookie to the response headers
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())

        return ResponseEntity.ok().build()
    }









}

// Data classes can be here or in their own files
data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String, val user: Userdto)