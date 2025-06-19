package com.example.jeebapi.services

import com.example.jeebapi.models.Customer
import com.example.jeebapi.repository.CustomerRepository
import jakarta.transaction.Transactional
import org.springframework.data.crossstore.ChangeSetPersister
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.Optional

@Service
class CustomerService(private val customerRepository: CustomerRepository) {

    fun create(customer: Customer): Customer {
        val existing = customerRepository.findByPhone(customer.phone)
        if (existing != null) {
            throw IllegalArgumentException("Customer already exists with phone: ${customer.phone}")
        }
        return customerRepository.save(customer)
    }

    fun findAll(): List<Customer> {
        return customerRepository.findAll()
    }

    // Find customer by ID
    fun findById(id: Long): Optional<Customer?> {
        return customerRepository.findById(id)
    }

    // Find customer by phone (optional, included for completeness)
    fun findByPhone(phone: String): Customer? {
        return customerRepository.findByPhone(phone)
    }

    // Update customer
    @Transactional
    fun update(customer: Customer): Customer {
        if (!customerRepository.existsById(customer.id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")
        }
        return customerRepository.save(customer.copy(
            name = customer.name,
            phone = customer.phone
        ))
    }

    // Delete customer by ID
    @Transactional
    fun deleteById(id: Long) {
        if (!customerRepository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")
        }
        customerRepository.deleteById(id)
    }


}