package com.example.jeebapi

import com.example.jeebapi.models.User
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.xhtmlrenderer.pdf.ITextRenderer
import java.io.ByteArrayOutputStream
import javax.sql.DataSource
import kotlin.use

@RestController
@RequestMapping("/api")
class ApiController(private val apiService: ApiService) {




    @GetMapping("/generate-invoice")
    fun generateInvoice(response: HttpServletResponse) {
        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 40px;
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 40px;
                    }
                    .logo {
                        width: 120px;
                        margin-bottom: 10px;
                    }
                    h1 {
                        color: #333;
                    }
                    table {
                        width: 100%;
                        border-collapse: collapse;
                        margin-top: 20px;
                    }
                    th, td {
                        padding: 10px;
                        border: 1px solid #ccc;
                        text-align: left;
                    }
                    th {
                        background-color: #f2f2f2;
                    }
                    .total {
                        text-align: right;
                        font-size: 18px;
                        margin-top: 20px;
                    }
                    .footer {
                        margin-top: 50px;
                        font-size: 12px;
                        text-align: center;
                        color: #777;
                    }
                </style>
            </head>
            <body>
                <div class="header">
                    <img class="logo" src="https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/React-icon.svg/1024px-React-icon.svg.png" alt="Company Logo"/>
                    <h1>Invoice</h1>
                </div>

                <p><strong>Invoice #:</strong> 98765</p>
                <p><strong>Date:</strong> ${java.time.LocalDate.now()}</p>
                <p><strong>Customer:</strong> Alice Wonderland</p>

                <table>
                    <thead>
                        <tr>
                            <th>Item</th>
                            <th>Quantity</th>
                            <th>Unit Price</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>Green Tea</td>
                            <td>3</td>
                            <td>$4.00</td>
                            <td>$12.00</td>
                        </tr>
                        <tr>
                            <td>Black Coffee</td>
                            <td>2</td>
                            <td>$6.00</td>
                            <td>$12.00</td>
                        </tr>
                        <tr>
                            <td>Herbal Infusion</td>
                            <td>1</td>
                            <td>$10.00</td>
                            <td>$10.00</td>
                        </tr>
                    </tbody>
                </table>

                <p class="total"><strong>Grand Total: $34.00</strong></p>

                <div class="footer">
                    Thank you for your business!<br/>
                    Company Name · Address · Contact Info
                </div>
            </body>
            </html>
        """.trimIndent()

        val outputStream = ByteArrayOutputStream()
        val renderer = ITextRenderer()

        renderer.setDocumentFromString(html)
        renderer.layout()
        renderer.createPDF(outputStream)

        val pdfBytes = outputStream.toByteArray()

        response.contentType = "application/pdf"
        response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf")
        response.outputStream.write(pdfBytes)
    }


    @GetMapping("/users/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = apiService.findByEmail(email) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }




}