package com.example.jeebapi

import com.example.jeebapi.models.User
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
@Service
class ApiService(private val userRepository: UserRepository) {
    private val restTemplate = RestTemplate()




    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}