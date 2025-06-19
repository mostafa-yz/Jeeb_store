package com.example.jeebapi.services

import com.example.jeebapi.models.User
import com.example.jeebapi.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository, private val passwordEncoder: PasswordEncoder) {

    open fun create(user: User): User {
        if (userRepository.existsByEmail(user.email) ){
                throw IllegalArgumentException("Email already exists")
            }
        val hashpass = passwordEncoder.encode(user.password)
        val newuser = user.copy(password = hashpass)
        return userRepository.save(newuser)

    }

    fun findAll(): List<User> = userRepository.findAll()
    fun findById(id: Long): User = userRepository.findById(id).orElse(null)


}