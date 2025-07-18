package com.example.jeebapi.services

import com.example.jeebapi.DTO.ActionType
import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.models.Products
import com.example.jeebapi.repository.ProductsRepository
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.UUID


@Service
class RechargeService(
    val productsRepository: ProductsRepository,
    val stoServicelog: StoServicelog
) {

    fun upload(file: MultipartFile) {
        val workbook = XSSFWorkbook(file.inputStream)
        val sheet = workbook.getSheetAt(0) // Get the first sheet

        val products = mutableListOf<Products>()
        try {
            for (rowIndex in 1..sheet.lastRowNum) {
                val row = sheet.getRow(rowIndex)
                val id = row.getCell(0).numericCellValue.toLong()
                val name = row.getCell(1).toString()
                val category = row.getCell(2).toString()
                val price = row.getCell(3).numericCellValue
                val quantity = row.getCell(4).numericCellValue.toInt()
                products.add(
                    Products(
                        id, name, category, price, quantity
                    )
                )
            }
            validateRow(products)
            validate(products)


            workbook.close()
        } catch (e: Exception) {

        }


    }

    fun validate(products: MutableList<Products>) {
        val updatedProducts = mutableListOf<Products>()
        val storagelog=mutableListOf<Storagedto>()
        try {
            for (item in products) {
                val existing = productsRepository.findById(item.id)
                if (existing.isPresent) {
                    var existingProduct = existing.get()
                    existingProduct.name = item.name
                    existingProduct.category = item.category
                    existingProduct.price = item.price
                    existingProduct.quantity = item.quantity
                    updatedProducts.add(existingProduct)
                } else {
                    val newProductEntity = Products(
                        name = item.name,
                        category = item.category,
                        price = item.price,
                        quantity = item.quantity,
                        profit = item.profit,
                        qrcode = UUID.randomUUID().toString().replace("-", "").substring(0, 10),
                        position = item.position,
                        provider = item.provider
                    )

                    productsRepository.save(newProductEntity)
                    val storageLogDTO = Storagedto(
                        quantity =item.quantity,
                        action =ActionType.RECHARGE,
                        reason =" new product",
                        date = LocalDateTime.now(),
                        product_id =newProductEntity.id,
                        provider_id = newProductEntity.provider!!.id,
                    )
                    storagelog.add(storageLogDTO)
                }





            }

            stoServicelog.create(storagelog)
            productsRepository.saveAll(updatedProducts)
        } catch (e: Exception) {
            println("Error during product validation and save: ${e.message}")
            throw e
        }

    }

    data class ValidationResponse(
        val isValid: Boolean,
        val problems: List<String>
    )

    fun validateRow(products: List<Products>): ValidationResponse {
        val problems = mutableListOf<String>()
        for (item in products) {
            if (item.id <= 0) {
                problems.add("Invalid id: must be greater than 0. Got ${item.id}")
            }
            if (item.name.isBlank()) {
                problems.add("Invalid name: cannot be blank.")
            }
            if (item.category.isBlank()) {
                problems.add("Invalid category: cannot be blank.")
            }
            if (item.price < 0) {
                problems.add("Invalid price: must be non-negative. Got ${item.price}")
            }
            if (item.quantity < 0) {
                problems.add("Invalid quantity: must be non-negative. Got ${item.quantity}")
            }
        }

        return ValidationResponse(isValid = problems.isEmpty(), problems = problems)
    }


}









