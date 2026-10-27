package com.example.data.parser

import com.example.data.model.ChildProfile
import com.example.data.model.HealthRecord
import com.example.data.model.NutritionStatus
import com.example.data.model.VaccinationRecord
import com.example.data.model.VaccineStatus

object SampleDataGenerator {

    // Admin Credentials defined in PRD v1.2
    const val ADMIN_USERNAME = "admin_aw042"
    const val ADMIN_PASSWORD = "123456"
    const val ADMIN_NAME = "Rekha Tai Patil (Anganwadi Sevika)"
    const val ADMIN_CENTER = "AW-Center-042 (Shivaji Nagar)"

    val sampleCsvTemplate = """
child_id,name,dob,age_months,gender,guardian,parent_contact,reg_code,weight_kg,height_cm,nutrition_status,next_vaccine,attendance_count,date,notes
AW-042-04,Ishaan Nilesh Kadam,10-Mar-2024,19,M,Nilesh Kadam,9823112233,ACT-4455-IK,10.6,82.0,Normal,Vitamin A Dose 2,21,2026-10-18,Healthy appetite
AW-042-05,Tanvi Santosh Jadhav,18-Jun-2023,28,F,Savita Jadhav,9834223344,ACT-6677-TJ,9.4,80.5,MAM,MR Booster,17,2026-10-18,Prescribed additional THR packet
    """.trimIndent()

    val sampleXmlText = """
<?xml version="1.0" encoding="utf-8"?>
<children>
    <child>
        <child_id>AW-042-06</child_id>
        <name>Meera Yadav</name>
        <age_months>20</age_months>
        <weight_kg>9.2</weight_kg>
        <height_cm>80.1</height_cm>
        <nutrition_status>MAM</nutrition_status>
        <vaccination_status>DPT Booster due</vaccination_status>
        <attendance_count>19</attendance_count>
        <date>2026-10-18</date>
        <gender>F</gender>
        <parent_contact>9856789012</parent_contact>
        <guardian>Sunil Yadav</guardian>
        <center_name>AW-Center-042 (Shivaji Nagar)</center_name>
        <notes>Provided extra egg and poshan packet</notes>
    </child>
    <child>
        <child_id>AW-042-07</child_id>
        <name>Rohan Das</name>
        <age_months>12</age_months>
        <weight_kg>9.6</weight_kg>
        <height_cm>75.5</height_cm>
        <nutrition_status>Normal</nutrition_status>
        <vaccination_status>Up to date</vaccination_status>
        <attendance_count>23</attendance_count>
        <date>2026-10-18</date>
        <gender>M</gender>
        <parent_contact>9867890123</parent_contact>
        <guardian>Amit Das</guardian>
        <center_name>AW-Center-042 (Shivaji Nagar)</center_name>
        <notes>1-year milestone achieved successfully</notes>
    </child>
</children>
    """.trimIndent()

    // 3 Linked Children & Guardian Profiles specified in PRD v1.2
    fun getInitialProfiles(): List<ChildProfile> = listOf(
        ChildProfile(
            childId = "AW-042-01",
            name = "Aarav Sachin Gaikwad",
            dob = "14-Aug-2023",
            ageMonths = 26,
            gender = "M",
            parentName = "Sachin Gaikwad",
            guardian = "Sachin Gaikwad",
            parentContact = "9890123456",
            regCode = "ACT-7842-AG",
            anganwadiCenterId = "AW-Center-042",
            anganwadiCenterName = "AW-Center-042 (Shivaji Nagar)",
            address = "Galli No 3, Shivaji Nagar",
            nextVaccine = "DPT Booster (12-Nov-2026)",
            avatarColorIndex = 0
        ),
        ChildProfile(
            childId = "AW-042-02",
            name = "Ananya Rahul Meshram",
            dob = "05-Feb-2024",
            ageMonths = 20,
            gender = "F",
            parentName = "Snehal Meshram",
            guardian = "Snehal Meshram",
            parentContact = "9765432109",
            regCode = "ACT-9912-AM",
            anganwadiCenterId = "AW-Center-042",
            anganwadiCenterName = "AW-Center-042 (Shivaji Nagar)",
            address = "Plot 14, Near Samaj Mandir, Shivaji Nagar",
            nextVaccine = "Measles 2nd (20-Dec-2026)",
            avatarColorIndex = 1
        ),
        ChildProfile(
            childId = "AW-042-03",
            name = "Vihaan Pramod Shinde",
            dob = "20-Nov-2022",
            ageMonths = 35,
            gender = "M",
            parentName = "Pramod Shinde",
            guardian = "Pramod Shinde",
            parentContact = "9422187654",
            regCode = "ACT-3301-VS",
            anganwadiCenterId = "AW-Center-042",
            anganwadiCenterName = "AW-Center-042 (Shivaji Nagar)",
            address = "House 82, Bazar Galli, Shivaji Nagar",
            nextVaccine = "All doses complete",
            avatarColorIndex = 2
        )
    )

