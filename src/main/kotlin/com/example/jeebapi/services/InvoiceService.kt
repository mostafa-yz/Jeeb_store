package com.example.jeebapi.services

import com.example.jeebapi.DTO.ActionType
import com.example.jeebapi.DTO.Invoicedto
import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.mapper.InvoiceMapper
import com.example.jeebapi.models.InvoProducts
import com.example.jeebapi.models.Invoice
import com.example.jeebapi.models.Transactions
import com.example.jeebapi.repository.*
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.TimeZone


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
    private val stoServicelog: StoServicelog

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
                date = LocalDateTime.now(),
                description = request.description,
                user = user,
                customer = customer,
                phoneNumber = request.phoneNumber,
                paymentmethod = request.paymentmethod,
            )

        )
        println("request output ${request.paymentmethod}")
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
            val product = productsById[itemDto.id]

            invoiceItems.add(
                InvoProducts(
                    name = itemDto.name,
                    quantity = itemDto.quantity,
                    price = itemDto.price,
                    invoice = newInvoice,
                    product = product,
                    provider = product?.provider

                )
            )
            productsService.updateinvoice(itemDto.id, -itemDto.quantity.toInt())


            transactions.add(
                Transactions(
                    price = itemDto.price,
                    quantity = itemDto.quantity.toInt(),
                    date = LocalDateTime.now(),
                    invoice = newInvoice,
                    product = product,
                    user = user,
                    provider = product?.provider
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

    @Transactional
    fun update(request: Invoicedto) {

        val customer =
            customerRepository.findById(request.customerId ?: throw IllegalArgumentException("Customer ID is missing"))
                .orElseThrow { EntityNotFoundException("Customer not found with ID: ${request.customerId}") }

        val user = userRepository.findById(request.userId ?: throw IllegalArgumentException("User ID is missing"))
            .orElseThrow { EntityNotFoundException("User not found with ID: ${request.userId}") }


        var actualInvoice = invoiceRepository.findById(request.id)
            .orElseThrow { ProductsService.ResourceNotFoundException("Invoice with ID ${request.id} not found") }


        actualInvoice.apply {
            this.buyer = request.buyer
            this.status = request.status
            this.description = request.description
            this.user = user
            this.customer = customer
            this.paymentmethod = request.paymentmethod
            this.phoneNumber = request.phonenumber
        }

        val savedInvoice = invoiceRepository.save(actualInvoice)


        val existingItemsMap = actualInvoice.items.associateBy { it.product?.id }.toMutableMap()
        val itemsToSave = mutableListOf<InvoProducts>()
        val transactions = mutableListOf<Transactions>()
        for (itemDto in request.items) {
            val product = itemDto.productId?.let { productRepository.findById(it) }
                ?.orElseThrow { EntityNotFoundException("Product not found with ID: ${itemDto.productId}") }
                ?: throw IllegalArgumentException("Product ID is missing for item ${itemDto.name}")


            // Check if the item from the request already exists in the invoice
            val existingItem = existingItemsMap[itemDto.productId]

            if (existingItem != null) {
                val quantityChange = itemDto.quantity - existingItem.quantity
                if (quantityChange.toDouble() != 0.0) {
                    productsService.updateinvoice(product.id, -quantityChange.toInt())
                }
                existingItem.apply {
                    this.quantity = itemDto.quantity
                    this.price = itemDto.price
                    this.name = itemDto.name
                    this.provider = provider

                }
                val existingtransaction = transaction.findByInvoiceIdAndProductId(savedInvoice.id, itemDto.productId!!)
                existingtransaction?.let {
                    if (it.isPresent) {
                        val transactionToUpdate = existingtransaction.get()
                        transactionToUpdate.quantity = itemDto.quantity
                        transactionToUpdate.price = itemDto.price
                    }
                }
                itemsToSave.add(existingItem)
                existingItemsMap.remove(itemDto.productId)
            } else {


                itemsToSave.add(
                    InvoProducts(
                        name = itemDto.name,
                        quantity = itemDto.quantity,
                        price = itemDto.price,
                        invoice = savedInvoice,
                        product = product,
                        provider = product.provider,
                    )
                )



                transactions.add(
                    Transactions(
                        price = itemDto.price,
                        quantity = itemDto.quantity.toInt(),
                        date = LocalDateTime.now(),
                        invoice = savedInvoice,
                        product = product,
                        user = user,
                        provider = product.provider,
                    )
                )

            }


        }

        // --- 6. Process Removed Items ---
        val itemsToDelete = existingItemsMap.values.toList()
        if (itemsToDelete.isNotEmpty()) {
            for (itemToRemove in itemsToDelete) {
                val product = itemToRemove.product
                    ?: throw IllegalArgumentException("Product not found for item ${itemToRemove.id}")

                // 1. Reverse inventory
                productsService.updateinvoice(
                    product.id,
                    itemToRemove.quantity.toInt(),
                )

                // 2. Create log entry
                val logEntry = Storagedto(
                    quantity = itemToRemove.quantity.toInt(),
                    action = ActionType.ADD,
                    reason = "حذف آیتم از فاکتور و بازگردانی موجودی '${product.name}' (Item ID: )",
                    date = LocalDateTime.now(),
                    qr = product.qrcode,
                    productId = product.id,
                    providerId = product.provider?.id,
                    providerName = product.provider?.name,
                    productName = product.name,
                )

                stoServicelog.create(listOf(logEntry))  // save log

                // 3. Delete invoice item and transaction
                actualInvoice.items.remove(itemToRemove)
                invo_items.delete(product.id, savedInvoice.id)
                transaction.deletebyid(product.id, savedInvoice.id)
            }
        }

        if (itemsToSave.isNotEmpty()) {
            invo_items.saveAll(itemsToSave)
            transaction.saveAll(transactions)
        }
    }


    fun delete(id: Long) {

        val invoice = invoiceRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Invoice with ID $id not found") }

        val invoiceItems = invo_items.findByInvoiceId(id)

        for (item in invoiceItems) {
            val product = item.productId?.let { productRepository.findById(it.toLong()) }
                ?.orElseThrow { IllegalArgumentException("Product with ID ${item.productId} not found") }
                ?: continue  // skip if productId was null

            val logEntry = Storagedto(
                quantity = item.quantity.toInt(),
                action = ActionType.ADD,
                reason = "حذف آیتم از فاکتور و بازگردانی موجودی '${item.name}' )",
                date = LocalDateTime.now(),
                qr = product.qrcode,
                productId = item.productId,   // FIXED: use productId, not item.id
                providerId = item.providerId,
                providerName = product.provider?.name,
                productName = item.name,
            )

            productsService.updateinvoice(item.productId, item.quantity.toInt())
            stoServicelog.create(listOf(logEntry))
        }

        transaction.deleteByInvoiceId(id)
        invo_items.deleteByInvoiceId(id)
        invoice.isDeleted = true
        invoiceRepository.save(invoice)
    }



}