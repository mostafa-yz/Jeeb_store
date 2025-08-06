package com.example.jeebapi.DTO

import java.math.BigDecimal

data class ProviderPaymentDTO(
    val providerName: String,
    val providerEmail: String,
    val providerShabaNumber: String,
    val providerCardNumber: String,
    val totalPaidToProvider: BigDecimal
)
