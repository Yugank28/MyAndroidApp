package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "child_profiles",
    indices = [
        Index(value = ["childId"], unique = true),
        Index(value = ["parentContact"]),
        Index(value = ["regCode"])
    ]
)
data class ChildProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val childId: String, // e.g. "AW-042-01"
    val name: String, // e.g. "Aarav Sachin Gaikwad"
    val dob: String, // e.g. "14-Aug-2023"
    val ageMonths: Int,
    val gender: String = "M", // M or F
    val parentName: String = "Guardian",
    val guardian: String = parentName,
    val parentContact: String = "",
    val regCode: String = "", // e.g. "ACT-7842-AG"
    val anganwadiCenterId: String = "AW-Center-042",
    val anganwadiCenterName: String = "AW-Center-042 (Shivaji Nagar)",
    val address: String = "Shivaji Nagar Ward 4",
    val nextVaccine: String = "DPT Booster (12-Nov-2026)",
    val avatarColorIndex: Int = 0
) {
    val formattedAge: String
        get() {
            val years = ageMonths / 12
            val months = ageMonths % 12
            return when {
                years > 0 && months > 0 -> "$years yr $months mo ($ageMonths months)"
                years > 0 -> "$years years ($ageMonths months)"
                else -> "$months months"
            }
        }

    val formattedAgeHi: String
        get() {
            val years = ageMonths / 12
            val months = ageMonths % 12
            return when {
                years > 0 && months > 0 -> "$years वर्ष $months माह ($ageMonths माह)"
                years > 0 -> "$years वर्ष ($ageMonths माह)"
                else -> "$months माह"
            }
        }

    val formattedAgeMr: String
        get() {
            val years = ageMonths / 12
            val months = ageMonths % 12
            return when {
                years > 0 && months > 0 -> "$years वर्षे $months महिने ($ageMonths महिने)"
                years > 0 -> "$years वर्षे ($ageMonths महिने)"
                else -> "$months महिने"
            }
        }
}
