package com.example.jeebapi.repository

import com.example.jeebapi.DTO.Pay
import com.example.jeebapi.models.Payments
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository: JpaRepository<Payments, Long> {



      fun findByProvider_Id(providerId: Long): List<Payments>






}