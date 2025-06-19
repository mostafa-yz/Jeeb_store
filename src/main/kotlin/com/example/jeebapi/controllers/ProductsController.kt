package com.example.jeebapi.controllers

import com.example.jeebapi.DTO.Productdto
import com.example.jeebapi.services.ProductsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/products")
class ProductsController(
    private val productsService: ProductsService
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


    @DeleteMapping("delete/{id}")
    fun deleteProduct(@PathVariable("id") id: Long): ResponseEntity<Map<String, String>> {
        return try {
            productsService.deleteProduct(id)
            // Successfully deleted, return 200 OK with a message
            val responseBody = mapOf("message" to "Product with ID $id deleted successfully.")
            ResponseEntity.ok(responseBody)
        } catch (ex: ProductsService.ResourceNotFoundException) {
            // Return 404 Not Found if resource doesn't exist
            val errorBody = mapOf("error" to (ex.message ?: "Product not found."))
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody)
        } catch (ex: Exception) {
            // Catch other unexpected errors
            val errorBody = mapOf("error" to "An unexpected error occurred during product deletion.")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody)
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









}
