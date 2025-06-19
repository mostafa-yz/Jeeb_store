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
import java.time.LocalDateTime
import java.util.Date


@Entity
@Table(name = "invoice")
 class Invoice(

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
   var buyer: String ="",
   var status: String ="",
   var date: LocalDateTime =LocalDateTime.now(),
   var description: String? ="",


   @OneToMany(mappedBy = "invoice", cascade = [CascadeType.ALL], orphanRemoval = true)
   var items: MutableList<InvoProducts> = mutableListOf(),


   @OneToMany(mappedBy = "invoice", cascade = [CascadeType.ALL], orphanRemoval = true)
    val transaction: MutableList<Transactions> =mutableListOf(),

   @ManyToOne
    @JoinColumn(name = "user_id")
   var user: User?= null,

   @ManyToOne
    @JoinColumn(name = "customer_id")
   var customer: Customer?= null,
    )
