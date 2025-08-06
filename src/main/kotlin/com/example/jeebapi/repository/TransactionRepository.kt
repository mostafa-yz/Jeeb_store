package com.example.jeebapi.repository

import com.example.jeebapi.DTO.ProviderPaymentDTO
import com.example.jeebapi.DTO.ReprotDTO
import com.example.jeebapi.DTO.TransactionDto
import com.example.jeebapi.models.Transactions
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*


@Repository
interface TransactionRepository : JpaRepository<Transactions, Long> {





    fun findByInvoiceIdAndProductId(invoiceId: Long?, productId: Long?): Optional<Transactions?>?


    @Transactional
    @Modifying
    @Query("DELETE FROM Transactions t WHERE t.product.id = :productId AND t.invoice.id = :invoiceId")
    fun deletebyid(@Param("productId") productId: Long, @Param("invoiceId") invoiceId: Long)


    // Delete all transactions for an invoice
    @Transactional
    fun deleteByInvoiceId(invoiceId: Long)


    @Query(value = """
    SELECT
        pr.name AS providerName,
        CAST(SUM(t.price * t.quantity * (p.profit / 100)) AS DECIMAL(19, 2)) AS totalOurEarnings,
        CAST(SUM(t.price * t.quantity * (1 - (p.profit / 100))) AS DECIMAL(19, 2)) AS totalPaidToProvider,
        CAST(SUM(t.price * t.quantity) AS DECIMAL(19, 2)) AS totalTransactionValue,
        p.name AS productName,
        CAST(SUM(t.quantity) AS SIGNED) AS totalQuantitySold
    FROM
        transactions AS t
    JOIN
        products AS p ON t.product_id = p.id
    JOIN
        providers AS pr ON t.provider_id = pr.id
    WHERE
        t.date >= STR_TO_DATE(?1, '%Y-%m-%d %H:%i:%s') AND t.date < STR_TO_DATE(?2, '%Y-%m-%d %H:%i:%s')
    GROUP BY
        pr.id, pr.name, p.id, p.name
    ORDER BY
        pr.name, p.name
""", nativeQuery = true)
    fun ProviderSummary(startDate: Instant, endDate: Instant): List<ReprotDTO>




    @Query(value = """
        SELECT
            pr.name AS providerName,
            CAST(SUM(t.price * t.quantity * (p.profit / 100)) AS DECIMAL(19, 2)) AS totalOurEarnings,
            CAST(SUM(t.price * t.quantity * (1 - (p.profit / 100))) AS DECIMAL(19, 2)) AS totalPaidToProvider,
            CAST(SUM(t.price * t.quantity) AS DECIMAL(19, 2)) AS totalTransactionValue,
            p.name AS productName,
            CAST(SUM(t.quantity) AS SIGNED) AS totalQuantitySold
        FROM
            transactions AS t
        JOIN
            products AS p ON t.product_id = p.id
        JOIN
            providers AS pr ON t.provider_id = pr.id
        WHERE
            t.date >= STR_TO_DATE(:startDate, '%Y-%m-%d %H:%i:%s') AND t.date < STR_TO_DATE(:endDate, '%Y-%m-%d %H:%i:%s')
            AND pr.id = :providerId
        GROUP BY
            pr.id, pr.name, p.id, p.name
        ORDER BY
            pr.name, p.name
    """, nativeQuery = true)
    fun getProviderSummaryById(
        @Param("startDate") startDate: Instant,
        @Param("endDate") endDate: Instant,
        @Param("providerId") providerId: Long
    ): List<ReprotDTO>





    @Query(value = """
        SELECT
            pr.name AS providerName,
            pr.email AS providerEmail,
            pr.shabanumber AS providerShabaNumber,
            pr.cardnumber AS providerCardNumber,
            CAST(SUM(t.price * t.quantity * (1 - (p.profit / 100))) AS DECIMAL(19, 2)) AS totalPaidToProvider
        FROM
            transactions AS t
        JOIN
            products AS p ON t.product_id = p.id
        JOIN
            providers AS pr ON t.provider_id = pr.id
        WHERE
            t.date >= STR_TO_DATE(:startDate, '%Y-%m-%d %H:%i:%s')
            AND t.date < STR_TO_DATE(:endDate, '%Y-%m-%d %H:%i:%s')
        GROUP BY
            pr.id, pr.name, pr.email, pr.shabanumber, pr.cardnumber
        ORDER BY
            pr.name
    """, nativeQuery = true)
    fun getProviderPaymentsSummary(
        @Param("startDate") startDate: Instant,
        @Param("endDate") endDate: Instant
    ): List<ProviderPaymentDTO>











}