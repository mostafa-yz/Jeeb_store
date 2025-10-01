package com.example.jeebapi.services

import com.example.jeebapi.DTO.ActionType
import com.example.jeebapi.DTO.Productdto
import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.models.Products
import com.example.jeebapi.repository.ProductsRepository
import com.example.jeebapi.repository.ProviderRepository
import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID


@Service
class ProductsService(
    val productsRepository: ProductsRepository,
    private val providerRepository: ProviderRepository,
    private val providerService: ProviderService,
    private val stoServicelog: StoServicelog
) {


    private val logger = LoggerFactory.getLogger(ProductsService::class.java)


    fun getAllProducts(): List<Productdto> { 
        logger.info("ProductsService.getAllProducts() called.")
        val productsEntities = productsRepository.findAll()
        logger.info("Found {} products entities from repository.", productsEntities.size)

        val productResponses = productsEntities.map { productEntity ->

            Productdto(
                id = productEntity.id,
                name = productEntity.name,
                category = productEntity.category,
                price = productEntity.price,
                quantity = productEntity.quantity.toLong(),
                profit = productEntity.profit,
                qrcode = productEntity.qrcode,
                position = productEntity.position,
                providerId = productEntity.provider?.id
            )
        }

        logger.info("Returning {} product responses.", productResponses.size)
        return productResponses
    }


    fun getProductByqrcode(barcode: String): Productdto {

        val productEntity: Products? = productsRepository.getProductByqrcode(barcode)

        // Handle the case where the product is not found
        if (productEntity == null) {
            logger.warn("Product with QR code '{}' not found.", barcode)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product with QR code '$barcode' not found")
        }
        logger.info("Found product with QR code '{}': {}", barcode, productEntity.name)


        val productResponse = Productdto(
            id = productEntity.id,
            name = productEntity.name,
            category = productEntity.category,
            price = productEntity.price,
            quantity = productEntity.quantity.toLong(),
            profit = productEntity.profit,
            qrcode = productEntity.qrcode,
            position = productEntity.position,
            providerId = productEntity.provider?.id
        )

        logger.info("Returning ProductResponse DTO for QR code '{}'.", barcode)
        return productResponse
    }


    fun getprobyname(name: String): Productdto {

        val productEntity: Products? = productsRepository.getProductsByName(name)

        if (productEntity == null) {
            logger.warn("Product with QR code '{}' not found.", name)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product with QR code '$name' not found")
        }
        val productResponse = Productdto(
            id = productEntity.id,
            name = productEntity.name,
            category = productEntity.category,
            price = productEntity.price,
            quantity = productEntity.quantity.toLong(),
            profit = productEntity.profit,
            qrcode = productEntity.qrcode,
            position = productEntity.position,
            providerId = productEntity.provider?.id

        )

        return productResponse
    }


    fun getproductbyCategory(category: String): List<Productdto> {
        val productEntity: List<Products> = productsRepository.getProductsByCategory(category)

        if (productEntity == null) {
            logger.warn("Product with QR code '{}' not found.", category)
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Product with QR code '$category' not found")
        }


        val productResponses = productEntity.map { productEntity ->
            Productdto(
                id = productEntity.id,
                name = productEntity.name,
                category = productEntity.category,
                price = productEntity.price,
                quantity = productEntity.quantity.toLong(),
                profit = productEntity.profit,
                qrcode = productEntity.qrcode,
                position = productEntity.position,
                providerId = productEntity.provider?.id
            )
        }

        return productResponses

    }

    @Transactional
    fun createProduct(createRequest: Productdto): Productdto {
        val providerEntity = createRequest.providerId?.let {
            providerRepository.findByIdOrNull(it)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with ID $it not found")
        } ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Provider ID is required")

        val newProductEntity = Products(
            name = createRequest.name,
            category = createRequest.category,
            price = createRequest.price,
            quantity = createRequest.quantity.toInt(),
            profit = createRequest.profit,
            qrcode = UUID.randomUUID().toString().replace("-", "").substring(0, 7),
            position = createRequest.position,
            provider = providerEntity
        )
        val savedProduct = productsRepository.save(newProductEntity)
        productsRepository.flush() 

        val logEntry = Storagedto(
            quantity = createRequest.quantity.toInt(),
            action = ActionType.ADD,
            reason = "افزودن  '${createRequest.name}' (ID: ${savedProduct.id})",
            date = LocalDateTime.now(),
            qr = savedProduct.qrcode,
            productId = savedProduct.id,
            providerId = providerEntity.id,
            providerName = providerEntity.name,
            productName = savedProduct.name,
        )
        stoServicelog.create(listOf(logEntry))

        return Productdto(
            id = savedProduct.id,
            name = createRequest.name,
            category = createRequest.category,
            price = createRequest.price,
            quantity = createRequest.quantity,
            profit = createRequest.profit,
            qrcode = savedProduct.qrcode,
            position = createRequest.position,
            providerId = providerEntity.id
        )
    }

    @Transactional
    fun deleteProduct(id: Long) {
        // 1. Find the product or throw an exception if it doesn't exist.
        val product = productsRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Product with ID '$id' not found.") }

        //2. Mark the product as deleted.
//
        val logEntry = Storagedto(
            quantity = product.quantity,
            action = ActionType.REMOVE,
            reason = "delete '${product.name}' (ID: ${product.id})",
            date = LocalDateTime.now(),
            qr = null,
            productId = product.id,
            providerId = product.provider?.id,
            providerName = product.provider?.name,
            productName = product.name,
        )
        stoServicelog.create(listOf(logEntry))
        product.isDeleted = true
        productsRepository.save(product)


    }

    @Transactional
    fun updateProduct(updateRequest: Productdto) {
        // 1. Fetch the existing product and store it in a variable
        val existingProduct = productsRepository.findById(updateRequest.id)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Product with ID ${updateRequest.id} not found")
            }

        // 2. Find the provider to associate with the product
        val provider = providerService.providerExists(updateRequest)

        // 3. Use copy() to create an updated instance from the existing one
        val updatedProduct = existingProduct.copy(
            name = updateRequest.name,
            category = updateRequest.category,
            price = updateRequest.price,
            quantity = updateRequest.quantity.toInt(),
            profit = updateRequest.profit,
            qrcode = updateRequest.qrcode,
            position = updateRequest.position,
            provider = provider // Assign the new provider reference
        )

        // 4. Save the updated entity
        productsRepository.save(updatedProduct)
    }


    @Transactional
    fun updateAmount(productId: Long?, quantityChange: Int) {
        val product = productsRepository.findByIdOrNull(productId)
            ?: throw ResourceNotFoundException("Product with ID $productId not found")
        if (quantityChange < 0 && product.quantity < -quantityChange) {
            throw InsufficientStockException("تعداد کافی از محصول موجود نیست '${product.name}'. Available: ${product.quantity}, Requested: ${-quantityChange}")
        }
        product.quantity += quantityChange
        val updatedProduct = productsRepository.save(product)


        try {
            val logEntry = Storagedto(
                quantity = quantityChange,
                action = ActionType.RECHARGE,
                reason = "recharge '${updatedProduct.name}' (ID: ${updatedProduct.id})",
                date = LocalDateTime.now(),
                qr = null,
                productId = updatedProduct.id,
                providerId = updatedProduct.provider?.id,
                providerName = updatedProduct.provider?.name,
                productName = updatedProduct.name,
            )
            stoServicelog.create(listOf(logEntry))
        } catch (ex: OptimisticLockingFailureException) {
            throw ConcurrencyConflictException("Product stock was updated concurrently. Please try again.", ex)
        }
    }

    @Transactional
    fun updateinvoice(productId: Long?, quantityChange: Int) {
        val product = productsRepository.findByIdOrNull(productId)
            ?: throw ResourceNotFoundException("Product with ID $productId not found")
        if (quantityChange < 0 && product.quantity < -quantityChange) {
            throw InsufficientStockException("Not enough stock for product '${product.name}'. Available: ${product.quantity}, Requested: ${-quantityChange}")
        }
        product.quantity += quantityChange
         productsRepository.save(product)
    }

    class ResourceNotFoundException(message: String) : RuntimeException(message)
}

class InsufficientStockException(message: String) : RuntimeException(message)
class ConcurrencyConflictException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
