package com.example.jeebapi.controllers

import com.example.jeebapi.DTO.Userdto
import com.example.jeebapi.models.User
import com.example.jeebapi.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    /**
     * POST /api/users
     * Creates a new user. Restricted to admins by the UserService.
     */
    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        val createdUser = userService.create(user)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser)
    }

    /**
     * GET /api/users
     * Retrieves a list of all users. Restricted to admins by the UserService.
     */
    @GetMapping
    fun getAllUsers(): ResponseEntity<List<Userdto>> {
        val users = userService.findAll()
        return ResponseEntity.ok(users)
    }

    /**
     * GET /api/users/{id}
     * Retrieves a single user by their ID.
     */
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.findById(id)
        return ResponseEntity.ok(user)
    }

    /**
     * PUT /api/users/{id}
     * Updates an existing user. Restricted to admins by the UserService.
     */
    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): ResponseEntity<User> {
        val updatedUser = userService.update(id, user)
        return ResponseEntity.ok(updatedUser)
    }

    /**
     * DELETE /api/users/{id}
     * Deletes a user. Restricted to admins by the UserService.
     */
    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteById(id)
        return ResponseEntity.noContent().build() // Returns HTTP 204 No Content
    }
}