package com.example.jeebapi.models

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Where


@Entity
@Table(name = "providers")
@Where(clause = "is_deleted = false")
data class Provider(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String = "",
    val phonenumber: String ="",
    val email: String = "",
    val instagramid: String = "",
    val shabanumber :String = "",
    val cardnumber:String = "",
    var isDeleted: Boolean = false,

    @OneToMany(mappedBy = "provider", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @JsonBackReference
    val products: MutableList<Products> = mutableListOf(),


    @OneToMany(mappedBy = "provider", cascade = [CascadeType.ALL])
   val payments: MutableList<Payments> =mutableListOf()












)
