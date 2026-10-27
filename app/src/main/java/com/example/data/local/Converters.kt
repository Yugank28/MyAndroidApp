package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.NutritionStatus
import com.example.data.model.VaccineStatus

class Converters {
    @TypeConverter
    fun fromNutritionStatus(status: NutritionStatus?): String {
        return status?.name ?: NutritionStatus.NORMAL.name
    }

    @TypeConverter
    fun toNutritionStatus(value: String?): NutritionStatus {
        return if (value != null) {
            try {
                NutritionStatus.valueOf(value)
            } catch (_: Exception) {
                NutritionStatus.NORMAL
            }
        } else {
            NutritionStatus.NORMAL
        }
    }

    @TypeConverter
    fun fromVaccineStatus(status: VaccineStatus?): String {
        return status?.name ?: VaccineStatus.UPCOMING.name
    }

    @TypeConverter
    fun toVaccineStatus(value: String?): VaccineStatus {
        return if (value != null) {
            try {
                VaccineStatus.valueOf(value)
            } catch (_: Exception) {
                VaccineStatus.UPCOMING
            }
        } else {
            VaccineStatus.UPCOMING
        }
    }
}
