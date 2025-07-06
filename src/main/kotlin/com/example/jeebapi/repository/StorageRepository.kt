package com.example.jeebapi.repository

import com.example.jeebapi.models.ActionType
import com.example.jeebapi.models.Products
import com.example.jeebapi.models.storage_log
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime


@Repository
interface StorageRepository: JpaRepository<storage_log, Long> {



    fun findByProduct(product: Products): List<storage_log>
    fun findByAction(action: ActionType): List<storage_log>


    @Query("SELECT s FROM storage_log s WHERE s.action = :action AND s.date BETWEEN :start AND :end")
    fun findLogsByActionAndDateRange(
        action: ActionType,
        start: LocalDateTime,
        end: LocalDateTime
    ): List<storage_log>








}