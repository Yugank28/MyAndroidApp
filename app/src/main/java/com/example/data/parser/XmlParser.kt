package com.example.data.parser

import android.content.Context
import android.net.Uri
import com.example.data.model.NutritionStatus
import com.example.data.model.ParseResult
import com.example.data.model.ParsedChildRecord
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream

object XmlParser {

    fun parseUri(context: Context, uri: Uri): ParseResult {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return ParseResult(emptyList(), listOf("Could not open file URI: $uri"), "XML")
            parseStream(inputStream)
        } catch (e: Exception) {
            ParseResult(emptyList(), listOf("Failed to read XML: ${e.localizedMessage}"), "XML")
        }
    }

    fun parseText(xmlContent: String): ParseResult {
        return parseStream(xmlContent.byteInputStream())
    }

    fun parseStream(inputStream: InputStream): ParseResult {
        val successfulRecords = mutableListOf<ParsedChildRecord>()
        val errors = mutableListOf<String>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(inputStream, "UTF-8")

            var eventType = parser.eventType
            var currentValues = mutableMapOf<String, String>()

            val knownTags = setOf(
                "child_id", "id", "name", "child_name", "age_months", "age",
                "weight_kg", "weight", "height_cm", "height",
                "nutrition_status", "status", "vaccination_status", "vaccination",
                "attendance_count", "attendance", "date", "gender",
                "parent_contact", "contact", "center_name", "anganwadi", "notes"
            )

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name?.lowercase() ?: ""
                        if (tagName == "child" || tagName == "record") {
                            currentValues = mutableMapOf()
                        } else if (tagName in knownTags) {
                            val text = parser.nextText()
                            currentValues[tagName] = text.trim()
                        }
                    }
                    XmlPullParser.END_TAG -> {
                        val endTag = parser.name?.lowercase() ?: ""
                        if (endTag == "child" || endTag == "record") {
                            try {
                                val record = parseRecordFromMap(currentValues, parser.lineNumber)
                                successfulRecords.add(record)
                            } catch (e: Exception) {
                                errors.add("XML Element at line ${parser.lineNumber}: ${e.message}")
                            }
                            currentValues = mutableMapOf()
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            errors.add("XML Parsing Error: ${e.localizedMessage}")
        } finally {
            try {
                inputStream.close()
            } catch (_: Exception) {}
        }

        return ParseResult(successfulRecords, errors, "XML")
    }

    private fun parseRecordFromMap(
        map: Map<String, String>,
        lineNumber: Int
    ): ParsedChildRecord {
        val childId = map["child_id"] ?: map["id"]
        if (childId.isNullOrEmpty()) {
            throw IllegalArgumentException("Missing <child_id> tag")
        }

        val name = map["name"] ?: map["child_name"]
        if (name.isNullOrEmpty()) {
            throw IllegalArgumentException("Missing <name> tag for child $childId")
        }

        val ageMonths = map["age_months"]?.toIntOrNull()
            ?: map["age"]?.toIntOrNull()
            ?: 12
        val weightKg = map["weight_kg"]?.toFloatOrNull()
            ?: map["weight"]?.toFloatOrNull()
            ?: throw IllegalArgumentException("Missing or invalid <weight_kg>")
        val heightCm = map["height_cm"]?.toFloatOrNull()
            ?: map["height"]?.toFloatOrNull()
            ?: throw IllegalArgumentException("Missing or invalid <height_cm>")

        val rawStatus = map["nutrition_status"] ?: map["status"]
        val status = if (!rawStatus.isNullOrEmpty()) {
            NutritionStatus.fromString(rawStatus)
        } else {
            NutritionStatus.evaluate(ageMonths, weightKg)
        }

        val vaccination = map["vaccination_status"] ?: map["vaccination"] ?: "Up to date"
        val attendance = map["attendance_count"]?.toIntOrNull()
            ?: map["attendance"]?.toIntOrNull()
            ?: 20
        val date = map["date"] ?: "2026-10-01"
        val gender = map["gender"] ?: "M"
        val contact = map["parent_contact"] ?: map["contact"] ?: "9876543210"
        val center = map["center_name"] ?: map["anganwadi"] ?: "Kalyanpuri Anganwadi 4"
        val notes = map["notes"] ?: ""

        return ParsedChildRecord(
            childId = childId,
            name = name,
            ageMonths = ageMonths,
            gender = gender,
            parentName = "Parent of $name",
            parentContact = contact,
            centerId = "AWC-${childId.take(3)}",
            centerName = center,
            date = date,
            weightKg = weightKg,
            heightCm = heightCm,
            nutritionStatus = status,
            vaccinationStatus = vaccination,
            attendanceCount = attendance.coerceIn(0, 31),
            notes = notes
        )
    }
}
