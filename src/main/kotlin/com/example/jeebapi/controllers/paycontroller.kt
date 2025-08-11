package com.example.jeebapi.controllers
import com.example.jeebapi.DTO.Pay
import com.example.jeebapi.models.Payments
import com.example.jeebapi.services.paymentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController





@RestController
@RequestMapping("/payments")
class paycontroller(
    val paymentService: paymentService
) {


    @PostMapping
    fun createPayment(@RequestBody request: Pay): ResponseEntity<Pay> {
        val createdPayment = paymentService.createPayment(request)
        return ResponseEntity(createdPayment, HttpStatus.CREATED)
    }


    @GetMapping("/{id}")
    fun getPaymentById(@PathVariable id: Long): List<Pay> {
        val payment = paymentService.getPaymentsByProviderId(id)

        return  payment
    }


    @GetMapping()
   fun getAllPayments(): ResponseEntity<List<Pay>> {
        val payments = paymentService.getAllPayments()
        return ResponseEntity(payments, HttpStatus.OK)
    }


    @PutMapping("/{id}")
    fun updatePayment(@PathVariable id: Long, @RequestBody request: Pay): ResponseEntity<Pay> {
        return try {
            val updatedPayment = paymentService.updatePayment(id, request)
            ResponseEntity(updatedPayment, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/{id}")
    fun deletePaymentById(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            paymentService.deletePaymentById(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: NoSuchElementException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }


}