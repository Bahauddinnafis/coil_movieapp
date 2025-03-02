package com.nafis.coilmovieapp.utils

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

fun writeLogcatLoadingTimesToExcel(
    loadingTimes: List<Pair<String, Triple<String, String, Long>>>,
    filePath: String
) {
    val workbook = XSSFWorkbook()
    val sheet = workbook.createSheet("Loading Times")

    // Header tanpa kolom "Component"
    val headerRow = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("Image URL")
    headerRow.createCell(1).setCellValue("Title")
    headerRow.createCell(2).setCellValue("Loading Time (ms)")
    headerRow.createCell(3).setCellValue("Memory Usage (MB)")

    // Tulis data tanpa kolom "Component"
    loadingTimes.forEachIndexed { index, (imageUrl, data) ->
        val row = sheet.createRow(index + 1)
        row.createCell(0).setCellValue(imageUrl) // Image URL
        row.createCell(1).setCellValue(data.second) // Title

        if (data.first == "Memory Usage") {
            row.createCell(3).setCellValue(data.third.toDouble()) // Isi kolom memory
            row.createCell(2).setCellValue("") // Kosongkan loading time
        } else {
            row.createCell(2).setCellValue(data.third.toDouble()) // Isi loading time
            row.createCell(3).setCellValue("") // Kosongkan memory
        }
    }

    // Hitung statistik (hanya untuk loading time)
    if (loadingTimes.isNotEmpty()) {
        val validTimes = loadingTimes.filter { it.second.first != "Memory Usage" }
        val totalTime = validTimes.sumOf { it.second.third }
        val avgTime = totalTime.toDouble() / validTimes.size

        var lastRow = sheet.lastRowNum + 2

        // Total loading time
        val totalRow = sheet.createRow(lastRow++)
        totalRow.createCell(0).setCellValue("Total Loading Time")
        totalRow.createCell(2).setCellValue(totalTime.toDouble()) // Kolom loading time

        // Average loading time
        val avgRow = sheet.createRow(lastRow)
        avgRow.createCell(0).setCellValue("Average Loading Time")
        avgRow.createCell(2).setCellValue(avgTime) // Kolom loading time
    }

    // Tulis ke file
    FileOutputStream(File(filePath)).use { outputStream ->
        workbook.write(outputStream)
    }
    workbook.close()
}