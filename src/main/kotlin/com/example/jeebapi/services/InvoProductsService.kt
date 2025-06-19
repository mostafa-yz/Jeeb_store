package com.example.jeebapi.services

import com.example.jeebapi.models.InvoProducts
import com.example.jeebapi.models.Invoice
import com.example.jeebapi.repository.InvoProRepository
import org.springframework.stereotype.Service


@Service
class InvoProductsService(
    private val repository: InvoProRepository
) {


    fun create(request: InvoProducts): InvoProducts {

        return repository.save(request)


    }


}