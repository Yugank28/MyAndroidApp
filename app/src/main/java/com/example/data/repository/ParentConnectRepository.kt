package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.local.AppDatabase
import com.example.data.local.ChildDao
import com.example.data.model.AnnouncementCategory
import com.example.data.model.CentreAnnouncement
import com.example.data.model.ChildProfile
import com.example.data.model.DiffImportResult
import com.example.data.model.HealthRecord
import com.example.data.model.NutritionStatus
import com.example.data.model.ParsedChildRecord
import com.example.data.model.RationSchedule
import com.example.data.model.SessionRole
import com.example.data.model.UserSession
import com.example.data.model.VaccinationRecord
import com.example.data.parser.CsvParser
import com.example.data.parser.SampleDataGenerator
import com.example.data.remote.GeminiService
import com.example.data.remote.GrowthSummaryResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

interface ParentConnectRepository {
    val currentSession: Flow<UserSession>

    suspend fun loginAdmin(username: String, pin: String): Result<UserSession>
    suspend fun loginParent(mobile: String, code: String): Result<UserSession>
    fun logout()

    fun getChildProfile(childId: String): Flow<ChildProfile?>
    fun getChildHealthRecords(childId: String): Flow<List<HealthRecord>>
    fun getChildVaccinations(childId: String): Flow<List<VaccinationRecord>>
    fun getAllChildren(): Flow<List<ChildProfile>>

    fun getRationSchedules(): Flow<List<RationSchedule>>
    fun getAnnouncements(): Flow<List<CentreAnnouncement>>
    fun getAttendanceMap(): Flow<Map<String, Boolean>>

    suspend fun toggleAttendance(childId: String, isPresent: Boolean)
    suspend fun dispatchRationSchedule(schedule: RationSchedule)
    suspend fun dispatchAnnouncement(announcement: CentreAnnouncement)

    suspend fun parseAndValidateUpload(uri: Uri, isExcel: Boolean): DiffImportResult
    suspend fun parseSampleExcel(): DiffImportResult
    suspend fun commitImport(diff: DiffImportResult): Result<Int>
    fun getTemplateCsvContent(): String

    suspend fun getAiGrowthAdvice(childId: String, forceRefresh: Boolean = false): GrowthSummaryResult
    suspend fun resetDatabaseToInitial()
}

