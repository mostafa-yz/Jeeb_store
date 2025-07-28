package com.example.jeebapi.controllers

import com.example.jeebapi.DTO.Productdto
import com.example.jeebapi.DTO.Storagedto
import com.example.jeebapi.models.StorageLog
import com.example.jeebapi.services.ProductsService
import com.example.jeebapi.services.RechargeService
import com.example.jeebapi.services.StoServicelog
import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate


@RestController
@RequestMapping("/products")
class ProductsController(
    private val productsService: ProductsService,
    private val recharge: RechargeService,
    private val log : StoServicelog
) {


    @GetMapping("/getAll")
    fun getAllProducts(): List<Productdto> {
        val productResponses = productsService.getAllProducts()
        return productResponses
    }


    @GetMapping("/getbyqrcode/{qrcode}")
    fun getprobyqr(@PathVariable qrcode: String): Productdto {

        val responce = productsService.getProductByqrcode(qrcode)
        return responce
    }

    @GetMapping("/getbyname/{name}")
    fun getprobyname(@PathVariable name: String): Productdto {

        val responce = productsService.getprobyname(name)
        return responce
    }


    @GetMapping("/getbycategory/{category}")
    fun getbycategory(@PathVariable category: String): List<Productdto> {

        val responce = productsService.getproductbyCategory(category)
        return responce
    }

    @PostMapping("/create")
    fun createProduct(@RequestBody createRequest: Productdto): Productdto {

        val product = productsService.createProduct(createRequest)
        return product

    }






    @DeleteMapping("/delete/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Any> {
         val log = LoggerFactory.getLogger(ProductsController::class.java)
        return try {
            productsService.deleteProduct(id)
            ResponseEntity.ok().body(mapOf("message" to "Product with ID $id deleted successfully."))
        } catch (ex: Exception) {
            // This block will catch the hidden error from the database
            log.error("!!! FAILED TO DELETE PRODUCT $id, ROOT CAUSE: !!!", ex)

            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "Failed to delete product. Check server logs for the full error."))
        }
    }

    @PutMapping("/update")
    fun updateProduct(@RequestBody updateRequest: Productdto): ResponseEntity<Map<String, String>> {
        return try {
            productsService.updateProduct(updateRequest)
            // Successfully deleted, return 200 OK with a message
            val responseBody = mapOf("message" to "Product with ID ${updateRequest.id} updated successfully.")
            ResponseEntity.ok(responseBody)
        } catch (ex: ProductsService.ResourceNotFoundException) {
            // Return 404 Not Found if resource doesn't exist
            val errorBody = mapOf("error" to (ex.message ?: "Product not found."))
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody)
        } catch (ex: Exception) {
            // Catch other unexpected errors
            val errorBody = mapOf("error" to "An unexpected error occurred during product update.")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody)
        }
    }

    @PostMapping("/excel")
    fun uploadExcelFile(@RequestParam("file") file: MultipartFile): ResponseEntity<String> {
        return try {
            recharge.upload(file)
            ResponseEntity.ok("File uploaded and data saved successfully!")
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Failed to upload file: ${e.message}")
        }
    }



    @PutMapping("/upquan")
    fun updateQuantityByParam(@RequestParam id: Long, @RequestParam amount: Int): ResponseEntity<String> {
        try {
            productsService.updateAmount(id, amount)
            return ResponseEntity.ok("Product with ID $id updated successfully.")
        } catch (ex: Exception) {
            return ResponseEntity.badRequest().body(ex.message)
        }
    }


     @GetMapping("/log")
     fun getlog():List<Storagedto>{

             return  log.getAll()

     }



    @GetMapping("/by-date")
    fun getProductsByDate(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<List<Productdto>> {

        // Call the correct service method
        val products = log.findqrbyhistory(startDate, endDate)

        // Return the list with a 200 OK status
        return ResponseEntity.ok(products)
    }














}





