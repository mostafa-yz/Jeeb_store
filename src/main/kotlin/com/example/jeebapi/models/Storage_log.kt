package com.example.jeebapi.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name="storage_log")
data class storage_log(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val quantity: Int,
    val action: ActionType,
    val reason: String? = null,
    var date: LocalDateTime? =LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Products,

    @ManyToOne
    @JoinColumn(name = "provider_id")
    val provider: Provider?,



    )
enum class ActionType {
    RECHARGE, REMOVE,add
}

