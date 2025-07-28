package com.example.jeebapi.services

import com.example.jeebapi.auth.CustomUserDetails
import com.example.jeebapi.models.User
import com.example.jeebapi.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")

        // 1. Correctly map the access level to an authority
        // It now uses the constants from your User class
        val authorities = if (user.accesslevel == User.LEVEL_ADMIN) {
            listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
        } else {
            listOf(SimpleGrantedAuthority("ROLE_CASHIER"))
        }

        // 2. Return YOUR CustomUserDetails class, NOT Spring's default User
        return CustomUserDetails(user, authorities)
    }
}