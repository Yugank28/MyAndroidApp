package com.example.ui.theme

enum class AppLanguage(val code: String, val displayName: String, val shortLabel: String) {
    ENGLISH("EN", "English", "EN"),
    MARATHI("MR", "मराठी", "मराठी"),
    HINDI("HI", "हिंदी", "हिंदी")
}

object AppStrings {

    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Anganwadi Parent Connect"
        AppLanguage.MARATHI -> "अंगणवाडी पालक कनेक्ट"
        AppLanguage.HINDI -> "आंगनवाड़ी पालक कनेक्ट"
    }

    fun appSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Child Growth, Attendance & Rations Portal"
        AppLanguage.MARATHI -> "बाल विकास, उपस्थिती व पोषण आहार पोर्टल"
        AppLanguage.HINDI -> "बाल विकास, उपस्थिति एवं पोषण आहार पोर्टल"
    }

    fun tabAdmin(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Anganwadi Worker (Admin)"
        AppLanguage.MARATHI -> "अंगणवाडी सेविका (प्रशासक)"
        AppLanguage.HINDI -> "आंगनवाड़ी कार्यकर्ता (एडमिन)"
    }

    fun tabParent(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Parent / Guardian"
        AppLanguage.MARATHI -> "पालक / पालकवर्ग"
        AppLanguage.HINDI -> "माता-पिता / अभिभावक"
    }

    fun adminUsername(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Sevika Username"
        AppLanguage.MARATHI -> "सेविका युझरनेम"
        AppLanguage.HINDI -> "कार्यकर्ता यूज़रनेम"
    }

    fun adminPassword(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Password / Security PIN"
        AppLanguage.MARATHI -> "पासवर्ड / सुरक्षा पिन"
        AppLanguage.HINDI -> "पासवर्ड / सुरक्षा पिन"
    }

    fun btnLoginAdmin(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Log In as Sevika"
        AppLanguage.MARATHI -> "सेविका म्हणून लॉग इन करा"
        AppLanguage.HINDI -> "कार्यकर्ता के रूप में लॉग इन करें"
    }

    fun parentLoginTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Child ID or Registered Mobile"
        AppLanguage.MARATHI -> "चाइल्ड आयडी किंवा नोंदणीकृत मोबाईल"
        AppLanguage.HINDI -> "चाइल्ड आईडी या पंजीकृत मोबाइल"
    }

    fun parentChildIdHint(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "e.g. AW-042-01"
        AppLanguage.MARATHI -> "उदा. AW-042-01"
        AppLanguage.HINDI -> "उदा. AW-042-01"
    }

    fun parentMobileHint(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "10-Digit Mobile Number"
        AppLanguage.MARATHI -> "१०-अंकी मोबाईल नंबर"
        AppLanguage.HINDI -> "१०-अंकीय मोबाइल नंबर"
    }

    fun parentRegCodeHint(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Registration Code (e.g. ACT-7842-AG)"
        AppLanguage.MARATHI -> "नोंदणी कोड (उदा. ACT-7842-AG)"
        AppLanguage.HINDI -> "पंजीकरण कोड (उदा. ACT-7842-AG)"
    }

    fun btnVerifyParent(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Verify & Open Dashboard"
        AppLanguage.MARATHI -> "सत्यापित करा व डॅशबोर्ड उघडा"
        AppLanguage.HINDI -> "सत्यापित करें और डैशबोर्ड खोलें"
    }

    fun quickDemoTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Quick Demo Fill & Login:"
        AppLanguage.MARATHI -> "त्वरित डेमो लॉगिन (एक क्लिक):"
        AppLanguage.HINDI -> "त्वरित डेमो लॉगिन (एक क्लिक):"
    }

    fun rationScheduleTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Upcoming Ration Distribution"
        AppLanguage.MARATHI -> "आगामी पोषण आहार वाटप"
        AppLanguage.HINDI -> "आगामी पोषण राशन वितरण"
    }

    fun immunizationTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Immunization Schedule & Alerts"
        AppLanguage.MARATHI -> "लसीकरण वेळापत्रक व सतर्कता"
        AppLanguage.HINDI -> "टीकाकरण अनुसूची एवं अलर्ट"
    }

    fun aiAdviceTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "AI Growth & Nutrition Guidance"
        AppLanguage.MARATHI -> "AI वाढ व पोषण मार्गदर्शन"
        AppLanguage.HINDI -> "AI वृद्धि एवं पोषण मार्गदर्शन"
    }

    fun attendanceTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Monthly Attendance"
        AppLanguage.MARATHI -> "मासिक उपस्थिती"
        AppLanguage.HINDI -> "मासिक उपस्थिति"
    }

    fun offlineSyncStatus(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Offline Sync: Just now"
        AppLanguage.MARATHI -> "ऑफलाईन सिंक: नुकतेच पूर्ण"
        AppLanguage.HINDI -> "ऑफ़लाइन सिंक: अभी-अभी"
    }

    fun logout(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Log Out"
        AppLanguage.MARATHI -> "लॉग आउट"
        AppLanguage.HINDI -> "लॉग आउट"
    }

    fun bulkUploadTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Bulk Records Ingestion (CSV / XML)"
        AppLanguage.MARATHI -> "मोफत फाईल नोंदणी (CSV / XML)"
        AppLanguage.HINDI -> "बल्क रिकॉर्ड अपलोड (CSV / XML)"
    }

    fun downloadTemplate(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Download Template (.csv / .xlsx)"
        AppLanguage.MARATHI -> "टेम्पलेट डाऊनलोड करा (.csv / .xlsx)"
        AppLanguage.HINDI -> "टेम्प्लेट डाउनलोड करें (.csv / .xlsx)"
    }

    fun dailyAttendance(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Daily Attendance & Services"
        AppLanguage.MARATHI -> "दैनिक उपस्थिती नोंदणी"
        AppLanguage.HINDI -> "दैनिक उपस्थिति व सेवाएं"
    }

    fun present(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Present"
        AppLanguage.MARATHI -> "उपस्थित"
        AppLanguage.HINDI -> "उपस्थित"
    }

    fun absent(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Absent"
        AppLanguage.MARATHI -> "अनुपस्थित"
        AppLanguage.HINDI -> "अनुपस्थित"
    }
}
