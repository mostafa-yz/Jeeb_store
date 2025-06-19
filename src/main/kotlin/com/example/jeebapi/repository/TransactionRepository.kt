package com.example.jeebapi.repository

import com.example.jeebapi.DTO.Invoicedto
import com.example.jeebapi.DTO.TransactionDto
import com.example.jeebapi.models.Invoice
import com.example.jeebapi.models.Products
import com.example.jeebapi.models.Transactions
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface TransactionRepository : JpaRepository<Transactions, Long> {


   fun findByInvoice(invoice: Invoice): List<TransactionDto>


    fun deleteByProductAndInvoice(product: Products, invoice: Invoice)

    // Delete all transactions for an invoice
    fun deleteByInvoice(invoice: Invoice)

}