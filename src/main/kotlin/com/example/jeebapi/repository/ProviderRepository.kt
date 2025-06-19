package com.example.jeebapi.repository

import com.example.jeebapi.models.Products
import com.example.jeebapi.models.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ProviderRepository : JpaRepository<Provider, Long> {




    @Query("SELECT p FROM Provider p WHERE p.phonenumber LIKE %:phone%")
    fun findByPhone(@Param("phone") phone: String): Optional<Provider>?



    @Query("SELECT DISTINCT p FROM Provider p LEFT JOIN FETCH p.products WHERE p.id = :id")
    fun findprovider(@Param("id") id: Long): Provider?





}


