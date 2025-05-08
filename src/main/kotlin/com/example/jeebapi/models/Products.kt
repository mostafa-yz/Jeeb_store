package com.example.jeebapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany

import jakarta.persistence.Table


@Entity
@Table(name = "products")
data class Products(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val category: String,
    val price: Double,
    val quantity: Int,
    val profit: Double,
    val qrcode: String,
    val position: String,
    @ManyToOne
    @JoinColumn(name = "provider_id")
    val provider: Provider,


//    @OneToMany(mappedBy = "products", cascade = [CascadeType.ALL])
//    val transaction: List<Transactions> = emptyList()



)

