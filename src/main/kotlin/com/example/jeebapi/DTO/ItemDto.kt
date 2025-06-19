package com.example.jeebapi.DTO

import com.example.jeebapi.models.Invoice
import com.example.jeebapi.models.Products
import com.example.jeebapi.models.Provider

data class ItemDto(
    val id: Long,
    val name: String,
    var quantity: Long,
    val price: Double,
    val invoiceId: Long?,
    val productId: Long?,
    val providerId: Long?,
)