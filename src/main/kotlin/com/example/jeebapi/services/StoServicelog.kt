package com.example.jeebapi.services

import com.example.jeebapi.DTO.ActionType
import com.example.jeebapi.DTO.Productdto
import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.models.StorageLog
import com.example.jeebapi.repository.ProductsRepository
import com.example.jeebapi.repository.ProviderRepository
import com.example.jeebapi.repository.StorageRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset


@Service
class StoServicelog(
    private val storageRepository: StorageRepository,
    private val productsRepository: ProductsRepository,
    private val providerRepository: ProviderRepository
) {

    @Transactional
    fun create(storageDTOs: List<Storagedto>) {
        val productIds = storageDTOs.mapNotNull { it.productId }.toSet()
        val providerIds = storageDTOs.mapNotNull { it.providerId }.toSet()

        val productsMap = productsRepository.findAllById(productIds).associateBy { it.id }
        val providersMap = providerRepository.findAllById(providerIds).associateBy { it.id }

        val logsToSave = storageDTOs.mapNotNull { dto ->
            val product = dto.productId?.let { productsMap[it] }
            if (product == null) {
                println("Warning: Product with ID ${dto.productId} not found. Skipping.")
                return@mapNotNull null
            }


            val provider = dto.providerId?.let { providersMap[it] }

            StorageLog(
                quantity = dto.quantity,
                action = dto.action,
                reason = dto.reason,
                date = LocalDateTime.now(),
                productId = product.id,
                productName = product.name,
                providerId = provider?.id,
                providerName = provider?.name
            )



        }

        if (logsToSave.isNotEmpty()) {
            println("Saving ${logsToSave.size} storage log(s)")
            storageRepository.saveAll(logsToSave)
        }
    }



















    fun getAll(): List<Storagedto> {
        return storageRepository.findAll().map { log ->
            Storagedto(
                id = log.id,
                quantity = log.quantity,
                action = log.action,
                reason = log.reason,
                date = log.date,
                qr = null,
                productId = log.productId,
                providerId = log.providerId,
                productName = log.productName,
                providerName = log.providerName
            )
        }
    }






    fun findqrbyhistory(start: LocalDate, end: LocalDate): List<Productdto> {
        val startDateTime = start.atStartOfDay()

        val endDateTime = end.atTime(23, 59, 59, 999999999)
        val actionsToFind = listOf(ActionType.RECHARGE, ActionType.ADD)
        println("the date$startDateTime - $endDateTime")
        return storageRepository.findLogsByActionsAndDateRange(actionsToFind, startDateTime, endDateTime)
    }







}
