package com.example.data.model

data class ParsedChildRecord(
    val childId: String,
    val name: String,
    val dob: String = "14-Aug-2023",
    val ageMonths: Int,
    val gender: String = "M",
    val parentName: String = "Guardian",
    val guardian: String = parentName,
    val parentContact: String = "",
    val regCode: String = "",
    val centerId: String = "AW-Center-042",
    val centerName: String = "AW-Center-042 (Shivaji Nagar)",
    val date: String,
    val weightKg: Float,
    val heightCm: Float,
    val nutritionStatus: NutritionStatus,
    val vaccinationStatus: String = "Up to date",
    val nextVaccine: String = "Up to date",
    val attendanceCount: Int = 20,
    val notes: String = "",
    val isUpdate: Boolean = false // Flagged during diff check
)

data class ParseResult(
    val successfulRecords: List<ParsedChildRecord>,
    val errors: List<String>,
    val sourceFormat: String = "CSV",
    val newRecords: List<ParsedChildRecord> = emptyList(),
    val matchedRecords: List<ParsedChildRecord> = emptyList(),
    val duplicateIdsInFile: List<String> = emptyList()
)
