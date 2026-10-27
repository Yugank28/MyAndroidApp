package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class VaccineStatus(val labelEn: String, val labelHi: String) {
    COMPLETED("Administered", "लग चुका"),
    DUE("Due Now", "अतिदेय / अभी लगाएं"),
    UPCOMING("Upcoming", "आगामी")
}

@Entity(
    tableName = "vaccination_records",
    indices = [
        Index(value = ["childId"])
    ]
)
data class VaccinationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childId: String,
    val vaccineName: String,
    val dose: String,
    val dueDate: String,
    val status: VaccineStatus,
    val administeredDate: String? = null
)
