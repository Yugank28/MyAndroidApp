package com.example.data.model

data class RationSchedule(
    val id: String = "RATION-2026-10",
    val cycleMonth: String = "October 2026",
    val distributionDate: String = "26-Oct-2026",
    val pickupTiming: String = "08:30 AM – 01:30 PM",
    val location: String = "AW-Center-042, Shivaji Nagar Community Hall",
    val sevikaContact: String = "Rekha Tai Patil (+91 9822001122)",
    val items: List<RationItem> = listOf(
        RationItem("Take Home Ration (THR) Ready Mix", "2 Packets (2 kg)", "ऊर्जा व प्रथिनयुक्त पोषण पूरक आहार"),
        RationItem("Fortified Wheat-Soy Flour", "2 kg", "लोहयुक्त व फॉलिक ॲसिड समृद्ध पीठ"),
        RationItem("Vitamin A & D Fortified Refined Oil", "1 Litre", "खाद्यतेल पाऊच"),
        RationItem("Fresh Eggs (Special Child Ration)", "8 units", "ताजी अंडी (प्रथिन पुरवठा)"),
        RationItem("Roasted Bengal Gram / Chana Dal", "1 kg", "भाजलेली चणा डाळ / पोषण खाऊ")
    ),
    val statusEn: String = "Upcoming (3 days left)",
    val statusMr: String = "आगामी (३ दिवस शिल्लक)",
    val statusHi: String = "आगामी (३ दिन शेष)"
)

data class RationItem(
    val name: String,
    val quantity: String,
    val note: String = ""
)
