package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_records",
    indices = [
        Index(value = ["childId"]),
        Index(value = ["date"])
    ]
)
data class HealthRecord(
    @PrimaryKey(autoGenerate = true)
    val recordId: Long = 0,
    val childId: String,
    val date: String, // YYYY-MM-DD
    val weightKg: Float,
    val heightCm: Float,
    val nutritionStatus: NutritionStatus,
    val attendanceDays: Int = 20, // out of ~25 working days
    val muacCm: Float? = null,
    val notes: String = ""
) {
    // Calculated BMI: weight (kg) / [height (m)]^2
    val bmi: Float
        get() {
            val heightM = heightCm / 100f
            return if (heightM > 0f) weightKg / (heightM * heightM) else 0f
        }
}
