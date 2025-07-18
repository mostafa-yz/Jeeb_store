package com.example.jeebapi.services

import com.example.jeebapi.DTO.ActionType
import com.example.jeebapi.DTO.Productdto
import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.models.Products
import com.example.jeebapi.models.Provider
import com.example.jeebapi.repository.ProductsRepository
import com.example.jeebapi.repository.ProviderRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime
import java.util.UUID


@Service
class ProductsService(
    val productsRepository: ProductsRepository,
    private val providerRepository: ProviderRepository,
    private val providerService: ProviderService,
    private val stoServicelog: StoServicelog
) {


    private val logger = LoggerFactory.getLogger(ProductsService::class.java)


    fun getAllProducts(): List<Productdto> { // Using ProductResponse for consistency
        logger.info("ProductsService.getAllProducts() called.")
        val productsEntities = productsRepository.findAll()
        logger.info("Found {} products entities from repository.", productsEntities.size)

        val productResponses = productsEntities.map { productEntity ->

            Productdto(
                id = productEntity.id,
                name = productEntity.name,
                category = productEntity.category,
                price = productEntity.price,
                quantity = productEntity.quantity,
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
            quantity = productEntity.quantity,
            profit = productEntity.profit,
            qrcode = productEntity.qrcode,
            position = productEntity.position,
            providerId = productEntity.provider?.id
            // Ensure no 'provider' field is mapped if ProductResponse doesn't have it
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
            quantity = productEntity.quantity,
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
                quantity = productEntity.quantity,
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

        var providerEntity: Provider? = null
        if (createRequest.providerId != null) {
            providerEntity = providerRepository.findByIdOrNull(createRequest.providerId)
                ?: run {
                    logger.warn("Provider with ID {} not found for product creation.", createRequest.providerId)
                    throw ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Provider with ID ${createRequest.providerId} not found"
                    )
                }
        }
        val newProductEntity = Products(
            name = createRequest.name,
            category = createRequest.category,
            price = createRequest.price,
            quantity = createRequest.quantity,
            profit = createRequest.profit,
            qrcode = UUID.randomUUID().toString().replace("-", "").substring(0, 10),
            position = createRequest.position,
            provider = providerEntity
        )
        productsRepository.save(newProductEntity)

        val logEntry = Storagedto(
            quantity = createRequest.quantity,
            action = ActionType.ADD,
            reason = " update amount  '${createRequest.name}' (ID: ${createRequest.id})",
            date =  LocalDateTime.now(),
            product_id =newProductEntity.id,
            provider_id = providerEntity!!.id
        )

        stoServicelog.create(listOf(logEntry))

        return Productdto(
            id = createRequest.id,
            name = createRequest.name,
            category = createRequest.category,
            price = createRequest.price,
            quantity = createRequest.quantity,
            profit = createRequest.profit,
            qrcode = createRequest.qrcode,
            position = createRequest.position,
            providerId = createRequest.providerId
        )
    }

    @Transactional
    fun deleteProduct(id: Long) {
        // 1. Find the product to ensure it exists.
        val productToDelete = productsRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Product with ID $id not found and cannot be deleted.") }

        // 2. Create the single log DTO before deleting the product.
        val logEntry = Storagedto(
            quantity = productToDelete.quantity,
            action = com.example.jeebapi.DTO.ActionType.REMOVE,
            reason = "Deleted product '${productToDelete.name}' (ID: ${productToDelete.id})",
            date =  LocalDateTime.now(),
            product_id = productToDelete.id,
            provider_id = productToDelete.id
        )

       stoServicelog.create(listOf(logEntry))

        productsRepository.delete(productToDelete)
    }


    @Transactional
    fun updateProduct(updateRequest: Productdto) {
        productsRepository.getProductsById(updateRequest.id) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "product not found"
        )
        val provider = providerService.providerExists(updateRequest)
        val newProductEntity = Products(
            id = updateRequest.id,
            name = updateRequest.name,
            category = updateRequest.category,
            price = updateRequest.price,
            quantity = updateRequest.quantity,
            profit = updateRequest.profit,
            qrcode = updateRequest.qrcode,
            position = updateRequest.position,
            provider = provider
        )
        productsRepository.save(newProductEntity)
    }


    @Transactional
    fun updateAmount(productId: Long?, quantityChange: Int) {
        val product = productsRepository.findByIdOrNull(productId)
            ?: throw ResourceNotFoundException("Product with ID $productId not found")


        if (quantityChange < 0 && product.quantity < -quantityChange) {
            throw InsufficientStockException("Not enough stock for product '${product.name}'. Available: ${product.quantity}, Requested: ${-quantityChange}")
        }
        product.quantity += quantityChange
        try {
            productsRepository.save(product)
        } catch (ex: OptimisticLockingFailureException) {
            throw ConcurrencyConflictException("Product stock was updated concurrently. Please try again.", ex)
        }

        val logEntry = Storagedto(
            quantity = product.quantity,
            action = ActionType.RECHARGE,
            reason = " update amount  '${product.name}' (ID: ${product.id})",
            date =  LocalDateTime.now(),
            product_id = product.id,
            provider_id = product.id
        )

        stoServicelog.create(listOf(logEntry))






    }



    class ResourceNotFoundException(message: String) : RuntimeException(message)
}

class InsufficientStockException(message: String) : RuntimeException(message)
class ConcurrencyConflictException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)