package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChildProfile
import com.example.data.model.HealthRecord
import com.example.data.model.NutritionStatus
import com.example.data.model.VaccinationRecord
import com.example.data.model.VaccineStatus
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent? = null
)

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val requestAdapter = moshi.adapter(GeminiRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiResponse::class.java)

    /**
     * Generates a parent-friendly child growth and nutrition summary in Hindi & English
     */
    suspend fun generateGrowthSummary(
        profile: ChildProfile,
        latestRecord: HealthRecord?,
        previousRecords: List<HealthRecord>,
        vaccinations: List<VaccinationRecord>
    ): GrowthSummaryResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        val weightStr = latestRecord?.let { "${it.weightKg} kg" } ?: "Not recorded"
        val heightStr = latestRecord?.let { "${it.heightCm} cm" } ?: "Not recorded"
        val statusStr = latestRecord?.nutritionStatus?.name ?: "NORMAL"
        val attendanceStr = latestRecord?.let { "${it.attendanceDays} days" } ?: "Regular"
        val dueVaccines = vaccinations.filter { it.status == VaccineStatus.DUE }.joinToString { it.vaccineName }

        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.d(TAG, "No valid Gemini API key found, generating localized poshan guidance")
            return@withContext generateExpertLocalSummary(profile, latestRecord, previousRecords, dueVaccines)
        }

        val prompt = """
You are an expert, compassionate Anganwadi Poshan Nutritionist speaking to an Indian parent.
Analyze this child's health metrics:
- Child Name: ${profile.name}
- Age: ${profile.ageMonths} months (${profile.formattedAge})
- Gender: ${if (profile.gender == "M") "Boy" else "Girl"}
- Latest Weight: $weightStr
- Latest Height: $heightStr
- WHO Growth Status: $statusStr
- Anganwadi Monthly Attendance: $attendanceStr
- Due Vaccinations: ${if (dueVaccines.isEmpty()) "None (All up to date)" else dueVaccines}
- Anganwadi Center: ${profile.anganwadiCenterName}

Generate a clear, encouraging, parent-friendly growth summary structured with:
[HINDI - सरल हिंदी विवरण]:
1. स्वास्थ्य स्थिति: बच्चे के वजन और कद की वर्तमान स्थिति (सरल शब्दों में)।
2. पोषण सलाह (आहार): घर पर क्या खिलाएं (दलिया, मूंग दाल खिचड़ी, उबला अंडा/केला, हरी सब्जियां, आंगनवाड़ी पूरक पोषाहार)।
3. टीका और देखभाल: जरूरी टीके और साफ-सफाई।
4. अगली जांच की तारीख।

[ENGLISH SUMMARY]:
A concise, reassuring 3-4 sentence growth review, dietary recommendations, and follow-up guidance.
Keep the tone warm, clear, and actionable. Do not use complex medical jargon.
        """.trimIndent()

        try {
            val reqPayload = GeminiRequest(
                contents = listOf(
                    GeminiContent(parts = listOf(GeminiPart(text = prompt)))
                )
            )
            val jsonBody = requestAdapter.toJson(reqPayload)
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val parsed = responseAdapter.fromJson(responseBody)
                val fullText = parsed?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!fullText.isNullOrBlank()) {
                    return@withContext parseSummarySections(fullText, false)
                }
            } else {
                Log.w(TAG, "Gemini API call failed with code: ${response.code}, falling back to local engine")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Gemini API request", e)
        }

        // Graceful fallback to expert local engine
        generateExpertLocalSummary(profile, latestRecord, previousRecords, dueVaccines)
    }

    private fun parseSummarySections(fullText: String, isLocal: Boolean): GrowthSummaryResult {
        var hindiSection = ""
        var englishSection = ""

        if (fullText.contains("[HINDI", ignoreCase = true) && fullText.contains("[ENGLISH", ignoreCase = true)) {
            val parts = fullText.split(Regex("""\[ENGLISH[^]]*\]""", RegexOption.IGNORE_CASE))
            if (parts.size >= 2) {
                val hindiRaw = parts[0].replace(Regex("""\[HINDI[^]]*\]""", RegexOption.IGNORE_CASE), "").trim()
                val engRaw = parts[1].trim()
                hindiSection = hindiRaw
                englishSection = engRaw
            }
        }

        if (hindiSection.isBlank() || englishSection.isBlank()) {
            hindiSection = fullText
            englishSection = fullText
        }

        return GrowthSummaryResult(
            hindiSummary = hindiSection,
            englishSummary = englishSection,
            isAiGenerated = !isLocal
        )
    }

    /**
     * Highly authentic, clinical Poshan Abhiyaan expert guidelines fallback
     */
    private fun generateExpertLocalSummary(
        profile: ChildProfile,
        latestRecord: HealthRecord?,
        previousRecords: List<HealthRecord>,
        dueVaccines: String
    ): GrowthSummaryResult {
        val status = latestRecord?.nutritionStatus ?: NutritionStatus.NORMAL
        val name = profile.name
        val weight = latestRecord?.weightKg ?: 10.0f
        val height = latestRecord?.heightCm ?: 80.0f

        val (hindiText, engText) = when (status) {
            NutritionStatus.NORMAL -> {
                val hi = """
🌟 स्वास्थ्य स्थिति:
बधाई हो! ${name} की वृद्धि विश्व स्वास्थ्य संगठन (WHO) के मानकों के अनुसार बिल्कुल सामान्य और स्वस्थ है। वर्तमान वजन ${weight} किलो और लंबाई ${height} सेमी है।

🥣 पोषण आहार सलाह:
• बच्चे को दिन में 3-4 बार संतुलित भोजन दें (दलिया, खिचड़ी, गाढ़ी दाल, हरी पत्तेदार सब्जियां)।
• ऊर्जा के लिए भोजन में थोड़ा देशी घी या मक्खन मिलाएं।
• फल (केला, पपीता) और आंगनवाड़ी का गरम ताजा भोजन नियमित खिलाएं।

💉 टीका व देखभाल:
${if (dueVaccines.isNotEmpty()) "⚠️ ध्यान दें: $dueVaccines का टीका नियत समय पर लगवाएं।" else "✅ सभी जरूरी टीके समय पर लगे हैं।"}
हाथ धोने की अच्छी आदतें सिखाएं और शुद्ध उबला पानी पिलाएं।

📅 अगली जांच:
अगले महीने आंगनवाड़ी में मासिक वजन दिवस पर जांच अवश्य कराएं।
                """.trimIndent()

                val en = """
🌟 Health Overview:
Great news! ${name}'s growth is within the normal healthy range as per WHO growth charts. Current weight is ${weight} kg and height is ${height} cm.

🥣 Diet & Nutrition Plan:
• Continue offering 3-4 nutrient-dense meals a day including dalia, lentil khichdi, eggs, and mashed vegetables.
• Add a teaspoon of ghee/oil for healthy energy density.
• Ensure daily participation in Anganwadi hot cooked meals.

💉 Vaccination & Care:
${if (dueVaccines.isNotEmpty()) "⚠️ Pending vaccines: $dueVaccines. Please schedule at the Anganwadi center." else "✅ Vaccinations are currently up to date."}
Maintain clean drinking water and hand-hygiene habits. Next monthly weight check scheduled in 30 days.
                """.trimIndent()
                hi to en
            }

            NutritionStatus.MAM -> {
                val hi = """
⚠️ स्वास्थ्य स्थिति (मध्यम कुपोषण - MAM):
${name} का वजन उम्र के सामान्य मानक से थोड़ा कम है (वर्तमान वजन: ${weight} kg, लंबाई: ${height} cm)। थोड़ी अतिरिक्त देखभाल से बच्चा शीघ्र सामान्य श्रेणी में आ सकता है।

🥣 विशेष पोषण आहार:
• आंगनवाड़ी से मिलने वाला टेक-होम राशन (THR) पोषाहार पैकेट प्रतिदिन दें।
• भोजन की बारंबारता बढ़ाएं—दिन में 4 से 5 बार थोड़ा-थोड़ा पौष्टिक आहार दें।
• मूंगफली का पेस्ट, सूजी/दलिया की गाढ़ी खीर, उबला अंडा और केला जरूर खिलाएं।
• पानी उबालकर ठंडा करके ही पिलाएं।

💉 टीका व स्वास्थ्य निगरानी:
${if (dueVaccines.isNotEmpty()) "⚠️ लंबित टीका: $dueVaccines तुरंत लगवाएं।" else "✅ टीकाकरण नियमित रखें।"}
हर 15 दिन में आंगनवाड़ी कार्यकर्ता दीदी से वजन अवश्य नपवाएं।
                """.trimIndent()

                val en = """
⚠️ Health Status (Moderate Acute Malnutrition - MAM):
${name}'s weight is slightly below expected WHO milestones for ${profile.ageMonths} months (Weight: ${weight} kg, Height: ${height} cm). With targeted dietary enrichment, recovery is fast.

🥣 Dietary Action Plan:
• Provide energy-dense supplementary nutrition from Anganwadi Take-Home Rations (THR).
• Feed 5 times daily with smaller portions enriched with ghee, crushed peanuts, or boiled eggs.
• Ensure safe boiled drinking water to prevent diarrhea.

💉 Follow-up & Care:
${if (dueVaccines.isNotEmpty()) "⚠️ Overdue vaccine: $dueVaccines. Administer at earliest." else "✅ Ensure ongoing vaccine adherence."}
Bi-weekly weight monitoring is scheduled at Kalyanpuri Anganwadi.
                """.trimIndent()
                hi to en
            }

            NutritionStatus.SAM -> {
                val hi = """
🚨 विशेष ध्यान आवश्यक (गंभीर कुपोषण - SAM):
${name} का वजन मानक से काफी कम है (वर्तमान वजन: ${weight} kg)। बच्चे को तुरंत विशेष पोषण और चिकित्सीय सहायता की आवश्यकता है।

🏥 तत्काल प्राथमिक कदम:
• आंगनवाड़ी कार्यकर्ता या आशा दीदी की मदद से तुरंत नजदीकी पोषण पुनर्वास केंद्र (NRC / PHC) में संपर्क करें।
• डॉक्टर द्वारा अनुशंसित उपचारात्मक आहार (Therapeutic Feed / F-75) शुरू करें।
• बच्चे को हर 2 घंटे में स्तनपान/आहार और ओआरएस घोल दें।

🥣 घर पर देखभाल:
• बच्चे को ठंड से बचाकर गर्म रखें।
• साबुन से हाथ धोकर ही भोजन कराएं।
• कोई भी संक्रमण या दस्त होने पर तुरंत अस्पताल ले जाएं।

📅 निगरानी: साप्ताहिक वजन जांच अनिवार्य है।
                """.trimIndent()

                val en = """
🚨 Priority Attention Required (Severe Acute Malnutrition - SAM):
${name}'s weight is significantly below the WHO standard curve (Weight: ${weight} kg, Height: ${height} cm). Urgent nutritional rehabilitation is recommended.

🏥 Immediate Actions:
• Consult the Nutrition Rehabilitation Centre (NRC) or Medical Officer through the Anganwadi worker.
• Administer prescribed Ready-to-Use Therapeutic Food (RUTF) or enriched therapeutic formula.
• Keep child warm and ensure clean, sanitized feeding utensils.

📅 Follow-up: Weekly weight monitoring and pediatric review is mandatory.
                """.trimIndent()
                hi to en
            }
        }

        return GrowthSummaryResult(
            hindiSummary = hindiText,
            englishSummary = engText,
            isAiGenerated = false
        )
    }
}

data class GrowthSummaryResult(
    val hindiSummary: String,
    val englishSummary: String,
    val isAiGenerated: Boolean = false
)
