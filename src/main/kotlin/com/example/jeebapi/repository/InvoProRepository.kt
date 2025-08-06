package com.example.jeebapi.repository

import com.example.jeebapi.DTO.ItemDto
import com.example.jeebapi.models.InvoProducts
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface InvoProRepository : JpaRepository<InvoProducts, Long> {



    @Modifying
    @Query("DELETE FROM InvoProducts t WHERE t.product.id = :productId AND t.invoice.id = :invoiceId")
    fun delete(@Param("productId") productId: Long?, @Param("invoiceId") invoiceId: Long)

    fun findByInvoiceId(invoiceId: Long): List<ItemDto>



    @Transactional
    fun deleteByInvoiceId(invoiceId: Long)








}