    // 6 Months of historical measurements for smooth line chart trajectories
    fun getInitialHealthRecords(): List<HealthRecord> = listOf(
        // Child 1: Aarav Sachin Gaikwad (Normal - 12.8 kg, 91 cm)
        HealthRecord(childId = "AW-042-01", date = "2026-05-14", weightKg = 11.4f, heightCm = 85.0f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 22, notes = "Normal milestone"),
        HealthRecord(childId = "AW-042-01", date = "2026-06-15", weightKg = 11.8f, heightCm = 86.2f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 23, notes = "Steady weight gain"),
        HealthRecord(childId = "AW-042-01", date = "2026-07-14", weightKg = 12.0f, heightCm = 87.5f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 21, notes = "Good cognitive skills"),
        HealthRecord(childId = "AW-042-01", date = "2026-08-16", weightKg = 12.3f, heightCm = 88.8f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 24, notes = "Active in early learning"),
        HealthRecord(childId = "AW-042-01", date = "2026-09-15", weightKg = 12.5f, heightCm = 89.9f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 23, notes = "Healthy nutrition"),
        HealthRecord(childId = "AW-042-01", date = "2026-10-14", weightKg = 12.8f, heightCm = 91.0f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 24, notes = "Normal healthy milestone (12.8 kg, 91 cm)"),

        // Child 2: Ananya Rahul Meshram (MAM - 8.5 kg, 74 cm)
        HealthRecord(childId = "AW-042-02", date = "2026-05-12", weightKg = 7.7f, heightCm = 70.0f, nutritionStatus = NutritionStatus.MAM, attendanceDays = 16, notes = "Identified under MAM category"),
        HealthRecord(childId = "AW-042-02", date = "2026-06-14", weightKg = 7.9f, heightCm = 71.0f, nutritionStatus = NutritionStatus.MAM, attendanceDays = 17, notes = "Provided additional egg & THR dalia"),
        HealthRecord(childId = "AW-042-02", date = "2026-07-12", weightKg = 8.0f, heightCm = 71.8f, nutritionStatus = NutritionStatus.MAM, attendanceDays = 18, notes = "Mother attended Poshan counseling"),
        HealthRecord(childId = "AW-042-02", date = "2026-08-14", weightKg = 8.2f, heightCm = 72.6f, nutritionStatus = NutritionStatus.MAM, attendanceDays = 19, notes = "Weight improving slowly"),
        HealthRecord(childId = "AW-042-02", date = "2026-09-12", weightKg = 8.3f, heightCm = 73.2f, nutritionStatus = NutritionStatus.MAM, attendanceDays = 18, notes = "Energy-rich supplementary feed continued"),
        HealthRecord(childId = "AW-042-02", date = "2026-10-12", weightKg = 8.5f, heightCm = 74.0f, nutritionStatus = NutritionStatus.MAM, attendanceDays = 20, notes = "Current: 8.5 kg, 74 cm. On continuous MAM care"),

        // Child 3: Vihaan Pramod Shinde (Normal - 14.1 kg, 98 cm)
        HealthRecord(childId = "AW-042-03", date = "2026-05-10", weightKg = 13.0f, heightCm = 93.0f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 23, notes = "Very active"),
        HealthRecord(childId = "AW-042-03", date = "2026-06-11", weightKg = 13.2f, heightCm = 94.2f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 22, notes = "Good appetite and social play"),
        HealthRecord(childId = "AW-042-03", date = "2026-07-10", weightKg = 13.5f, heightCm = 95.0f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 24, notes = "Participates in preschool activities"),
        HealthRecord(childId = "AW-042-03", date = "2026-08-12", weightKg = 13.7f, heightCm = 96.1f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 25, notes = "Healthy weight curve"),
        HealthRecord(childId = "AW-042-03", date = "2026-09-10", weightKg = 13.9f, heightCm = 97.0f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 24, notes = "All immunizations updated"),
        HealthRecord(childId = "AW-042-03", date = "2026-10-10", weightKg = 14.1f, heightCm = 98.0f, nutritionStatus = NutritionStatus.NORMAL, attendanceDays = 25, notes = "Current: 14.1 kg, 98 cm. Fully normal")
    )

