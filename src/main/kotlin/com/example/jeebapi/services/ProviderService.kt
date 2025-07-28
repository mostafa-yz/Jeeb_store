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
class ProviderService(private val providers: ProviderRepository) {
    private val logger = LoggerFactory.getLogger(ProviderService::class.java)


    fun getAll(): List<Providerdto> {
        return providers.findAllWithProducts().map { it.toDto() }
    }


    fun getById(id: Long): Providerdto {
        return providers.findWithProductsById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with ID $id not found") }
            .toDto()
    }

    fun getByPhoneNumber(phoneNumber: String): Providerdto {
        val provider = providers.findByPhonenumber(phoneNumber)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with phone number $phoneNumber not found") }
        return getById(provider.id)
    }

    @Transactional
    fun create(request: Providerdto): Providerdto {
        if (providers.findByPhonenumber(request.phonenumber).isPresent) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "A provider with phone number ${request.phonenumber} already exists.")
        }

        val provider = Provider(
            name = request.name,
            phonenumber = request.phonenumber,
            email = request.email,
            instagramid = request.instagramid,
            shabanumber = request.shabanumber,
            cardnumber = request.cardnumber
        )
        val savedProvider = providers.save(provider)
        return savedProvider.toDto()
    }

    @Transactional
    fun update(id: Long, request: Providerdto): Providerdto {
        val existingProvider = providers.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with ID $id not found") }

        val updatedProvider = existingProvider.copy(
            name = request.name,
            phonenumber = request.phonenumber,
            email = request.email,
            instagramid = request.instagramid,
            shabanumber = request.shabanumber,
            cardnumber = request.cardnumber
        )

        val savedProvider = providers.save(updatedProvider)
        return savedProvider.toDto()
    }

    @Transactional
    fun delete(id: Long) {
        if (!providers.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with ID $id not found")
        }
        providers.deleteById(id)
    }


    fun providerExists(productDto: Productdto): Provider? {
        val providerId = productDto.providerId ?: return null

        return providers.findById(providerId)
            .orElseThrow {
                logger.warn("Provider with ID {} not found during product operation.", providerId)
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Provider with ID $providerId not found"
                )
            }
    }
}

// Helper extension function to map a Provider entity to a Providerdto
private fun Provider.toDto(): Providerdto = Providerdto(
    id = this.id,
    name = this.name,
    phonenumber = this.phonenumber,
    email = this.email,
    instagramid = this.instagramid,
    shabanumber = this.shabanumber,
    cardnumber = this.cardnumber,
    products = this.products.map { product ->
        Productdto(
            id = product.id,
            name = product.name,
            category = product.category,
            price = product.price,
            quantity = product.quantity.toLong(),
            profit = product.profit,
            qrcode = product.qrcode,
            position = product.position,
            providerId = this.id
        )
    }
)


















