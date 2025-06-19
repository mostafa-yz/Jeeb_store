package com.example.jeebapi.DTO

data class Providerdto (
    val id: Long,
    val name: String,
    val phonenumber: String,
    val email: String,
    val instagramid: String,
    val shabanumber: String,
    val cardnumber: String,
    val products: List<Productdto>
)
