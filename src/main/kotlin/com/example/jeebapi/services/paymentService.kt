package com.example.jeebapi.services

import com.example.jeebapi.DTO.Pay
import com.example.jeebapi.models.Payments
import com.example.jeebapi.models.Provider
import com.example.jeebapi.repository.PaymentRepository
import com.example.jeebapi.repository.ProviderRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class paymentService(
    val paymentsRepository: PaymentRepository,
     val providerRepository: ProviderRepository
) {
    fun getAllPayments(): List<Pay> {
        println("Attempting to retrieve all payments and map to DTOs") // For debugging
        return paymentsRepository.findAll().map { it.toResponseDto() }

    }

    fun createPayment(request: Pay): Pay {
        println("Attempting to create payment from request: $request") // For debugging

        // Map DTO to entity. Find the Provider entity based on the DTO's providerId.
        val provider: Provider? = request.providerId?.let {
            providerRepository.findById(it)
                .orElseThrow { NoSuchElementException("Provider with ID ${it} not found.") }
        }

        val paymentToSave = Payments(
            date =LocalDateTime.now(),
            amount = request.amount,
            note = request.note,
            provider = provider
        )

        val savedPayment = paymentsRepository.save(paymentToSave)
        return savedPayment.toResponseDto()
    }





    fun getPaymentsByProviderId(providerId: Long): List<Pay> {
        val payments = paymentsRepository.findByProvider_Id(providerId)
        return payments.map { it.toResponseDto() } // Convert each Payments object to a DTO
    }
























    fun updatePayment(id: Long, request: Pay): Pay {
        println("Attempting to update payment with ID: $id using request: $request") // For debugging

        val existingPayment = paymentsRepository.findById(id)
            .orElseThrow { NoSuchElementException("Payment with ID $id not found for update.") }

        // Find the Provider entity based on the DTO's providerId.
        val provider: Provider? = request.providerId?.let {
            providerRepository.findById(it)
                .orElseThrow { NoSuchElementException("Provider with ID ${it} not found.") }
        }




        // Update the fields of the existing entity with the new values from the DTO.
        existingPayment.date = request.date
        existingPayment.amount = request.amount
        existingPayment.note = request.note
        existingPayment.provider = provider

        val updatedPayment = paymentsRepository.save(existingPayment)
        return updatedPayment.toResponseDto()
    }
//

    fun deletePaymentById(id: Long) {
        println("Attempting to delete payment with ID: $id") // For debugging
        if (paymentsRepository.existsById(id)) {
            paymentsRepository.deleteById(id)
        } else {
            throw NoSuchElementException("Payment with ID $id not found for deletion.")
        }
    }




    // Extension function to easily map a Payments entity to a DTO
    private fun Payments.toResponseDto(): Pay {
        return Pay(
            id = this.id,
            date = this.date,
            amount = this.amount,
            note = this.note,
            providerId = this.provider?.id
        )


    }



}