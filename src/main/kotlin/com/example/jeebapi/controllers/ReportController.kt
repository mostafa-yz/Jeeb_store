package com.example.jeebapi.controllers

import com.example.jeebapi.services.ReportService
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.format.DateTimeParseException





@RestController
@RequestMapping("/reports")
class ReportController(
    private val reportService: ReportService
) {
    @GetMapping("/zip")
    fun downloadReportsZip(
        @RequestParam("startDate") startDate: String,
        @RequestParam("endDate") endDate: String,
        @RequestParam("providerId", required = false) providerId: Long? // Add the new optional parameter
    ): ResponseEntity<ByteArray> {
        return try {
            val startInstant = Instant.parse(startDate)
            val endInstant = Instant.parse(endDate)


            val zipFileBytes = reportService.generateAndZipAllReports(startInstant, endInstant, providerId)

            val headers = HttpHeaders().apply {
                contentDisposition = ContentDisposition.attachment()
                    .filename("provider_reports.zip")
                    .build()
            }

            ResponseEntity.ok()
                .headers(headers)
                .contentLength(zipFileBytes.size.toLong())
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(zipFileBytes)

        } catch (e: DateTimeParseException) {
            println("Failed to parse date: startDate=$startDate, endDate=$endDate, Error: ${e.message}")
            ResponseEntity.badRequest().body("Invalid date format. Please use ISO 8601 (e.g., YYYY-MM-DDTHH:mm:ssZ)".toByteArray())
        }
    }







    @GetMapping("/excel")
    fun downloadPaymentsExcel(
        @RequestParam("startDate") startDate: String,
        @RequestParam("endDate") endDate: String
    ): ResponseEntity<ByteArray> {
        return try {
            val startInstant = Instant.parse(startDate)
            val endInstant = Instant.parse(endDate)

            val excelBytes = reportService.generateExcelReport(startInstant, endInstant)

            val headers = HttpHeaders().apply {
                contentDisposition = ContentDisposition.attachment()
                    .filename("provider_payments_report.xlsx")
                    .build()
            }

            ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelBytes.size.toLong())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes)

        } catch (e: DateTimeParseException) {
            println("Failed to parse date: startDate=$startDate, endDate=$endDate, Error: ${e.message}")
            ResponseEntity.badRequest().body("Invalid date format. Please use ISO 8601 (e.g., YYYY-MM-DDTHH:mm:ssZ)".toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to generate Excel report.".toByteArray())
        }
    }






}

