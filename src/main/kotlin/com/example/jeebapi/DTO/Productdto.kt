package com.example.jeebapi.DTO

import com.example.jeebapi.models.Provider


data class Productdto(
    val id: Long = 0,
    val name: String,
    val category: String,
    val price: Double,
    val quantity: Int,
    val profit: Double,
    val qrcode: String,
    val position: String,
    val providerId: Long?



)
