package com.example.jeebapi.controllers

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
class ProvidersController(
    private val providerService: ProviderService
) {

    //add provider
    @PostMapping("/create")
    fun create(@RequestBody provider: Provider): ResponseEntity<Any> {
        return try {
            providerService.create(provider)
            ResponseEntity.ok("provider saved")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating provider: ${e.message}")
        }
    }


    //find by phone
    @GetMapping("/findprovider/{phone}")
    fun findProvider(@PathVariable phone: String): ResponseEntity<Provider> {
        val provider = providerService.findProvider(phone)
        return if (provider != null) {
            ResponseEntity.ok(provider)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    //get by id
        @GetMapping("/provider/{id}")
    fun getProvider(@PathVariable id: Long): ResponseEntity<Any> {
        val provider = providerService.getProviderById(id)
        println("Found provider: $id")
        return ResponseEntity.ok(provider)

    }


    ///update
    @PutMapping("/update/{id}")
    fun updateProvider(@RequestBody provider: Provider): ResponseEntity<Any> {
        providerService.update(provider)
        return ResponseEntity.ok("provider updated")
    }




    @DeleteMapping("/remove/{id}")
    fun removeProvider(@PathVariable id: Long): ResponseEntity<Any> {
        providerService.deleteById(id)
        return ResponseEntity.ok("provider deleted")
    }





    @GetMapping("/getall")
    fun getAll(): List<Provider> {
       return providerService.getall()
    }











}