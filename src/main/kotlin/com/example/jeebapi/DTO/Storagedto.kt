package com.example.jeebapi.DTO


import java.time.Instant
import java.time.LocalDateTime

data class Storagedto(
    val id: Long = 0,
    val quantity: Int,
    val action: ActionType,
    val reason: String? = "",
    val date: Instant?,
    val qr: String? = null,
    val productId: Long? = null,
    val providerId: Long? = null,
    val productName: String? = null,
    val providerName: String? = null,
)



  enum class ActionType {
    RECHARGE, REMOVE,ADD,decrease,invoice
}