package com.example.jeebapi.services


import com.example.jeebapi.DTO.ActionType
import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.models.StorageLog
import com.example.jeebapi.repository.ProductsRepository
import com.example.jeebapi.repository.StorageRepository
import org.springframework.stereotype.Service


@Service
class StoServicelog(
    private val storageRepository: StorageRepository,
    private val productsRepository: ProductsRepository

) {
    fun create(storageDTOs: List<Storagedto>) {
        // 1. Collect all unique product IDs from the incoming DTO list.
        val productIds = storageDTOs.map { it.product_id }.toSet()

        // 2. Fetch all the required products from the database in a SINGLE query.
        val productsMap = productsRepository.findAllById(productIds).associateBy { it.id }

        // 3. Create a list of all the storage_log entities to be saved.
        val logsToSave = storageDTOs.mapNotNull { dto ->
            // Find the product from our map (much faster than a DB call).
            val product = productsMap[dto.product_id]

            // If the product exists, create the log entry. If not, ignore this DTO.
            product?.let {
                StorageLog(
                    quantity = dto.quantity,
                    action = ActionType.ADD, // Corrected to uppercase
                    reason = dto.reason,
                    date = dto.date,
                    product = it, // 'it' refers to the found product
                    provider = it.provider
                )
            }
        }

        // 4. Save all the newly created log entities to the database in one batch operation.
        if (logsToSave.isNotEmpty()) {
            println("Saving ${logsToSave.size} storage log(s)")
            storageRepository.saveAll(logsToSave)
        }
    }


    fun getall(): List<Storagedto> {
        // 1. Fetch all the log entities from the database.
        val logEntities = storageRepository.findAll()

        // 2. Map the list of entities to a new list of DTOs.
        return logEntities.map { log ->
            Storagedto(
                id = log.id,
                quantity = log.quantity,
                action = log.action,
                reason = log.reason,
                date = log.date,
                product_id = log.product.id,
                provider_id = log.provider?.id ?: 0L
            )
        }
    }









}