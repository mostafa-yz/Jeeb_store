package com.example.jeebapi.DTO

import java.math.BigDecimal

data class ReprotDTO(
    val providerName: String,
    val totalOurEarnings: BigDecimal,
    val totalPaidToProvider: BigDecimal,
    val totalTransactionValue: BigDecimal,
    val productName: String,
    val totalQuantitySold: Long
)
