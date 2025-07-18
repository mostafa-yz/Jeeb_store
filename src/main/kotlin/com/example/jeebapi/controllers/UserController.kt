package com.example.jeebapi.controllers

import com.example.jeebapi.auth.JwtService
import com.example.jeebapi.models.User
import com.example.jeebapi.repository.UserRepository
import com.example.jeebapi.services.UserService
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user")
class UserController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService


) {
    @PostMapping
    fun create(@RequestBody user: User): ResponseEntity<out Any?> {
        return try {
            userService.create(user)
            ResponseEntity.status(201).body("user created")
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(e.message)
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("error" to "Server error"))

        }


    }

    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        val auth = UsernamePasswordAuthenticationToken(request.username, request.password)
        authenticationManager.authenticate(auth)








        val user = userDetailsService.loadUserByUsername(request.username)
        val token = jwtService.generateToken(user.username)
        return ResponseEntity.ok(AuthResponse(token))
    }


    @GetMapping("/find/{id}")
    @PreAuthorize("isAuthenticated()")
    fun findById(@PathVariable id: Long): User {
        return userService.findById(id)
        ResponseEntity.status(201).body("user created")
    }


}

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String)