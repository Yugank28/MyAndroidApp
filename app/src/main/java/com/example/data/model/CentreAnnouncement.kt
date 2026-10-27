package com.example.data.model

data class CentreAnnouncement(
    val id: String,
    val titleEn: String,
    val titleMr: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionMr: String,
    val descriptionHi: String,
    val date: String,
    val category: AnnouncementCategory,
    val isUrgent: Boolean = false
)

enum class AnnouncementCategory(val labelEn: String, val labelMr: String, val labelHi: String) {
    RATION("Ration Distribution", "पोषण आहार वाटप", "राशन वितरण"),
    HEALTH("Immunization & Health", "लसीकरण व आरोग्य तपासणी", "टीकाकरण एवं स्वास्थ्य"),
    EVENT("Anganwadi Event", "अंगणवाडी उपक्रम / कार्यक्रम", "आंगनवाड़ी कार्यक्रम"),
    NUTRITION("Poshan Nutrition Camp", "पोषण व सुपोषण मेळावा", "पोषण मेला")
}
