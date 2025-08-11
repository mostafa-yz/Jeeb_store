package com.example.jeebapi.DTO

import java.math.BigDecimal
import java.time.LocalDateTime

data class Pay(
    val id: Long,
    val date: LocalDateTime? = null,
    val amount: BigDecimal,
    val note: String?,
    val providerId: Long?

    )
