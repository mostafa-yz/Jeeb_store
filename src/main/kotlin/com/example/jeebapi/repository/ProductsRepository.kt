package com.example.jeebapi.repository

import com.example.jeebapi.DTO.Productdto
import com.example.jeebapi.models.Products
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface ProductsRepository : JpaRepository<Products, Long> {


    fun getProductByqrcode(barcode: String): Products
    fun getProductsByName(name: String):Products
    fun getProductsByCategory(category: String): List<Products>
    fun getProductsById(id: Long): Products





}