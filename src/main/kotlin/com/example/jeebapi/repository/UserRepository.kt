package com.example.jeebapi.repository

import com.example.jeebapi.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>{

    fun existsByEmail(@Param("email") email: String): Boolean

    fun findByEmail(email: String): User?

}


