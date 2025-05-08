package com.example.jeebapi.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table


@Entity
@Table(name = "providers")
data class Provider(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    val phonenumber: String = "",
    val email: String = "",
    val instagramid: String = "",
    val shabanumber :String = "",
    val cardnumber:String = "",

    @OneToMany(mappedBy = "provider")
    val products: List<Products> = emptyList()
)
