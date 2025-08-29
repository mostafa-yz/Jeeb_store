package com.example.jeebapi.repository

import com.example.jeebapi.DTO.ActionType
import com.example.jeebapi.DTO.Productdto

import com.example.jeebapi.models.StorageLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface StorageRepository: JpaRepository<StorageLog, Long> {


    @Query("""
    SELECT new com.example.jeebapi.DTO.Productdto(
        p.id,
        p.name,
        p.category,
        p.price,
        SUM(s.quantity),
        p.profit,
        p.qrcode,
        p.position,
        p.provider.id
    )
    FROM Products p
    JOIN StorageLog s ON p.id = s.productId
    WHERE s.action IN :actions AND s.date BETWEEN :start AND :end

    GROUP BY p.id, p.name, p.category, p.price, p.profit, p.qrcode, p.position, p.provider.id
""")
    fun findLogsByActionsAndDateRange(
        actions: List<ActionType>,
        start: LocalDateTime?,
        end: LocalDateTime?
    ): List<Productdto>

}


