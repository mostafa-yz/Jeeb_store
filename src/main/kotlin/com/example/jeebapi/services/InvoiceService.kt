package com.example.jeebapi.services

import com.example.jeebapi.DTO.Invoicedto
import com.example.jeebapi.DTO.ItemDto
import com.example.jeebapi.mapper.InvoiceMapper
import com.example.jeebapi.models.InvoProducts
import com.example.jeebapi.models.Invoice
import com.example.jeebapi.models.Products
import com.example.jeebapi.models.Provider
import com.example.jeebapi.models.Transactions
import com.example.jeebapi.repository.CustomerRepository
import com.example.jeebapi.repository.InvoProRepository
import com.example.jeebapi.repository.InvoiceRepository
import com.example.jeebapi.repository.ProductsRepository
import com.example.jeebapi.repository.ProviderRepository
import com.example.jeebapi.repository.TransactionRepository
import com.example.jeebapi.repository.UserRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.Optional

@Service
class InvoiceService(
    private val invoiceRepository: InvoiceRepository,
    private val invoiceMapper: InvoiceMapper,
    private val userRepository: UserRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductsRepository,
    private val invo_items: InvoProRepository,
    private val transaction: TransactionRepository,
    private val productsService: ProductsService,
    private val providerRepository: ProviderRepository,
) {
    @Transactional
    fun createInvoice(request: Invoice) {

        val customerId = request.customer?.id ?: throw IllegalArgumentException("Customer is missing")
        val customer = customerRepository.findById(customerId)
            .orElseThrow { EntityNotFoundException("Customer not found with ID: $customerId") }

        val userId = request.user?.id ?: throw IllegalArgumentException("User is missing")
        val user = userRepository.findById(userId)
            .orElseThrow { EntityNotFoundException("User not found with ID: $userId") }

        val newInvoice = invoiceRepository.save(
            Invoice(
                buyer = request.buyer,
                status = request.status,
                date = request.date ?: LocalDateTime.now(),
                description = request.description,
                user = user,
                customer = customer,
            )
        )
        val productIds = request.items.map { it.id }
        val products = productRepository.findAllById(productIds)
        val productsById = products.associateBy { it.id }

        // Ensure all requested products were found
        if (productsById.size != productIds.size) {
            val foundIds = productsById.keys
            val notFoundIds = productIds.filterNot { foundIds.contains(it) }
            throw EntityNotFoundException("Products not found with IDs: $notFoundIds")
        }

        val invoiceItems = mutableListOf<InvoProducts>()
        val transactions = mutableListOf<Transactions>()

        request.items.forEach { itemDto ->
            val product = productsById[itemDto.id]!!

            invoiceItems.add(
                InvoProducts(
                    name = itemDto.name,
                    quantity = itemDto.quantity,
                    price = itemDto.price,
                    invoice = newInvoice,
                    product = product,
                    provider = product.provider

                )
            )
            productsService.updateAmount(itemDto.id, -itemDto.quantity.toInt())


            transactions.add(
                Transactions(
                    price = itemDto.price,
                    amount = itemDto.quantity.toInt(),
                    invoice = newInvoice,
                    product = product,
                    user = user,
                    provider = product.provider
                )
            )
        }


        invo_items.saveAll(invoiceItems)
        transaction.saveAll(transactions)

    }

    @Transactional
    fun getInvoices(): List<Invoicedto> {
        val invoices = invoiceRepository.findAll()
        val invoiceResponses = invoices.map { invo ->
            invoiceMapper.toDto(invo)
        }
        return invoiceResponses
    }

    fun findByCustomerId(id: Long): List<Invoicedto> {

        val invoices = invoiceRepository.findByCustomerId(id)

        val invoiceResponses = invoices.map { invo ->
            invoiceMapper.toDto(invo)
        }
        return invoiceResponses
    }

    fun findbyuserid(userId: Long): List<Invoicedto> {
        val invoices = invoiceRepository.findByUserId(userId)
        val invoiceResponses = invoices.map { invo ->
            invoiceMapper.toDto(invo)
        }
        return invoiceResponses
    }

    fun findbydateBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<Invoicedto> {
        val invoices = invoiceRepository.findByDateBetween(startDate, endDate)
        val invoiceResponses = invoices.map { invo ->
            invoiceMapper.toDto(invo)
        }
        return invoiceResponses
    }


    fun findbystatus(status: String): List<Invoicedto> {
        val invoices = invoiceRepository.findByStatus(status)
        val invoiceResponses = invoices.map { invo ->
            invoiceMapper.toDto(invo)
        }

        return invoiceResponses
    }


    fun update(request: Invoicedto) {
        // --- 1. Fetch Core Parent Entities ---
        val customer = customerRepository.findById(request.customerId ?: throw IllegalArgumentException("Customer ID is missing"))
            .orElseThrow { EntityNotFoundException("Customer not found with ID: ${request.customerId}") }

        val user = userRepository.findById(request.userId ?: throw IllegalArgumentException("User ID is missing"))
            .orElseThrow { EntityNotFoundException("User not found with ID: ${request.userId}") }

        // --- 2. Fetch the Existing Invoice to be Updated ---
        var actualInvoice = invoiceRepository.findById(request.id)
            .orElseThrow { ProductsService.ResourceNotFoundException("Invoice with ID ${request.id} not found") }

        // --- 3. Update Invoice Header Properties ---
        actualInvoice.apply {
            this.buyer = request.buyer
            this.status = request.status
            this.description = request.description
            this.user = user
            this.customer = customer
        }
        // Note: You don't need to save the invoice here. JPA's dirty checking within a
        // @Transactional context will automatically update it when the transaction commits.
        // However, we need a saved reference for new items, so `save` is okay.
        val savedInvoice = invoiceRepository.save(actualInvoice)

        // --- 4. Prepare for Item Processing ---
        // Create a mutable map of items currently in the database for this invoice.
        // We will remove items from this map as we process them. Anything left at the end
        // is an item that was removed by the user.
        val existingItemsMap = actualInvoice.items.associateBy { it.product?.id }.toMutableMap()

        val itemsToSave = mutableListOf<InvoProducts>()
        // The list of items to be deleted will be determined by what's left in existingItemsMap.
        val transactions = mutableListOf<Transactions>()
        for (itemDto in request.items) {
            val product = itemDto.productId?.let { productRepository.findById(it) }
                ?.orElseThrow { EntityNotFoundException("Product not found with ID: ${itemDto.productId}") }
                ?: throw IllegalArgumentException("Product ID is missing for item ${itemDto.name}")


            val provider: Provider? = itemDto.providerId?.let { providerId ->
                providerRepository.findById(providerId)
                    .orElseThrow { EntityNotFoundException("Provider not found with ID: $providerId") }
            }

            // Check if the item from the request already exists in the invoice
            val existingItem = existingItemsMap[itemDto.productId]

            if (existingItem != null) {
                // --- CASE 1: ITEM EXISTS -> UPDATE IT ---

                val quantityChange = itemDto.quantity - existingItem.quantity

                // *** FIX 1: Correct Stock Adjustment for Updates ***
                if (quantityChange.toDouble() != 0.0) {
                    productsService.updateAmount(product.id, -quantityChange.toInt())
                }

                existingItem.apply {
                    this.quantity = itemDto.quantity
                    this.price = itemDto.price
                    this.name = itemDto.name
                    this.provider = provider
                }




                itemsToSave.add(existingItem)
                existingItemsMap.remove(itemDto.productId)

            } else {
                // --- CASE 2: NEW ITEM -> ADD IT ---

                // Decrease product stock by the quantity of the new item.
                productsService.updateAmount(product.id, -itemDto.quantity.toInt())

                // Create the new invoice item entity
                itemsToSave.add(
                    InvoProducts(
                        name = itemDto.name,
                        quantity = itemDto.quantity,
                        price = itemDto.price,
                        invoice = savedInvoice,
                        provider = provider,
                        product = product
                    )
                )
              transactions.add(Transactions(
                  price = itemDto.price,
                  amount = itemDto.quantity.toInt(),
                  invoice = savedInvoice,
                  product = product,
                  provider = provider,
                  user = user
              ))













            }

        }

        // --- 6. Process Removed Items ---
        // Any items left in `existingItemsMap` were not in the incoming request,
        // which means they were removed by the user.
        val itemsToDelete = existingItemsMap.values.toList()

        if (itemsToDelete.isNotEmpty()) {
            for (itemToRemove in itemsToDelete) {
                productsService.updateAmount(itemToRemove.product?.id, itemToRemove.quantity.toInt())

                // Break the relationship with the invoice
                actualInvoice.items.remove(itemToRemove)
            }

            // Now delete from repository
            invo_items.deleteAll(itemsToDelete)
        }

        if (itemsToSave.isNotEmpty()) {
            invo_items.saveAll(itemsToSave)
            transaction.saveAll(transactions)
        }

    }


}