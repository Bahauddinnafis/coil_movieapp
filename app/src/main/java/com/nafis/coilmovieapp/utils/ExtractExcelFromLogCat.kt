package com.nafis.coilmovieapp.utils

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

fun extractLoadingTimesFromLogcat(): List<Pair<String, Triple<String, String, Long>>> {
    val loadingTimes = mutableListOf<Pair<String, Triple<String, String, Long>>>()

    try {
        val process = Runtime.getRuntime().exec("logcat -d")
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        val imageUrlPattern = ".*Image URL: (.*)".toRegex()
        val loadingTimePatterns = listOf(
            ".*LoadingTime: Start loading image for (.*)\\n.*Coil loading time for (.*): (\\d+) ms".toRegex(),
            ".*Coil loading time for (.*): (\\d+) ms".toRegex(),
            ".*Loading time for (.*): (\\d+) ms".toRegex()
        )
        val memoryUsagePattern = ".*Memori setelah loading (.*): (\\d+) MB.*".toRegex()

        var currentImageUrl: String? = null
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            // Tangkap URL gambar
            val urlMatch = imageUrlPattern.find(line ?: "")
            if (urlMatch != null) {
                currentImageUrl = urlMatch.groupValues[1]
                continue
            }

            // Tangkap loading time
            var isTimeLog = false
            for (pattern in loadingTimePatterns) {
                val matchResult = pattern.find(line ?: "")
                if (matchResult != null) {
                    val groups = matchResult.groupValues
                    when (groups.size) {
                        4 -> {
                            loadingTimes.add(
                                Pair(
                                    currentImageUrl ?: "Unknown URL",
                                    Triple(groups[1], groups[2], groups[3].toLong())
                                )
                            )
                        }
                        3 -> {
                            loadingTimes.add(
                                Pair(
                                    currentImageUrl ?: "Unknown URL",
                                    Triple("Unknown", groups[1], groups[2].toLong())
                                )
                            )
                        }
                    }
                    isTimeLog = true
                    break
                }
            }
            if (isTimeLog) continue

            // Tangkap memory usage
            val memoryMatch = memoryUsagePattern.find(line ?: "")
            if (memoryMatch != null) {
                val title = memoryMatch.groupValues[1]
                val memory = memoryMatch.groupValues[2].toLong()
                loadingTimes.add(
                    Pair(
                        currentImageUrl ?: "Unknown URL",
                        Triple("Memory Usage", title, memory)
                    )
                )
            }
        }

        bufferedReader.close()
        process.destroy()
    } catch (e: Exception) {
        Log.e("LogcatUtils", "Error extracting data from logcat", e)
    }

    return loadingTimes
}