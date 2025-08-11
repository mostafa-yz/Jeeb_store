package com.example.jeebapi.services


import com.example.jeebapi.DTO.ReprotDTO
import com.example.jeebapi.DTO.SalesDashboard
import com.example.jeebapi.repository.TransactionRepository
import com.ibm.icu.text.ArabicShaping
import com.ibm.icu.text.ArabicShapingException
import com.ibm.icu.text.Bidi
import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale
import com.openhtmltopdf.extend.FSSupplier
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.apache.poi.hssf.usermodel.HeaderFooter.font
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


@Service
class ReportService(
    private val transaction: TransactionRepository,
) {



    fun generateProviderPdf(
        reportData: List<ReprotDTO>, startDate: Instant, endDate: Instant
    ): ByteArray {
        if (reportData.isEmpty()) {
            throw IllegalArgumentException("Report data cannot be empty.")
        }

        val firstReportItem = reportData.first()

        fun formatCurrencyWithSlashes(value: BigDecimal): String {

            val longValue = value.toLong()
            val formatter = DecimalFormat("#,###")
            return formatter.format(longValue).replace(',', '/')
        }

        // Get the current date and format it in Persian
        val currentDate = Instant.now().atZone(ZoneId.of("Asia/Tehran"))
        val persianCurrentDate = convertToPersianDate(currentDate.toInstant())

        val fontStreamSupplier = FSSupplier<InputStream> {
            object {}.javaClass.getResourceAsStream("/fonts/Vazir.ttf")
                ?: throw IllegalArgumentException("Font file 'Vazir.ttf' not found in resources.")
        }

        val persianStartDate = convertToPersianDate(startDate)
        val persianEndDate = convertToPersianDate(endDate)
        val persianRange = " ${toPersianDigits(persianStartDate)}__${toPersianDigits(persianEndDate)}"

        val logoDataUri = getLogoBase64()

        val productTableHtml = reportData.joinToString(separator = "") { item ->
            """
        <tr>
            <td>${toPersianDigits(item.totalQuantitySold.toString())}</td>
            <td>${reorderText(item.productName)}</td>
        </tr>
        """
        }

        val htmlContent = """
        <!DOCTYPE html>
        <html dir="rtl" lang="fa">
        <head>
            <meta charset="UTF-8"/>
            
            <style>
                body {
                    font-family: 'Vazir', sans-serif;
                    color: #333;
                    margin: 0;
                    direction: rtl;
                    text-align: right;
                }
                .report-page {
                    padding: 40px;
                    background-color: #ffffff;
                }
                .logo-container {
                    text-align: center;
                    margin-bottom: 20px;
                }
                .logo {
                    max-width: 200px;
                    height: auto;
                }
                .section {
                    margin-bottom: 20px;
                }
                .section-title {
                    font-size: 1.2em;
                    font-weight: bold;
                    border-bottom: 2px solid #333;
                    padding-bottom: 5px;
                    margin-bottom: 10px;
                }
                .summary-box {
                    background-color: #f7f7f7;
                    padding: 20px;
                    border-radius: 5px;
                }
                .summary-item {
                    display: block;
                    padding: 5px 0;
                }
                .summary-item strong {
                    font-weight: bold;
                    display: inline-block;
                    width: 45%;
                }
                .summary-item span {
                    display: inline-block;
                    width: 50%;
                    text-align: left;
                }
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 20px;
                }
                th, td {
                    border: 1px solid #ddd;
                    padding: 12px;
                    text-align: right;
                }
                th {
                    background-color: #f2f2f2;
                    font-weight: bold;
                }
                
                .products-table-section {
                    margin-top: 30px;
                }
                thead {
                    display: table-header-group;
                }
                tfoot {
                    display: table-footer-group;
                }
                
                .footer {
                    display: table;
                    width: 100%;
                    position: absolute;
                    bottom: 0;
                    padding: 0 40px 40px 40px;
                }
                .footer-cell {
                    display: table-cell;
                    width: 50%;
                    vertical-align: top;
                }
                .signature-line {
                    border-bottom: 1px solid #333;
                    margin-top: 40px;
                }

                @page {
                    size: A4;
                    margin: 2cm 1cm;
                }
                .page-break-before {
                    page-break-before: always;
                }
            </style>
        </head>
        <body>
            <div class="report-page">
                <div class="logo-container">
                    <img class="logo" src="$logoDataUri" alt="Company Logo"/>
                </div>
                
                <div class="section">
                    <div class="section-title">${reorderText("خلاصه فروش ")}</div>
                    <div class="provider-info">
                        <span class="summary-item">
                            <strong>${reorderText("تامین کننده:")}</strong>
                            <span>${reorderText(firstReportItem.providerName)}</span>
                        </span>
                        <span class="summary-item">
                            <strong>${reorderText("تاریخ صدور:")}</strong>
                            <span>${toPersianDigits(persianCurrentDate)}</span>
                        </span>
                        <span class="summary-item">
                            <strong>${reorderText("دوره فروش:")}</strong>
                            <span>$persianRange</span>
                        </span>
                    </div>
                </div>

                <div class="section summary-box">
                    <div class="section-title">${reorderText("خلاصه کل فروش")}</div>
                    <div>
                        <span class="summary-item">
                            <strong>${reorderText("مجموع فروش:")}</strong>
                            <!-- Use the new formatting function here -->
                            <span>${formatCurrencyWithSlashes(firstReportItem.totalTransactionValue)}</span>
                        </span>
                        <span class="summary-item">
                            <strong>${reorderText("سهم ما:")}</strong>
                            <span>${formatCurrencyWithSlashes(firstReportItem.totalOurEarnings)}</span>
                        </span>
                        <span class="summary-item">
                            <strong>${reorderText("مجموع پرداختی به تامین کننده:")}</strong>
                            <span>${formatCurrencyWithSlashes(firstReportItem.totalPaidToProvider)}</span>
                        </span>
                    </div>
                </div>
            </div>

         

            <div class="page-break-before">
                <div class="products-table-section">
                    <h2>${reorderText("فروش محصولات")}</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>${reorderText("تعداد فروش")}</th>
                                <th>${reorderText("نام محصول")}</th>
                            </tr>
                        </thead>
                        <tbody>
                            $productTableHtml
                        </tbody>
                    </table>
                </div>
            </div>
            
        </body>
        </html>
    """.trimIndent()

        val outputStream = ByteArrayOutputStream()
        PdfRendererBuilder().apply {
            withHtmlContent(htmlContent, ClassPathResource("").url.toExternalForm())
            toStream(outputStream)
            useFont(fontStreamSupplier, "Vazir")
            run()
        }
        return outputStream.toByteArray()
    }

    fun generateAndZipAllReports(startDate: Instant, endDate: Instant,providerId: Long? = null): ByteArray {


        val summaryReports: List<ReprotDTO> = if (providerId != null) {
            // If an ID is provided, call the specific repository method.
            transaction.getProviderSummaryById(startDate, endDate, providerId)
        } else {
            // If no ID is provided, call the method for all providers.
            transaction.ProviderSummary(startDate, endDate)
        }

        if (summaryReports.isEmpty()) {
            return ByteArray(0)
        }


        val groupedByProvider = summaryReports.groupBy { it.providerName }
        println(groupedByProvider)
        val zipBaos = ByteArrayOutputStream()
        ZipOutputStream(zipBaos).use { zipOut ->
            // 3. Iterate through each provider's group.
            for ((providerName, reportsForProvider) in groupedByProvider) {
                // 4. Call the PDF function with the ENTIRE list of products for this provider.
                val pdfBytes = generateProviderPdf(reportsForProvider, startDate, endDate)
                val zipEntry = ZipEntry("${providerName.replace(" ", "_")}_report.pdf")
                zipOut.putNextEntry(zipEntry)
                zipOut.write(pdfBytes)
                zipOut.closeEntry()
            }
        }
        return zipBaos.toByteArray()
    }

    //
    fun reorderText(text: String): String {
        return try {
            val shaping = ArabicShaping(ArabicShaping.LETTERS_SHAPE)
            val reshapedText = shaping.shape(text)
            Bidi(reshapedText, Bidi.DIRECTION_DEFAULT_RIGHT_TO_LEFT).writeReordered(Bidi.REORDER_DEFAULT.toInt())
        } catch (e: ArabicShapingException) {
            e.printStackTrace()
            text
        }
    }

    fun convertToPersianDate(date: Instant): String {
        val uLocale = ULocale.forLanguageTag("fa-IR")
        val persianCalendar = Calendar.getInstance(uLocale)
        persianCalendar.timeInMillis = date.toEpochMilli()
        val formatter = com.ibm.icu.text.SimpleDateFormat("yyyy/MM/dd", uLocale)
        return toPersianDigits(formatter.format(persianCalendar.time))
    }

    fun toPersianDigits(input: String): String {
        val persianDigits = mapOf(
            '0' to '۰', '1' to '۱', '2' to '۲', '3' to '۳', '4' to '۴',
            '5' to '۵', '6' to '۶', '7' to '۷', '8' to '۸', '9' to '۹'
        )
        return input.map { persianDigits[it] ?: it }.joinToString("")
    }


    fun getLogoBase64(): String {
        val logoStream = this::class.java.getResourceAsStream("/logo/logo.jpg")
            ?: throw IllegalArgumentException("Logo image not found")

        val bytes = logoStream.readBytes()
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return "data:image/png;base64,$base64"
    }


    fun generateExcelReport(
        startDate: Instant,
        endDate: Instant
    ): ByteArray {
        // Create a new Excel workbook
        val workbook: Workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Provider Payments Report")
        val reportData = transaction.getProviderPaymentsSummary(startDate, endDate)

        // Create a font for the header row
        val headerFont = workbook.createFont().apply {
            bold = true
        }


        // Create the header row
        val headerRow = sheet.createRow(0)
        val headers = listOf(
            "نام تامین‌کننده",
            "ایمیل",
            "شماره شبا",
            "شماره کارت",
            "مجموع پرداختی به تامین‌کننده"
        )

        for ((index, header) in headers.withIndex()) {
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)

        }

        // Populate the data rows
        var rowNum = 1
        for (data in reportData) {
            val row = sheet.createRow(rowNum++)
            row.createCell(0).setCellValue(data.providerName)
            row.createCell(1).setCellValue(data.providerEmail)
            row.createCell(2).setCellValue(data.providerShabaNumber)
            row.createCell(3).setCellValue(data.providerCardNumber)
            row.createCell(4).setCellValue(data.totalPaidToProvider.toDouble()) // POI uses double for numbers
        }

        // Auto-size columns to fit the content
        for (i in headers.indices) {
            sheet.autoSizeColumn(i)
        }

        // Write the workbook to a ByteArrayOutputStream
        val outputStream = ByteArrayOutputStream()
        try {
            workbook.write(outputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            // You might want to throw a custom exception here
        } finally {
            workbook.close()
        }

        return outputStream.toByteArray()
    }



    @Transactional(readOnly = true)
    fun getSalesDashboardData(): SalesDashboard {
        val mostSoldItems = transaction.findMostSoldItemsForCurrentMonth()
        val topProviders = transaction.findTopProvidersByIncomeForCurrentMonth()
        val totalInvoices = transaction.countTotalInvoicesForCurrentMonth()
        val dailyInvoices = transaction.findDailyInvoicesForCurrentMonth()

        return SalesDashboard(
            mostSoldItems = mostSoldItems,
            topProviders = topProviders,
            totalInvoices = totalInvoices,
            dailyInvoices = dailyInvoices
        )
    }







}
