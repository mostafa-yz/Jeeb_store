package com.example.jeebapi.repository

import com.example.jeebapi.models.InvoProducts
import com.example.jeebapi.models.Invoice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface InvoProRepository : JpaRepository<InvoProducts, Long> {







}