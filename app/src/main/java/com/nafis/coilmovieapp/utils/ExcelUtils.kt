package com.nafis.coilmovieapp.utils

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

fun writeLoadingTimesToExcel(
    loadingTimes: Map<String, List<Pair<String, Long>>>,
    filePath: String
) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Loading Times")

    // Buat baris header
    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("Section")
    headerRow.createCell(1).setCellValue("Title")
    headerRow.createCell(2).setCellValue("Image URL")
    headerRow.createCell(3).setCellValue("Loading Time (ms)")

    // Tulis waktu loading sebagai baris
    var rowIndex = 1
    var totalLoadingTime = 0L
    var count = 0

    loadingTimes.forEach { (section, times) ->
        times.forEach { (titleWithUrl, time) ->
            val (title, imageUrl) = titleWithUrl.split("-", limit = 2)
            val imageSize = when (section) {
                "Discover Movie" -> "w154"
                "Trending Now" -> "w185"
                "Cast & Crew" -> "w92"
                "More Like This", "Top Content", "Detail Top Content" -> "w500"
                else -> "N/A"
            }
            val row = sheet.createRow(rowIndex++)
            row.createCell(0).setCellValue(section)
            row.createCell(1).setCellValue(title)
            row.createCell(2).setCellValue(imageUrl.replace(Regex("w\\d+"), imageSize))
            row.createCell(3).setCellValue(time.toDouble())
            totalLoadingTime += time
            count++
        }
    }

    // Hitung rata-rata waktu loading
    val averageLoadingTime = if (count > 0) totalLoadingTime.toDouble() / count else 0.0

    // Tambahkan rata-rata waktu loading ke baris terakhir
    val averageRow = sheet.createRow(rowIndex)
    averageRow.createCell(0).setCellValue("Average")
    averageRow.createCell(3).setCellValue(averageLoadingTime)

    // Tulis output ke file
    FileOutputStream(File(filePath)).use { outputStream ->
        workbook.write(outputStream)
    }
    workbook.close()
}