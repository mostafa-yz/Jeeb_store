package com.example.jeebapi.repository

import com.example.jeebapi.models.Products
import com.example.jeebapi.models.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ProviderRepository : JpaRepository<Provider, Long> {




    fun findByPhonenumber(phonenumber: String): Optional<Provider>

    @Query("SELECT p FROM Provider p LEFT JOIN FETCH p.products WHERE p.id = :id")
    fun findWithProductsById(@Param("id") id: Long): Optional<Provider>

    // ✅ This query efficiently fetches all providers with their products, avoiding N+1 issues.
    @Query("SELECT DISTINCT p FROM Provider p LEFT JOIN FETCH p.products")
    fun findAllWithProducts(): List<Provider>



}


