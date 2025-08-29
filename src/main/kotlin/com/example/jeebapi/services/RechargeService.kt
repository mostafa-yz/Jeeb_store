package com.example.jeebapi.services

import com.example.jeebapi.DTO.ActionType
import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.models.Products
import com.example.jeebapi.repository.ProductsRepository
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


data class ProductExcelRow(
    val rowIndex: Int,
    val id: Long?,
    val name: String?,
    val category: String?,
    val price: Double?,
    val quantity: Int?,
    val profit: Double?,
    val providerid:Long,

)

@Service
class RechargeService(
    val productsRepository: ProductsRepository,
    val stoServicelog: StoServicelog,
    val provider : ProviderService,
) {
    // Add a professional logger
    private val log = LoggerFactory.getLogger(RechargeService::class.java)

    fun upload(file: MultipartFile): String {
        val workbook = XSSFWorkbook(file.inputStream)
        val sheet = workbook.getSheetAt(0)
        val dataFormatter = DataFormatter()

        val excelRows = mutableListOf<ProductExcelRow>()

        for (rowIndex in 1..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            getCellAsLong(row.getCell(6), dataFormatter)?.let {
                excelRows.add(
                    ProductExcelRow(
                        rowIndex = rowIndex + 1,
                        id = getCellAsLong(row.getCell(0), dataFormatter),
                        name = getCellAsString(row.getCell(1), dataFormatter),
                        category = getCellAsString(row.getCell(2), dataFormatter),
                        price = getCellAsDouble(row.getCell(3), dataFormatter),
                        quantity = getCellAsInt(row.getCell(4), dataFormatter),
                        profit = getCellAsDouble(row.getCell(5), dataFormatter),
                        providerid = it,
                    )
                )
            }
        }
        workbook.close()
        log.info("Parsed ${excelRows.size} rows from Excel file: ${file.originalFilename}")

        // Process the data and return the result message
        return validateAndSave(excelRows)
    }

    private fun validateAndSave(excelRows: List<ProductExcelRow>): String {
        val validationProblems = validateRows(excelRows)
        if (validationProblems.isNotEmpty()) {
            throw IllegalArgumentException("Invalid data in Excel file: ${validationProblems.joinToString()}")
        }

        val productsToSave = mutableListOf<Products>()
        excelRows.forEach { row ->
            if (row.id != null) {
                productsRepository.findById(row.id).ifPresentOrElse({ dbProduct ->
                    dbProduct.price = row.price!!
                    dbProduct.quantity += row.quantity!!
                    productsToSave.add(dbProduct)
                }, {
                    throw NoSuchElementException("Product with ID ${row.id} from row ${row.rowIndex} not found.")
                })
            } else {
                val provider = provider.getbyid(row.providerid)
                productsToSave.add(Products(
                    name = row.name!!,
                    category = row.category!!,
                    price = row.price!!,
                    quantity = row.quantity!!,
                    profit = 0.0,
                    position = "Default Position",
                    provider = provider,
                    qrcode = UUID.randomUUID().toString().replace("-", "").substring(0, 10)
                ))
            }
        }

        if (productsToSave.isEmpty()) {
            val message = "No valid products found to save or update."
            log.warn(message)
            return message
        }
        log.info("Preparing to save or update ${productsToSave.size} product(s).")

        val savedProducts = productsRepository.saveAll(productsToSave)
        log.info("Successfully saved or updated ${savedProducts.size} product(s) in the database.")

        val logEntries = savedProducts.map { savedProduct ->
            val originalRow = excelRows.find { row -> if (row.id != null) row.id == savedProduct.id else row.name == savedProduct.name }
            val quantityChange = originalRow?.quantity ?: 0
            val isUpdate = originalRow?.id != null

            Storagedto(
                quantity = quantityChange,
                action = if (isUpdate) ActionType.RECHARGE else ActionType.ADD,
                reason = if (isUpdate) "شارژ مجدد '${savedProduct.name}' (ID: ${savedProduct.id}) from Excel"
                else "افزودن محصول جدید '${savedProduct.name}' (ID: ${savedProduct.id}) from Excel",
                date = LocalDateTime.now(),
                qr = savedProduct.qrcode,
                productId = savedProduct.id,
                productName = savedProduct.name.toString(),
                providerId = savedProduct.provider?.id,
                providerName = savedProduct.provider?.name
            )
        }
        log.info("Created ${logEntries.size} log entries to be saved.")

        if (logEntries.isNotEmpty()) {
            stoServicelog.create(logEntries)
            log.info("Successfully sent ${logEntries.size} log entries to the logging service.")
        } else {
            log.warn("No log entries were generated after saving products.")
        }

        return "Successfully processed file. Saved or updated ${savedProducts.size} products and created ${logEntries.size} log entries."
    }

    private fun getCellAsString(cell: Cell?, formatter: DataFormatter): String? = formatter.formatCellValue(cell).trim().takeIf { it.isNotBlank() }
    private fun getCellAsLong(cell: Cell?, formatter: DataFormatter): Long? = getCellAsString(cell, formatter)?.toLongOrNull()
    private fun getCellAsInt(cell: Cell?, formatter: DataFormatter): Int? = getCellAsString(cell, formatter)?.toIntOrNull()
    private fun getCellAsDouble(cell: Cell?, formatter: DataFormatter): Double? = getCellAsString(cell, formatter)?.toDoubleOrNull()

// In RechargeService.kt

    // In RechargeService.kt

    private fun validateRows(rows: List<ProductExcelRow>): List<String> {
        val problems = mutableListOf<String>()
        rows.forEach { row ->
            // This check now ensures the ID is a valid, positive number for an update.
            if (row.id != null && row.id > 0) {
                // --- Validation for EXISTING products ---
                if (row.price == null || row.price < 0) {
                    problems.add("Row ${row.rowIndex}: Price must be a non-negative number for update.")
                }
                if (row.quantity == null || row.quantity <= 0) {
                    problems.add("Row ${row.rowIndex}: Quantity must be a positive number for update.")
                }
            }
            else {
                // --- Validation for NEW products (ID is blank or 0) ---
                if (row.name.isNullOrBlank()) {
                    problems.add("Row ${row.rowIndex}: Product name is missing for new product.")
                }
                if (row.category.isNullOrBlank()) {
                    problems.add("Row ${row.rowIndex}: Category is missing for new product.")
                }
                if (row.price == null || row.price < 0) {
                    problems.add("Row ${row.rowIndex}: Price must be a non-negative number for new product.")
                }
                if (row.quantity == null || row.quantity <= 0) {
                    problems.add("Row ${row.rowIndex}: Quantity must be a positive number for new product.")
                }
            }
        }
        return problems
    }
}