package com.example.jeebapi.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime


@Entity
@Table(name = "payments")
data class Payments(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var date: LocalDateTime? = null,
    var amount: BigDecimal = BigDecimal.ZERO,
    var note: String? = null,
    @ManyToOne
    @JoinColumn(name = "provider_id")
    var provider: Provider? = null

)

