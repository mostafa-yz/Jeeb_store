package com.example.jeebapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

import jakarta.persistence.Table
import jakarta.persistence.Id

@Entity
@Table(name="invo_products")
data class InvoProducts(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val invoid: Long = 0,
    val name: String = "",
    val quantity: Long = 0,
    val price: Double,

    @ManyToOne
    @JoinColumn(name = "Invoice_id", nullable = false)
    val invoice: Invoice,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Products,
    @ManyToOne
    @JoinColumn(name = "provider_id")
    val provider: Provider,

)
