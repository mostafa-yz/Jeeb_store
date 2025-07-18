package com.example.jeebapi.repository

import com.example.jeebapi.DTO.ActionType

import com.example.jeebapi.models.Products
import com.example.jeebapi.models.StorageLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface StorageRepository: JpaRepository<StorageLog, Long> {



    fun findByProduct(product: Products): List<StorageLog>
    fun findByAction(action: ActionType): List<StorageLog>


    @Query("SELECT s FROM StorageLog s WHERE s.action = :action AND s.date BETWEEN :start AND :end")
    fun findLogsByActionAndDateRange(
        action: ActionType,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<StorageLog>








}