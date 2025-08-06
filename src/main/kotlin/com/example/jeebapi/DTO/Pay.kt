package com.example.jeebapi.DTO

import java.math.BigDecimal
import java.time.Instant

data class Pay(
    val id: Long,
    val date: Instant,
    val amount: BigDecimal,
    val note: String?,
    val providerId: Long?

    )
