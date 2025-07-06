package com.example.jeebapi.DTO


import java.time.LocalDateTime

data class Storagedto(

    val id: Long = 0,
    val quantity: Int,
    val action: ActionType,
    val reason: String? = "",
    var date: LocalDateTime?,
    var product_id: Long,
    var provider_id: Long,

    )


  enum class ActionType {
    RECHARGE, REMOVE,ADD
}