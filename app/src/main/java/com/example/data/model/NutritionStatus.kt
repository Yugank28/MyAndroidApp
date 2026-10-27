package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.StatusMAM
import com.example.ui.theme.StatusMAMBorder
import com.example.ui.theme.StatusMAMContainer
import com.example.ui.theme.StatusMAMText
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.StatusNormalBorder
import com.example.ui.theme.StatusNormalContainer
import com.example.ui.theme.StatusNormalText
import com.example.ui.theme.StatusSAM
import com.example.ui.theme.StatusSAMBorder
import com.example.ui.theme.StatusSAMContainer
import com.example.ui.theme.StatusSAMText

enum class NutritionStatus(
    val englishLabel: String,
    val marathiLabel: String,
    val hindiLabel: String,
    val descriptionEn: String,
    val descriptionMr: String,
    val descriptionHi: String
) {
    NORMAL(
        englishLabel = "Normal (Healthy)",
        marathiLabel = "सामान्य (निरोगी)",
        hindiLabel = "सामान्य (स्वस्थ)",
        descriptionEn = "Child growth parameters are within the healthy standard range. Continue diverse nutrient-rich diet.",
        descriptionMr = "मुलाची वाढ मानकांनुसार अगदी सामान्य व निरोगी आहे. संतुलित व चौरस आहार नियमित चालू ठेवा.",
        descriptionHi = "बच्चे की वृद्धि सामान्य मानक के अनुसार स्वस्थ है। संतुलित और पौष्टिक आहार जारी रखें।"
    ),
    MAM(
        englishLabel = "MAM (Moderate Malnutrition)",
        marathiLabel = "मध्यम कुपोषण (MAM)",
        hindiLabel = "एमएएम (मध्यम कुपोषण)",
        descriptionEn = "Moderate Acute Malnutrition detected. Needs energy-dense supplementary feeding, take-home rations, and bi-weekly checkups.",
        descriptionMr = "मध्यम तीव्र कुपोषण आढळले. ऊर्जासंवर्धक पूरक पोषण आहार (THR दलिया, अंडी) द्या आणि दर पंधरवड्याला वजन तपासा.",
        descriptionHi = "मध्यम कुपोषण। आंगनवाड़ी से अतिरिक्त पोषाहार (दलिया, अंडा, खिचड़ी) दें तथा हर 15 दिन में वजन कराएं।"
    ),
    SAM(
        englishLabel = "SAM (Alert / Severe Malnutrition)",
        marathiLabel = "तीव्र कुपोषण (SAM / सतर्कता)",
        hindiLabel = "एसएएम (गंभीर कुपोषण / चेतावनी)",
        descriptionEn = "Severe Acute Malnutrition. Priority medical referral to Nutrition Rehabilitation Centre (NRC) & therapeutic food needed.",
        descriptionMr = "गंभीर कुपोषण सतर्कता. त्वरित पोषण पुनर्वसन केंद्र (NRC) अथवा डॉक्टरांशी संपर्क साधा आणि विशेष उपचार आहार सुरू करा.",
        descriptionHi = "गंभीर कुपोषण। तुरंत पोषण पुनर्वास केंद्र (NRC) या डॉक्टर को दिखाएं और विशेष चिकित्सीय आहार शुरू करें।"
    );

    fun label(language: com.example.ui.theme.AppLanguage): String = when (language) {
        com.example.ui.theme.AppLanguage.ENGLISH -> englishLabel
        com.example.ui.theme.AppLanguage.MARATHI -> marathiLabel
        com.example.ui.theme.AppLanguage.HINDI -> hindiLabel
    }

    fun description(language: com.example.ui.theme.AppLanguage): String = when (language) {
        com.example.ui.theme.AppLanguage.ENGLISH -> descriptionEn
        com.example.ui.theme.AppLanguage.MARATHI -> descriptionMr
        com.example.ui.theme.AppLanguage.HINDI -> descriptionHi
    }

    val badgeColor: Color
        get() = when (this) {
            NORMAL -> StatusNormal
            MAM -> StatusMAM
            SAM -> StatusSAM
        }

    val containerColor: Color
        get() = when (this) {
            NORMAL -> StatusNormalContainer
            MAM -> StatusMAMContainer
            SAM -> StatusSAMContainer
        }

    val textColor: Color
        get() = when (this) {
            NORMAL -> StatusNormalText
            MAM -> StatusMAMText
            SAM -> StatusSAMText
        }

    val borderColor: Color
        get() = when (this) {
            NORMAL -> StatusNormalBorder
            MAM -> StatusMAMBorder
            SAM -> StatusSAMBorder
        }

    companion object {
        fun fromString(value: String?): NutritionStatus {
            if (value.isNullOrBlank()) return NORMAL
            val normalized = value.trim().uppercase()
            return when {
                normalized.contains("SAM") || normalized.contains("SEVERE") || normalized.contains("ALERT") -> SAM
                normalized.contains("MAM") || normalized.contains("MODERATE") -> MAM
                else -> NORMAL
            }
        }

        fun evaluate(ageMonths: Int, weightKg: Float, isBoy: Boolean = true): NutritionStatus {
            val expectedWeight = when {
                ageMonths <= 6 -> 3.3f + (ageMonths * 0.7f)
                ageMonths <= 12 -> 7.5f + ((ageMonths - 6) * 0.45f)
                ageMonths <= 24 -> 10.2f + ((ageMonths - 12) * 0.2f)
                ageMonths <= 36 -> 12.6f + ((ageMonths - 24) * 0.17f)
                ageMonths <= 48 -> 14.6f + ((ageMonths - 36) * 0.16f)
                else -> 16.5f + ((ageMonths - 48) * 0.15f)
            }
            val ratio = weightKg / expectedWeight
            return when {
                ratio < 0.70f -> SAM
                ratio < 0.82f -> MAM
                else -> NORMAL
            }
        }
    }
}
