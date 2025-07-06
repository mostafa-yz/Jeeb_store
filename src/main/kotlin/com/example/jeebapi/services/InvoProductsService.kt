package com.example.jeebapi.services

import com.example.jeebapi.models.InvoProducts
import com.example.jeebapi.models.Invoice
import com.example.jeebapi.repository.InvoProRepository
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Service


@Service
class InvoProductsService(
    private val repository: InvoProRepository
) {


    fun create(request: InvoProducts): InvoProducts {

        return repository.save(request)


    }









}