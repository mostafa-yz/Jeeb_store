package com.example.jeebapi.controllers

import com.example.jeebapi.DTO.Providerdto
import com.example.jeebapi.models.Provider
import com.example.jeebapi.services.ProviderService
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
@RequestMapping("/providers")
class ProviderController(private val providerService: ProviderService) {

    /**
     * GET /api/providers
     * Returns a list of all providers with their products.
     */
    @GetMapping
    fun getAllProviders(): ResponseEntity<List<Providerdto>> {
        val providers = providerService.getAll()
        return ResponseEntity.ok(providers)
    }

    /**
     * GET /api/providers/{id}
     * Returns a single provider by its ID, including its products.
     */
    @GetMapping("/{id}")
    fun getProviderById(@PathVariable id: Long): ResponseEntity<Providerdto> {
        val provider = providerService.getById(id)
        return ResponseEntity.ok(provider)
    }

    /**
     * GET /api/providers/by-phone/{phoneNumber}
     * Returns a single provider by its phone number, including its products.
     */
    @GetMapping("/by-phone/{phoneNumber}")
    fun getProviderByPhoneNumber(@PathVariable phoneNumber: String): ResponseEntity<Providerdto> {
        val provider = providerService.getByPhoneNumber(phoneNumber)
        return ResponseEntity.ok(provider)
    }

    /**
     * POST /api/providers
     * Creates a new provider.
     */
    @PostMapping
    fun createProvider(@RequestBody request: Providerdto): ResponseEntity<Providerdto> {
        val createdProvider = providerService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProvider)
    }

    /**
     * PUT /api/providers/{id}
     * Updates an existing provider.
     */
    @PutMapping("/{id}")
    fun updateProvider(@PathVariable id: Long, @RequestBody request: Providerdto): ResponseEntity<Providerdto> {
        val updatedProvider = providerService.update(id, request)
        return ResponseEntity.ok(updatedProvider)
    }

    /**
     * DELETE /api/providers/{id}
     * Deletes a provider by its ID.
     */
    @DeleteMapping("/{id}")
    fun deleteProvider(@PathVariable id: Long): ResponseEntity<Unit> {
        providerService.delete(id)
        return ResponseEntity.noContent().build()
    }
}











