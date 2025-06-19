package com.example.jeebapi.services

import com.example.jeebapi.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val    userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found")

        val role = when (user.accesslevel) {
            0 -> "ROLE_USER"
            1 -> "ROLE_MANAGER"
            2 -> "ROLE_ADMIN"
            else -> "ROLE_USER"
        }




        return org.springframework.security.core.userdetails.User
            .withUsername(user.email)
            .password(user.password)
            .authorities(SimpleGrantedAuthority(role))
            .build()
    }

}