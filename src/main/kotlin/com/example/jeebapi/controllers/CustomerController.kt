package com.example.jeebapi.controllers

import com.example.jeebapi.models.Customer
import com.example.jeebapi.services.CustomerService
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Optional

@RestController
@RequestMapping("/customer")
class CustomerController(
    private val customerService: CustomerService
) {


    @PostMapping("/create")
    fun create(@RequestBody customer: Customer): Customer {


        return customerService.create(customer)
    }


    @GetMapping("/getAll")
    fun getAll(): List<Customer> {
        return customerService.findAll()
    }




    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Optional<Customer?> {
        return customerService.findById(id)
    }


    // In your CustomerController
    @GetMapping("/phone/{phone}")
    fun getbyphone(@PathVariable phone: String): Customer? { // It's good practice to define the return type
        return customerService.findByPhone(phone) // Add "return" here
    }











    @PutMapping("/updatecustomer/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody customer: Customer
    ): ResponseEntity<Any> {
        return try {
            val product = customerService.update(customer)
            ResponseEntity.ok(product)
        } catch (e: ChangeSetPersister.NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Product not found"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Update failed", "details" to e.message))
        }
    }


    @DeleteMapping("/del/{id}")
    fun delCustomer(@PathVariable id: Long): ResponseEntity<Any> {
        return try {
            customerService.deleteById(id)
            ResponseEntity.ok(mapOf("message" to "Customer deleted successfully"))
        } catch (e: ChangeSetPersister.NotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf("error" to "Customer not found"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Delete failed", "details" to e.message))
        }
    }


}