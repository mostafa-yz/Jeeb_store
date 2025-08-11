package com.example.jeebapi.DTO

import com.example.jeebapi.models.Invoice
import com.example.jeebapi.models.Products
import com.example.jeebapi.models.Provider
import com.example.jeebapi.models.User
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.Instant
import java.time.LocalDateTime

data class TransactionDto(
    val id: Int,
    val price: Double = 0.0,
    val quantity: Int = 0,
    val date: LocalDateTime? = null,
    val Invoice_id: Invoice,
    val product_id: Products,
    val user_id: User,
    val provider_id: Provider,


    )
