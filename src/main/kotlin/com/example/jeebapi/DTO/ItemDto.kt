package com.example.jeebapi.DTO

data class ItemDto(
    val id: Long,
    val name: String,
    val quantity: Int = 0,
    val price: Double,
    val invoiceId: Long?,
    val productId: Long?,
    val providerId: Long?,
)