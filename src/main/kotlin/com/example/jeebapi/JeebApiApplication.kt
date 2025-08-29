package com.example.jeebapi

import com.ibm.icu.util.TimeZone
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.Date

@SpringBootApplication
class JeebApiApplication {


	@PostConstruct
	fun init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tehran"))


	}

}
fun main(args: Array<String>) {
	runApplication<JeebApiApplication>(*args)


}