package com.example.jeebapi.mapper

import com.example.jeebapi.DTO.Invoicedto
import com.example.jeebapi.DTO.ItemDto
import com.example.jeebapi.models.InvoProducts
import com.example.jeebapi.models.Invoice
import org.springframework.stereotype.Component
import kotlin.collections.flatten
import kotlin.collections.map

// InvoiceMapper (simplified for clarity, assuming ItemEntity has actual entity references)
@Component

 class InvoiceMapper {

    open fun toDto(invoice: Invoice): Invoicedto {
        return Invoicedto(
            id = invoice.id,
            buyer = invoice.buyer,
            status = invoice.status,
            date = invoice.date,
            description = invoice.description,
            items = invoice.items.map { toItemDto(it) },
            userId = invoice.user?.id,
            customerId = invoice.customer?.id
        )
    }

    open fun toItemDto(itemEntity: InvoProducts): ItemDto {
        return ItemDto(
            id = itemEntity.id,
            name = itemEntity.name,
            quantity = itemEntity.quantity,
            price = itemEntity.price,
            invoiceId = itemEntity.invoice?.id,
            productId = itemEntity.product?.id,
            providerId = itemEntity.provider?.id
        )
    }
}