package com.example.jeebapi.repository

import com.example.jeebapi.models.Invoice
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface InvoiceRepository: JpaRepository<Invoice, Long> {







    fun findByStatus(status: String): List<Invoice>

    @EntityGraph(attributePaths = ["items", "items.product", "items.provider", "user", "customer"])
    override fun findAll(): List<Invoice>


    // Find invoices by date range
    fun findByDateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Invoice>


    fun findByUserId(userId: Long): List<Invoice>


    fun findByCustomerId(customerId: Long): List<Invoice>









}