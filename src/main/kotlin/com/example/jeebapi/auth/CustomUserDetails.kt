package com.example.jeebapi.auth

import com.example.jeebapi.models.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {

    val accesslevel: Int
        get() = user.accesslevel

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities
    override fun getPassword(): String = user.password
    override fun getUsername(): String = user.email

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}