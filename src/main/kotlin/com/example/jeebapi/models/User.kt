package com.example.jeebapi.models

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String = "",

    val email: String = "",

    val password: String = "",
    val accesslevel: Int = 0,


    @OneToMany(mappedBy = "user")
    val invoice: List<Invoice> = emptyList(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val transaction: List<Transactions> = emptyList(),


    )