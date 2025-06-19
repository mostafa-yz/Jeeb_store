package com.example.jeebapi.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime


@Entity
@Table(name = "payments")
data class Payments(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val paymentDate: LocalDateTime = LocalDateTime.now(),
    val amount: Int = 0,
    val note: String? = null,
    val method: PaymentMethod,


)
enum class PaymentMethod {
    cashe,card,online
}

