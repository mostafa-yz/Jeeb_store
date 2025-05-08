package com.example.jeebapi

import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@SpringBootApplication
class JeebApiApplication

fun main(args: Array<String>) {
	runApplication<JeebApiApplication>(*args)




}