    fun getInitialVaccinations(): List<VaccinationRecord> = listOf(
        // Aarav AW-042-01 (Next Vaccine: DPT Booster 12-Nov-2026)
        VaccinationRecord(childId = "AW-042-01", vaccineName = "BCG", dose = "Birth Dose", dueDate = "14-Aug-2023", status = VaccineStatus.COMPLETED, administeredDate = "15-Aug-2023"),
        VaccinationRecord(childId = "AW-042-01", vaccineName = "OPV (0, 1, 2, 3)", dose = "Birth, 6, 10, 14 wks", dueDate = "20-Nov-2023", status = VaccineStatus.COMPLETED, administeredDate = "22-Nov-2023"),
        VaccinationRecord(childId = "AW-042-01", vaccineName = "Pentavalent (1, 2, 3)", dose = "6, 10, 14 weeks", dueDate = "20-Nov-2023", status = VaccineStatus.COMPLETED, administeredDate = "22-Nov-2023"),
        VaccinationRecord(childId = "AW-042-01", vaccineName = "Rotavirus & fIPV", dose = "6 & 14 weeks", dueDate = "20-Nov-2023", status = VaccineStatus.COMPLETED, administeredDate = "22-Nov-2023"),
        VaccinationRecord(childId = "AW-042-01", vaccineName = "Measles-Rubella (MR 1st)", dose = "9 - 12 Months", dueDate = "15-May-2024", status = VaccineStatus.COMPLETED, administeredDate = "18-May-2024"),
        VaccinationRecord(childId = "AW-042-01", vaccineName = "Vitamin A (1st Dose)", dose = "9 Months", dueDate = "15-May-2024", status = VaccineStatus.COMPLETED, administeredDate = "18-May-2024"),
        VaccinationRecord(childId = "AW-042-01", vaccineName = "DPT Booster 1st", dose = "16 - 24 Months", dueDate = "12-Nov-2026", status = VaccineStatus.DUE),

        // Ananya AW-042-02 (Next Vaccine: Measles 2nd 20-Dec-2026)
        VaccinationRecord(childId = "AW-042-02", vaccineName = "BCG & Hep B-0", dose = "At Birth", dueDate = "05-Feb-2024", status = VaccineStatus.COMPLETED, administeredDate = "06-Feb-2024"),
        VaccinationRecord(childId = "AW-042-02", vaccineName = "Pentavalent (1, 2, 3)", dose = "6, 10, 14 weeks", dueDate = "20-May-2024", status = VaccineStatus.COMPLETED, administeredDate = "22-May-2024"),
        VaccinationRecord(childId = "AW-042-02", vaccineName = "Measles-Rubella (MR 1st)", dose = "9 Months", dueDate = "10-Nov-2024", status = VaccineStatus.COMPLETED, administeredDate = "12-Nov-2024"),
        VaccinationRecord(childId = "AW-042-02", vaccineName = "Measles 2nd / MR 2nd", dose = "16 - 24 Months", dueDate = "20-Dec-2026", status = VaccineStatus.DUE),

        // Vihaan AW-042-03 (All doses complete)
        VaccinationRecord(childId = "AW-042-03", vaccineName = "BCG & OPV-0", dose = "At Birth", dueDate = "20-Nov-2022", status = VaccineStatus.COMPLETED, administeredDate = "21-Nov-2022"),
        VaccinationRecord(childId = "AW-042-03", vaccineName = "Pentavalent (1, 2, 3)", dose = "6, 10, 14 weeks", dueDate = "05-Mar-2023", status = VaccineStatus.COMPLETED, administeredDate = "05-Mar-2023"),
        VaccinationRecord(childId = "AW-042-03", vaccineName = "MR 1st & Vitamin A", dose = "9 Months", dueDate = "25-Aug-2023", status = VaccineStatus.COMPLETED, administeredDate = "25-Aug-2023"),
        VaccinationRecord(childId = "AW-042-03", vaccineName = "DPT Booster & MR 2nd", dose = "16 - 24 Months", dueDate = "20-May-2024", status = VaccineStatus.COMPLETED, administeredDate = "22-May-2024")
    )

    // Daily Attendance Records for today
    val initialAttendanceMap = mapOf(
        "AW-042-01" to true,
        "AW-042-02" to true,
        "AW-042-03" to false
    )
}
