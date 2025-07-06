package com.example.jeebapi.services


import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.models.ActionType
import com.example.jeebapi.models.storage_log
import com.example.jeebapi.repository.ProductsRepository
import com.example.jeebapi.repository.StorageRepository
import org.springframework.stereotype.Service


@Service
class StoServicelog(
    private val storageRepository: StorageRepository,
    private val productsRepository: ProductsRepository

) {



    fun create(products: List<Storagedto>) {


        for (item in products) {

            val pro = productsRepository.findById(item.product_id).get()
            val storage= storage_log(
                quantity = item.quantity,
                action = ActionType.add,
                reason = item.reason,
                date = item.date,
                product = pro,
                provider =pro.provider
            )
            println("storage log ${storage}")
            storageRepository.save(storage)

        }


    }


}