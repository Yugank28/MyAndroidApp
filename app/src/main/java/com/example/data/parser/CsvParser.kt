package com.example.data.parser

import android.content.Context
import android.net.Uri
import com.example.data.model.NutritionStatus
import com.example.data.model.ParseResult
import com.example.data.model.ParsedChildRecord
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object CsvParser {

    fun parseUri(context: Context, uri: Uri): ParseResult {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return ParseResult(emptyList(), listOf("Could not open file URI: $uri"), "CSV")
            parseStream(inputStream)
        } catch (e: Exception) {
            ParseResult(emptyList(), listOf("Failed to read file: ${e.localizedMessage}"), "CSV")
        }
    }

    fun parseText(csvContent: String): ParseResult {
        return parseStream(csvContent.byteInputStream())
    }

    fun parseStream(inputStream: InputStream): ParseResult {
        val successfulRecords = mutableListOf<ParsedChildRecord>()
        val errors = mutableListOf<String>()
        val seenIds = mutableSetOf<String>()
        val duplicateIds = mutableListOf<String>()

        try {
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                var lineNumber = 0
                var headers: Map<String, Int>? = null

                while (reader.readLine().also { line = it } != null) {
                    lineNumber++
                    val currentLine = line?.trim() ?: continue
                    if (currentLine.isEmpty() || currentLine.startsWith("#")) {
                        continue
                    }

                    val tokens = splitCsvLine(currentLine)

                    if (headers == null) {
                        headers = mapHeaders(tokens)
                        if (headers.containsKey("child_id") || headers.containsKey("name")) {
                            continue
                        } else {
                            headers = defaultHeaderMapping()
                        }
                    }

                    try {
                        val record = parseRow(tokens, headers, lineNumber)
                        if (record != null) {
                            if (seenIds.contains(record.childId)) {
                                duplicateIds.add(record.childId)
                                errors.add("Line $lineNumber: Duplicate Child ID '${record.childId}' in uploaded file (latest row used)")
                            }
                            seenIds.add(record.childId)
                            successfulRecords.add(record)
                        }
                    } catch (e: Exception) {
                        errors.add("Line $lineNumber: ${e.message ?: "Invalid data format"}")
                    }
                }
            }
        } catch (e: Exception) {
            errors.add("Fatal parsing error: ${e.localizedMessage}")
        }

        return ParseResult(
            successfulRecords = successfulRecords,
            errors = errors,
            sourceFormat = "CSV",
            duplicateIdsInFile = duplicateIds
        )
    }

    private fun mapHeaders(tokens: List<String>): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        tokens.forEachIndexed { index, rawHeader ->
            val clean = rawHeader.trim().lowercase().replace(" ", "_").replace("-", "_")
            map[clean] = index
        }
        return map
    }

    private fun defaultHeaderMapping(): Map<String, Int> {
        return mapOf(
            "child_id" to 0,
            "name" to 1,
            "dob" to 2,
            "age_months" to 3,
            "gender" to 4,
            "guardian" to 5,
            "parent_contact" to 6,
            "reg_code" to 7,
            "weight_kg" to 8,
            "height_cm" to 9,
            "nutrition_status" to 10,
            "next_vaccine" to 11,
            "attendance_count" to 12,
            "date" to 13,
            "notes" to 14
        )
    }

    private fun parseRow(
        tokens: List<String>,
        headers: Map<String, Int>,
        lineNumber: Int
    ): ParsedChildRecord? {
        fun get(key: String, alt: String? = null): String {
            val idx = headers[key] ?: (if (alt != null) headers[alt] else null) ?: -1
            return if (idx in tokens.indices) tokens[idx].trim().trim('"', '\'') else ""
        }

        val childId = get("child_id", "id")
        if (childId.isEmpty()) {
            throw IllegalArgumentException("Missing required 'child_id'")
        }

        val name = get("name", "child_name")
        if (name.isEmpty()) {
            throw IllegalArgumentException("Missing required 'name' for child $childId")
        }

        val dob = get("dob").ifEmpty { "14-Aug-2023" }
        val ageMonthsStr = get("age_months", "age")
        val ageMonths = ageMonthsStr.toIntOrNull() ?: 18

        val weightStr = get("weight_kg", "weight")
        val weightKg = weightStr.toFloatOrNull() ?: run {
            throw IllegalArgumentException("Invalid weight '$weightStr'")
        }

        val heightStr = get("height_cm", "height")
        val heightCm = heightStr.toFloatOrNull() ?: run {
            throw IllegalArgumentException("Invalid height '$heightStr'")
        }

        val rawStatus = get("nutrition_status", "status")
        val nutritionStatus = if (rawStatus.isNotEmpty()) {
            NutritionStatus.fromString(rawStatus)
        } else {
            NutritionStatus.evaluate(ageMonths, weightKg)
        }

        val guardian = get("guardian", "parent_name").ifEmpty { "Guardian of $name" }
        val vaccinationStatus = get("vaccination_status", "vaccination").ifEmpty { "Up to date" }
        val nextVaccine = get("next_vaccine").ifEmpty { vaccinationStatus }
        val attendanceStr = get("attendance_count", "attendance")
        val attendanceCount = attendanceStr.toIntOrNull() ?: 22
        val date = get("date", "record_date").ifEmpty { "2026-10-18" }
        val gender = get("gender").ifEmpty { "M" }
        val parentContact = get("parent_contact", "contact").ifEmpty { "9890123456" }
        val regCode = get("reg_code").ifEmpty { "ACT-${(1000..9999).random()}-AW" }
        val centerName = get("center_name", "anganwadi").ifEmpty { "AW-Center-042 (Shivaji Nagar)" }
        val notes = get("notes")

        return ParsedChildRecord(
            childId = childId,
            name = name,
            dob = dob,
            ageMonths = ageMonths,
            gender = gender,
            parentName = guardian,
            guardian = guardian,
            parentContact = parentContact,
            regCode = regCode,
            centerId = "AW-Center-042",
            centerName = centerName,
            date = date,
            weightKg = weightKg,
            heightCm = heightCm,
            nutritionStatus = nutritionStatus,
            vaccinationStatus = vaccinationStatus,
            nextVaccine = nextVaccine,
            attendanceCount = attendanceCount.coerceIn(0, 31),
            notes = notes
        )
    }

    private fun splitCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var inQuotes = false
        val sb = StringBuilder()

        for (ch in line) {
            when {
                ch == '\"' -> {
                    inQuotes = !inQuotes
                }
                ch == ',' && !inQuotes -> {
                    result.add(sb.toString().trim())
                    sb.clear()
                }
                else -> {
                    sb.append(ch)
                }
            }
        }
        result.add(sb.toString().trim())
        return result
    }
}
