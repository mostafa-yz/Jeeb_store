package com.example.jeebapi.models

import com.example.jeebapi.DTO.ActionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "storage_log")
data class StorageLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    var quantity: Int = 0,

    @Enumerated(EnumType.STRING)
    var action: ActionType = ActionType.RECHARGE,

    var reason: String? = null,

    var date: Instant = Instant.now(),

    @Column(name = "product_id", nullable = true)
    var productId: Long? = null,

    @Column(name = "provider_id", nullable = true)
    var providerId: Long? = null,

    // Snapshot values
    @Column(name = "product_name")
    var productName: String = "", // <-- FIXED: Added default value

    @Column(name = "provider_name", nullable = true) // provider name can be null
    var providerName: String? = null // <-- FIXED: Added default value & made nullable
)