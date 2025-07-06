package com.example.jeebapi.repository

import com.example.jeebapi.DTO.TransactionDto
import com.example.jeebapi.models.Invoice
import com.example.jeebapi.models.Transactions
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface TransactionRepository : JpaRepository<Transactions, Long> {


    fun findByInvoice(invoice: Invoice): List<TransactionDto>


    fun findByInvoiceIdAndProductId(invoiceId: Long?, productId: Long?): Optional<Transactions?>?


    @Transactional
    @Modifying
    @Query("DELETE FROM Transactions t WHERE t.product.id = :productId AND t.invoice.id = :invoiceId")
    fun deletebyid(@Param("productId") productId: Long, @Param("invoiceId") invoiceId: Long)


    // Delete all transactions for an invoice
    fun deleteByInvoice(invoice: Invoice)

}