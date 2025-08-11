package com.example.jeebapi.DTO

data class SalesDashboard(
    val mostSoldItems: List<Array<Any>>,
    val topProviders: List<Array<Any>>,
    val totalInvoices: Int,
    val dailyInvoices: List<Array<Any>>
)