class ParentConnectRepositoryImpl(
    private val context: Context,
    private val dao: ChildDao
) : ParentConnectRepository {

    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        null
    }

    init {
        try {
            firestore?.collection("announcements")
                ?.addSnapshotListener { snapshot, _ ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        val list = snapshot.toObjects(CentreAnnouncement::class.java)
                        if (list.isNotEmpty()) {
                            _announcements.value = list
                        }
                    }
                }
        } catch (_: Exception) {}
    }

    private val _currentSession = MutableStateFlow(UserSession())
    override val currentSession: Flow<UserSession> = _currentSession.asStateFlow()

    private val _attendanceMap = MutableStateFlow<Map<String, Boolean>>(SampleDataGenerator.initialAttendanceMap)
    override fun getAttendanceMap(): Flow<Map<String, Boolean>> = _attendanceMap.asStateFlow()

    private val _rationSchedules = MutableStateFlow(
        listOf(
            RationSchedule(
                id = "RATION-2026-10",
                cycleMonth = "October 2026",
                distributionDate = "24-Oct-2026",
                pickupTiming = "08:30 AM – 01:00 PM",
                location = "AW-Center-042 (Shivaji Nagar Community Hall)",
                sevikaContact = "Rekha Tai Patil (+91 9822001122)",
                statusEn = "Upcoming (Pickup on Saturday)",
                statusMr = "आगामी (शनिवार रोजी वाटप)",
                statusHi = "आगामी (शनिवार को वितरण)"
            )
        )
    )
    override fun getRationSchedules(): Flow<List<RationSchedule>> = _rationSchedules.asStateFlow()

    private val _announcements = MutableStateFlow(
        listOf(
            CentreAnnouncement(
                id = "ANN-01",
                titleEn = "Take-Home Ration (THR) Distribution on 24-Oct",
                titleMr = "२४ ऑक्टोबर रोजी पोषण आहार (THR) वाटप",
                titleHi = "२४ अक्टूबर को टेक-होम राशन (THR) वितरण",
                descriptionEn = "All registered parents must bring their ration card between 8:30 AM and 1:00 PM to receive THR packets, fortified oil, and eggs.",
                descriptionMr = "सर्व नोंदणीकृत पालकांनी सकाळी ८:३० ते दुपारी १:०० या वेळेत उपस्थित राहून पोषण आहार पाकिटे, तेल व अंडी घेऊन जावे.",
                descriptionHi = "सभी पंजीकृत अभिभावक सुबह ८:३० से दोपहर १:०० बजे के बीच केंद्र पर आकर पोषण आहार पैकेट, तेल व अंडे प्राप्त करें।",
                date = "18-Oct-2026",
                category = AnnouncementCategory.RATION,
                isUrgent = true
            ),
            CentreAnnouncement(
                id = "ANN-02",
                titleEn = "Quarterly Pediatric & Immunization Camp",
                titleMr = "त्रैमासिक बालरोग व लसीकरण शिबीर",
                titleHi = "त्रैमासिक बाल स्वास्थ्य एवं टीकाकरण शिविर",
                descriptionEn = "Dr. Shinde (PHC Medical Officer) will conduct routine physical growth exams and administer due DPT/Measles boosters on 28-Oct.",
                descriptionMr = "दिनांक २८ ऑक्टोबर रोजी प्राथमिक आरोग्य केंद्राचे वैद्यकीय अधिकारी मुलांची शारीरिक तपासणी व प्रलंबित लसीकरण करतील.",
                descriptionHi = "२८ अक्टूबर को प्राथमिक स्वास्थ्य केंद्र के चिकित्सक बच्चों की स्वास्थ्य जांच और छूटे हुए टीके लगाएंगे।",
                date = "16-Oct-2026",
                category = AnnouncementCategory.HEALTH,
                isUrgent = false
            ),
            CentreAnnouncement(
                id = "ANN-03",
                titleEn = "Suposhan Diwas (MAM Special Nutrition Counseling)",
                titleMr = "सुपोषण दिवस (MAM मुलांसाठी विशेष आहार मार्गदर्शन)",
                titleHi = "सुपोषण दिवस (MAM बच्चों हेतु विशेष पोषण परामर्श)",
                descriptionEn = "Special cooking demonstration for mothers: Preparing energy-dense khichdi and peanut-jaggery laddoos for rapid healthy weight gain.",
                descriptionMr = "मातांसाठी ऊर्जासंवर्धक मूग डाळ खिचडी व शेंगदाणा-गूळ लाडू बनवण्याचे प्रात्यक्षिक दाखवले जाईल.",
                descriptionHi = "माताओं के लिए मूंग दाल खिचड़ी एवं मूंगफली-गुड़ के लड्डू बनाने का विशेष प्रदर्शन आयोजित किया जाएगा।",
                date = "14-Oct-2026",
                category = AnnouncementCategory.NUTRITION,
                isUrgent = false
            )
        )
    )
    override fun getAnnouncements(): Flow<List<CentreAnnouncement>> = _announcements.asStateFlow()

    private val summaryCache = ConcurrentHashMap<String, GrowthSummaryResult>()

    override suspend fun loginAdmin(username: String, pin: String): Result<UserSession> = withContext(Dispatchers.Default) {
        val cleanUser = username.trim()
        val cleanPin = pin.trim()
        if (cleanUser == SampleDataGenerator.ADMIN_USERNAME && cleanPin == SampleDataGenerator.ADMIN_PASSWORD) {
            val session = UserSession(
                role = SessionRole.ADMIN,
                username = cleanUser,
                displayName = SampleDataGenerator.ADMIN_NAME,
                centerId = "AW-Center-042",
                centerName = SampleDataGenerator.ADMIN_CENTER,
                authenticatedChildId = null
            )
            _currentSession.value = session
            Result.success(session)
        } else {
            Result.failure(IllegalArgumentException("Invalid Admin Username or PIN. (Use: admin_aw042 / 123456)"))
        }
    }

    override suspend fun loginParent(mobile: String, code: String): Result<UserSession> = withContext(Dispatchers.IO) {
        val cleanMobile = mobile.trim()
        val cleanCode = code.trim().uppercase()

        // Match against database records
        val matchedChild = dao.verifyParentDirect(cleanMobile, cleanCode, cleanCode)
            ?: dao.getAllChildren().firstOrNull()?.firstOrNull { child ->
                (child.parentContact == cleanMobile && child.regCode.equals(cleanCode, ignoreCase = true)) ||
                child.childId.equals(cleanCode, ignoreCase = true) ||
                child.regCode.equals(cleanCode, ignoreCase = true)
            }

        if (matchedChild != null) {
            val session = UserSession(
                role = SessionRole.PARENT,
                username = matchedChild.parentContact,
                displayName = "${matchedChild.guardian} (${matchedChild.name}'s Parent)",
                centerId = matchedChild.anganwadiCenterId,
                centerName = matchedChild.anganwadiCenterName,
                authenticatedChildId = matchedChild.childId
            )
            _currentSession.value = session
            Result.success(session)
        } else {
            Result.failure(IllegalArgumentException("No child found matching Mobile '$cleanMobile' and Code '$cleanCode'. Check credentials issued by Anganwadi."))
        }
    }

    override fun logout() {
        _currentSession.value = UserSession()
    }

    override fun getChildProfile(childId: String): Flow<ChildProfile?> = dao.getChildById(childId)

    override fun getChildHealthRecords(childId: String): Flow<List<HealthRecord>> = dao.getHealthRecords(childId)

    override fun getChildVaccinations(childId: String): Flow<List<VaccinationRecord>> = dao.getVaccinations(childId)

    override fun getAllChildren(): Flow<List<ChildProfile>> = dao.getAllChildren()

    override suspend fun toggleAttendance(childId: String, isPresent: Boolean) = withContext(Dispatchers.Default) {
        val map = _attendanceMap.value.toMutableMap()
        map[childId] = isPresent
        _attendanceMap.value = map
    }

    override suspend fun dispatchRationSchedule(schedule: RationSchedule) = withContext(Dispatchers.Default) {
        val current = _rationSchedules.value.toMutableList()
        current.add(0, schedule)
        _rationSchedules.value = current
    }

    override suspend fun dispatchAnnouncement(announcement: CentreAnnouncement) {
        withContext(Dispatchers.Default) {
            val current = _announcements.value.toMutableList()
            current.add(0, announcement)
            _announcements.value = current
        }

        try {
            firestore?.collection("announcements")
                ?.document(announcement.id)
                ?.set(announcement)
        } catch (_: Exception) {}
    }

    override suspend fun parseAndValidateUpload(uri: Uri, isExcel: Boolean): DiffImportResult = withContext(Dispatchers.IO) {
        val parseResult = CsvParser.parseUri(context, uri)
        buildDiffResult(parseResult.successfulRecords, parseResult.errors, uri.lastPathSegment ?: "records.xlsx")
    }

    override suspend fun parseSampleExcel(): DiffImportResult = withContext(Dispatchers.IO) {
        val parseResult = CsvParser.parseText(SampleDataGenerator.sampleCsvTemplate)
        buildDiffResult(parseResult.successfulRecords, parseResult.errors, "Anganwadi_Child_Service_Template.xlsx")
    }

    private suspend fun buildDiffResult(
        parsed: List<ParsedChildRecord>,
        errors: List<String>,
        fileName: String
    ): DiffImportResult {
        val existingChildren = dao.getAllChildren().firstOrNull() ?: emptyList()
        val existingMap = existingChildren.associateBy { it.childId }

        val newRecords = mutableListOf<ParsedChildRecord>()
        val updatedRecords = mutableListOf<ParsedChildRecord>()
        val duplicateIds = mutableListOf<String>()
        val seenInFile = mutableSetOf<String>()

        for (item in parsed) {
            if (seenInFile.contains(item.childId)) {
                duplicateIds.add(item.childId)
            }
            seenInFile.add(item.childId)

            if (existingMap.containsKey(item.childId)) {
                updatedRecords.add(item.copy(isUpdate = true))
            } else {
                newRecords.add(item.copy(isUpdate = false))
            }
        }

        return DiffImportResult(
            fileName = fileName,
            totalRows = parsed.size,
            newRecords = newRecords,
            updatedRecords = updatedRecords,
            duplicateIds = duplicateIds,
            syntaxErrors = errors
        )
    }

    override suspend fun commitImport(diff: DiffImportResult): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val allToCommit = diff.newRecords + diff.updatedRecords
            var count = 0
            for (item in allToCommit) {
                val existing = dao.getChildByIdDirect(item.childId)
                val profile = ChildProfile(
                    id = existing?.id ?: 0,
                    childId = item.childId,
                    name = item.name,
                    dob = item.dob.ifEmpty { existing?.dob ?: "14-Aug-2023" },
                    ageMonths = item.ageMonths,
                    gender = item.gender,
                    parentName = item.guardian,
                    guardian = item.guardian,
                    parentContact = item.parentContact.ifEmpty { existing?.parentContact ?: "" },
                    regCode = item.regCode.ifEmpty { existing?.regCode ?: "ACT-AUTO-AW" },
                    anganwadiCenterId = "AW-Center-042",
                    anganwadiCenterName = "AW-Center-042 (Shivaji Nagar)",
                    address = existing?.address ?: "Shivaji Nagar",
                    nextVaccine = item.nextVaccine.ifEmpty { existing?.nextVaccine ?: "DPT Booster" },
                    avatarColorIndex = (item.childId.hashCode() and 0x7FFFFFFF) % 5
                )
                dao.upsertProfile(profile)

                val healthRecord = HealthRecord(
                    childId = item.childId,
                    date = item.date,
                    weightKg = item.weightKg,
                    heightCm = item.heightCm,
                    nutritionStatus = item.nutritionStatus,
                    attendanceDays = item.attendanceCount,
                    notes = item.notes
                )
                dao.insertHealthRecord(healthRecord)
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTemplateCsvContent(): String = SampleDataGenerator.sampleCsvTemplate

    override suspend fun getAiGrowthAdvice(childId: String, forceRefresh: Boolean): GrowthSummaryResult = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            summaryCache[childId]?.let { return@withContext it }
        }

        val profile = dao.getChildByIdDirect(childId)
            ?: return@withContext GrowthSummaryResult("रेकॉर्ड सापडला नाही", "Record not found")
        val latest = dao.getLatestHealthRecordDirect(childId)
        val allRecords = dao.getHealthRecords(childId).firstOrNull() ?: emptyList()
        val vaccinations = dao.getVaccinations(childId).firstOrNull() ?: emptyList()

        val summary = GeminiService.generateGrowthSummary(
            profile = profile,
            latestRecord = latest,
            previousRecords = allRecords,
            vaccinations = vaccinations
        )
        summaryCache[childId] = summary
        summary
    }

    override suspend fun resetDatabaseToInitial() = withContext(Dispatchers.IO) {
        dao.clearAllProfiles()
        dao.clearAllHealthRecords()
        dao.clearAllVaccinations()

        dao.upsertProfiles(SampleDataGenerator.getInitialProfiles())
        dao.insertHealthRecords(SampleDataGenerator.getInitialHealthRecords())
        dao.insertVaccinations(SampleDataGenerator.getInitialVaccinations())
        summaryCache.clear()
        _attendanceMap.value = SampleDataGenerator.initialAttendanceMap
    }

    companion object {
        @Volatile
        private var instance: ParentConnectRepository? = null

        fun getInstance(context: Context): ParentConnectRepository {
            return instance ?: synchronized(this) {
                val db = AppDatabase.getDatabase(context)
                val newInstance = ParentConnectRepositoryImpl(context.applicationContext, db.childDao())
                instance = newInstance
                newInstance
            }
        }
    }
}
