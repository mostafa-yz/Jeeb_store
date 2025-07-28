package com.example.jeebapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

import jakarta.persistence.Table
import org.hibernate.annotations.Where


@Entity
@Table(name = "products")
@Where(clause = "is_deleted = false")
data class Products(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String = "",
    var category: String = "",
    var price: Double = 0.0,
    var quantity: Int = 0,
    val profit: Double = 0.0,
    val qrcode: String? = "",
    val position: String = "",
    var isDeleted: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    var provider: Provider? = null,



)

