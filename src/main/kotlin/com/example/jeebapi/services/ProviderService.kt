package com.example.jeebapi.services

import com.example.jeebapi.DTO.Productdto
import com.example.jeebapi.DTO.Providerdto
import com.example.jeebapi.models.Provider
import com.example.jeebapi.repository.ProviderRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.Long
import kotlin.String


@Service
class ProviderService(
    private val providers: ProviderRepository,


) {


    ///add new provider
    fun create(provider: Provider): Provider {
        val existing = providers.findByPhone(provider.phonenumber)

      return  providers.save(provider)
    }

    ///seacrh by phone numbger
    fun findProvider(phoneNumber: String): Provider? {
        return providers.findByPhone(phoneNumber)!!.orElse(null)
    }

    fun getall(): List<Provider> {
        return providers.findAll()

    }





    fun getProviderById(id: Long): Providerdto? {
        val providerEntity: Provider? = providers.findprovider(id)


        return providerEntity?.let { provider ->

            val productSummaries = provider.products.map { product ->
                Productdto(
                    id = product.id,
                    name = product.name,
                    category = product.category,
                    price = product.price,
                    quantity = product.quantity,
                    profit = product.profit,
                    qrcode = product.qrcode,
                    position = product.position,
                    providerId = id
                )
            }
            println("Fetched products: ${productSummaries.size} items for DTO mapping")


            Providerdto(
                id = provider.id,
                name = provider.name,
                phonenumber = provider.phonenumber,
                email = provider.email,
                instagramid = provider.instagramid,
                shabanumber = provider.shabanumber,
                cardnumber = provider.cardnumber,
                products = productSummaries // Include the list of ProductSummaryDto
            )
        }
    }


    ////update
    @Transactional
    fun update(provider: Provider): Provider? {
        if (!providers.existsById(provider.id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found")
        }
        return providers.save(
            provider.copy(
                provider.id,
                provider.phonenumber,
                provider.name,
                provider.phonenumber,
                instagramid = provider.instagramid,
                shabanumber = provider.shabanumber,
                cardnumber = provider.cardnumber,

                )
        )
    }


    // Delete customer by ID
    @Transactional
    fun deleteById(id: Long) {
        if (!providers.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "provider not found")
        }
        providers.deleteById(id)
    }






    private val logger = LoggerFactory.getLogger(ProductsService::class.java)

    fun providerExists(createRequest: Productdto): Provider? {
        var providerEntity: Provider? = null
        if (createRequest.providerId != null) {
            providerEntity = providers.findByIdOrNull(createRequest.providerId)
                ?: run {
                    logger.warn("Provider with ID {} not found for product creation.", createRequest.providerId)
                    throw ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Provider with ID ${createRequest.providerId} not found"
                    )
                }
        }

        return providerEntity
    }


















}