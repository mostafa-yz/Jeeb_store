package com.example.jeebapi.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

import jakarta.persistence.Table
import jakarta.persistence.Id

@Entity
@Table(name = "invo_products")
data class InvoProducts(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,
    var name: String = "",
    var quantity: Int = 0,
    var price: Double = 0.0,

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    var invoice: Invoice? = null,


    @ManyToOne
    @JoinColumn(name = "product_id")
    val product: Products? = null,

    @ManyToOne
    @JoinColumn(name = "provider_id")
    var provider: Provider? = null,

    )