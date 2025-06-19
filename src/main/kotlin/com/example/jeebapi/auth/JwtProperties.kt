package com.example.jeebapi.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "jwt")
@Configuration
class JwtProperties {
    var secret: String = ""
    var expiration: Long = 0

}