package com.example.jeebapi.DTO

import com.example.jeebapi.models.Customer
import com.example.jeebapi.models.Transactions
import com.example.jeebapi.models.User
import java.time.LocalDateTime


    data class Invoicedto(
        val id: Long,
        val buyer: String,
        val status: String,
        val date: LocalDateTime?,
        val description: String?,
        val items: List<ItemDto>,
        val userId: Long?,
        val customerId: Long?,
        val phonenumber: String?,
        val paymentmethod: String?,
    )