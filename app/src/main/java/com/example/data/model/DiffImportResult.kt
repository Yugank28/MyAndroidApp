package com.example.data.model

data class DiffImportResult(
    val fileName: String,
    val totalRows: Int,
    val newRecords: List<ParsedChildRecord>,
    val updatedRecords: List<ParsedChildRecord>,
    val duplicateIds: List<String>,
    val syntaxErrors: List<String>,
    val detectedSheets: List<String> = listOf("Children", "Guardians", "Growth Records", "Ration Records", "Immunization")
) {
    val isValid: Boolean
        get() = (newRecords.isNotEmpty() || updatedRecords.isNotEmpty())

    val totalValidCount: Int
        get() = newRecords.size + updatedRecords.size
}
