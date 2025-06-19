package com.example.jeebapi.repository

import com.example.jeebapi.models.Customer
import org.hibernate.grammars.hql.HqlLexer.FROM
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {


    fun findByPhone(phone: String): Customer?




}