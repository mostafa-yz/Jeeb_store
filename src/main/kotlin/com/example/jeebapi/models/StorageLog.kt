package com.example.jeebapi.models

import com.example.jeebapi.DTO.ActionType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "storage_log")
data class StorageLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val quantity: Int = 0,
    val action: ActionType = ActionType.RECHARGE,
    val reason: String? = null,
    var date: LocalDateTime? = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Products = Products(),  // Products must also have a no-arg constructor or default values

    @ManyToOne
    @JoinColumn(name = "provider_id")
    val provider: Provider? = null       // Same for Provider
)

