package com.example.jeebapi.controllers

import com.example.jeebapi.DTO.Invoicedto
import com.example.jeebapi.models.Invoice
import com.example.jeebapi.services.InvoiceService
import jakarta.websocket.server.PathParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/invoice")
class InvoiceControlelr(
    private val invoiceService: InvoiceService

) {


    @GetMapping("/getInvoice")
    fun getInvoice(): List<Invoicedto> {
        return  invoiceService.getInvoices()

    }

    @GetMapping("/getInvoice/{id}")
    fun get(@PathVariable  id: Long): List<Invoicedto>{
        val invoice = invoiceService.findByCustomerId(id)
        return invoice

    }



    @GetMapping("/getstatus/{status}")
    fun getInvoiceStatus(@PathVariable status: String): List<Invoicedto> {
        val invoice=invoiceService.findbystatus(status)
        return invoice

    }


    @GetMapping("/getuser/{id}")
    fun getbyuser(@PathVariable id: Long): List<Invoicedto> {
        val invoice=invoiceService.findbyuserid(id)
        return invoice

    }

    @PostMapping("/create")
    fun addInvoice(@RequestBody request: Invoice) {
        return  invoiceService.createInvoice(request)

    }

    @PutMapping("/update")
    fun updateInvoice(@RequestBody request: Invoicedto) {
        invoiceService.update(request)



    }
















}