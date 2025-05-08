package com.example.jeebapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Id


@Entity
@Table (name = "invoice")
data class Invoice(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val buyer: String = "",
    val status: String = "",
   //  val date: Date = Date(),
    val description: String = "",

    @OneToMany(mappedBy = "invoice", cascade = [CascadeType.ALL])
    val items: List<InvoProducts> = emptyList(),

    @OneToMany(mappedBy = "invoice", cascade = [CascadeType.ALL])
    val transaction: List<Transactions> = emptyList(),




    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne
    @JoinColumn(name = "customer_id")
    val customer: Customer,


    )
