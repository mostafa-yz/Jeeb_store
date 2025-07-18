package com.example.jeebapi.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table



@Entity
@Table(name = "Transactions")
class Transactions(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0,
    var price: Double = 0.0,
    var quantity: Int?=0,

    @ManyToOne
    @JoinColumn(name = "Invoice_id", nullable = false)
    val invoice: Invoice? = null, // Corrected: Nullable with a default value




    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Products? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User? = null,

    @ManyToOne
    @JoinColumn(name = "provider_id")
    val provider: Provider? = null
)


