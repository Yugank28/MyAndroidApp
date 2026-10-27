package com.example.ui.parent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CentreAnnouncement
import com.example.data.model.ChildProfile
import com.example.data.model.HealthRecord
import com.example.data.model.NutritionStatus
import com.example.data.model.RationSchedule
import com.example.data.model.VaccinationRecord
import com.example.ui.components.GrowthChart
import com.example.ui.components.NutritionBadge
import com.example.ui.components.VaccinationCard
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.AppStrings
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.PrimaryIndigoContainer
import com.example.ui.theme.PrimaryIndigoOnContainer
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.StatusMAM
import com.example.ui.theme.StatusNormal
import com.example.ui.theme.StatusSAM
import com.example.ui.viewmodel.AiAdviceState
import com.example.ui.viewmodel.PoshanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentHubScreen(
    viewModel: PoshanViewModel,
    modifier: Modifier = Modifier
) {
    val language by viewModel.appLanguage.collectAsState()
    val session by viewModel.userSession.collectAsState()
    val child by viewModel.selectedChild.collectAsState()
    val healthRecords by viewModel.childHealthRecords.collectAsState()
    val vaccinations by viewModel.childVaccinations.collectAsState()
    val rationSchedules by viewModel.rationSchedules.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val aiAdviceState by viewModel.aiAdviceState.collectAsState()

    var aiDisplayLang by remember { mutableStateOf(language) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = AppStrings.appTitle(language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        )
                        Text(
                            text = session.centerName,
                            fontSize = 11.sp,
                            color = PrimaryIndigo,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                actions = {
                    // Trilingual Switcher Chip
                    FilterChip(
                        selected = true,
                        onClick = {
                            val nextLang = when (language) {
                                AppLanguage.MARATHI -> AppLanguage.HINDI
                                AppLanguage.HINDI -> AppLanguage.ENGLISH
                                AppLanguage.ENGLISH -> AppLanguage.MARATHI
                            }
                            viewModel.setLanguage(nextLang)
                        },
                        label = {
                            Text(
                                text = "🌐 ${language.displayName}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryIndigoContainer,
                            selectedLabelColor = PrimaryIndigo
                        ),
                        modifier = Modifier.testTag("parent_lang_toggle")
                    )

                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("parent_logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Logout",
                            tint = Slate600
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Slate50)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Offline-First Sync Badge Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusNormal)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.offlineSyncStatus(language),
                        fontSize = 11.sp,
                        color = StatusNormal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Encrypted Family Session",
                    fontSize = 10.sp,
                    color = Slate600
                )
            }

            if (child == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryIndigo)
                }
            } else {
                val profile = child!!
                val latestRecord = healthRecords.lastOrNull()
                val status = latestRecord?.nutritionStatus ?: NutritionStatus.NORMAL

                // 1. "Next Important Activity" Alert Hero Card
                NextActivityHeroCard(
                    profile = profile,
                    rationSchedule = rationSchedules.firstOrNull(),
                    language = language
                )

                // 2. Child Identity Summary Card
                ChildIdentitySummaryCard(
                    profile = profile,
                    latestRecord = latestRecord,
                    status = status,
                    language = language
                )

                // 3. Quick Growth Metric Summary Row (Weight, Height, Attendance)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricMiniCard(
                        title = if (language == AppLanguage.MARATHI) "वजन (Weight)" else "Weight",
                        value = latestRecord?.let { "${it.weightKg} kg" } ?: "${profile.id} kg",
                        sub = if (language == AppLanguage.MARATHI) "मागील तपासणी" else "Latest record",
                        icon = Icons.Default.Scale,
                        color = PrimaryIndigo,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = if (language == AppLanguage.MARATHI) "उंची (Height)" else "Height",
                        value = latestRecord?.let { "${it.heightCm} cm" } ?: "91 cm",
                        sub = if (language == AppLanguage.MARATHI) "मानकानुसार" else "WHO standard",
                        icon = Icons.Default.Height,
                        color = StatusNormal,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniCard(
                        title = AppStrings.attendanceTitle(language),
                        value = "${latestRecord?.attendanceDays ?: 24} / 25",
                        sub = "96% Present",
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFFD97706),
                        modifier = Modifier.weight(1f)
                    )
                }

                // 4. Smooth Growth Trajectory Chart (Past 6 Months)
                GrowthChart(
                    records = healthRecords,
                    language = language
                )

                // 5. Upcoming Ration Distribution Schedule Card
                RationScheduleCard(
                    schedule = rationSchedules.firstOrNull() ?: RationSchedule(),
                    language = language
                )

                // 6. Immunization Schedule & Alerts Card
                VaccinationCard(
                    vaccinations = vaccinations,
                    language = language
                )

                // 7. Centre Announcements & Events Feed
                AnnouncementsFeedCard(
                    announcements = announcements,
                    language = language
                )

                // 8. AI Growth & Nutrition Guidance Card (Gemini)
                AiGuidanceCard(
                    adviceState = aiAdviceState,
                    language = language,
                    displayLang = aiDisplayLang,
                    onToggleLang = { aiDisplayLang = it },
                    onRefresh = { viewModel.loadAiAdvice(profile.childId, forceRefresh = true) }
                )
            }
        }
    }
}

@Composable
private fun NextActivityHeroCard(
    profile: ChildProfile,
    rationSchedule: RationSchedule?,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PrimaryIndigo.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .testTag("next_activity_hero_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryIndigoContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigo),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationImportant,
                            contentDescription = "Alert",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.MARATHI)
                            "पुढील महत्त्वाचा उपक्रम (Next Activity)"
                        else if (language == AppLanguage.HINDI)
                            "अगली महत्वपूर्ण गतिविधि (Next Activity)"
                        else
                            "Next Important Activity",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = PrimaryIndigoOnContainer
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryIndigo)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "२४ ऑक्टोबर (24-Oct)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "📦 पोषण आहार वाटप दिवस (THR Ration Pickup): ${rationSchedule?.distributionDate ?: "24-Oct-2026"}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Slate900
            )

            Text(
                text = "📍 स्थळ: ${rationSchedule?.location ?: "AW-Center-042"} • वेळ: ${rationSchedule?.pickupTiming ?: "08:30 AM – 01:00 PM"}",
                fontSize = 12.sp,
                color = Slate700,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            if (profile.nextVaccine.isNotEmpty() && !profile.nextVaccine.contains("complete", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vaccines,
                        contentDescription = "Vaccine",
                        tint = StatusMAM,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "लसीकरण सतर्कता: ${profile.nextVaccine}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StatusMAM
                    )
                }
            }
        }
    }
}

@Composable
private fun ChildIdentitySummaryCard(
    profile: ChildProfile,
    latestRecord: HealthRecord?,
    status: NutritionStatus,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(16.dp))
            .testTag("child_summary_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Child Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (profile.gender == "F") Color(0xFFFCE4EC) else PrimaryIndigoContainer)
                        .border(
                            1.5.dp,
                            if (profile.gender == "F") Color(0xFFF472B6) else PrimaryIndigo,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profile.name.take(1),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (profile.gender == "F") Color(0xFFBE185D) else PrimaryIndigo
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = profile.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Slate900
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Slate100)
                                .border(1.dp, Slate200, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = profile.childId,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = PrimaryIndigo
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    val ageDisplay = when (language) {
                        AppLanguage.MARATHI -> profile.formattedAgeMr
                        AppLanguage.HINDI -> profile.formattedAgeHi
                        AppLanguage.ENGLISH -> profile.formattedAge
                    }

                    Text(
                        text = "$ageDisplay • जन्म: ${profile.dob}",
                        fontSize = 12.sp,
                        color = Slate600
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "पालक: ${profile.guardian} • 📞 ${profile.parentContact}",
                        fontSize = 11.sp,
                        color = Slate700
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Slate200
            )

            // Status Tag Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.MARATHI)
                        "पोषण व वाढ स्थिती (WHO मानकानुसार):"
                    else if (language == AppLanguage.HINDI)
                        "पोषण स्थिति (WHO मानक):"
                    else
                        "Nutrition Growth Status (WHO):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Slate700
                )

                val statusLabel = when (language) {
                    AppLanguage.MARATHI -> status.marathiLabel
                    AppLanguage.HINDI -> status.hindiLabel
                    AppLanguage.ENGLISH -> status.englishLabel
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(status.containerColor)
                        .border(1.dp, status.borderColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(status.badgeColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusLabel,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = status.textColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, Slate200, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(15.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate900)
            Text(text = title, fontSize = 11.sp, color = Slate600, fontWeight = FontWeight.Medium)
            Text(text = sub, fontSize = 9.sp, color = Slate600)
        }
    }
}

@Composable
private fun RationScheduleCard(
    schedule: RationSchedule,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(16.dp))
            .testTag("parent_ration_schedule_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = "Ration",
                        tint = PrimaryIndigo,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.rationScheduleTitle(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Slate900
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFFEF3C7))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = when (language) {
                            AppLanguage.MARATHI -> schedule.statusMr
                            AppLanguage.HINDI -> schedule.statusHi
                            AppLanguage.ENGLISH -> schedule.statusEn
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "📅 दिनांक: ${schedule.distributionDate} • वेळ: ${schedule.pickupTiming}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Slate900
            )
            Text(
                text = "📍 ठिकाण: ${schedule.location}",
                fontSize = 12.sp,
                color = Slate600,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (language == AppLanguage.MARATHI)
                    "वाटप करण्यात येणारे पोषण साहित्य (Ration Items):"
                else
                    "Take Home Ration (THR) Items Checklist:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Slate700
            )

            Spacer(modifier = Modifier.height(6.dp))

            schedule.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${item.name}",
                        fontSize = 12.sp,
                        color = Slate900,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = item.quantity,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryIndigo
                    )
                }
            }
        }
    }
}

@Composable
private fun AnnouncementsFeedCard(
    announcements: List<CentreAnnouncement>,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(16.dp))
            .testTag("parent_announcements_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = "Announcements",
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.MARATHI)
                        "अंगणवाडी केंद्र सूचना व उपक्रम"
                    else if (language == AppLanguage.HINDI)
                        "आंगनवाड़ी केंद्र सूचनाएं एवं कार्यक्रम"
                    else
                        "Centre Announcements & Events",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Slate900
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            announcements.forEachIndexed { index, ann ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    val title = when (language) {
                        AppLanguage.MARATHI -> ann.titleMr
                        AppLanguage.HINDI -> ann.titleHi
                        AppLanguage.ENGLISH -> ann.titleEn
                    }
                    val desc = when (language) {
                        AppLanguage.MARATHI -> ann.descriptionMr
                        AppLanguage.HINDI -> ann.descriptionHi
                        AppLanguage.ENGLISH -> ann.descriptionEn
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Slate900,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = ann.date,
                            fontSize = 11.sp,
                            color = Slate600
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = desc,
                        fontSize = 12.sp,
                        color = Slate700,
                        lineHeight = 16.sp
                    )
                }

                if (index < announcements.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Slate100)
                }
            }
        }
    }
}

@Composable
private fun AiGuidanceCard(
    adviceState: AiAdviceState,
    language: AppLanguage,
    displayLang: AppLanguage,
    onToggleLang: (AppLanguage) -> Unit,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Slate200, RoundedCornerShape(16.dp))
            .testTag("parent_ai_guidance_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(PrimaryIndigoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = PrimaryIndigo,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.aiAdviceTitle(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Slate900
                    )
                }

                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = PrimaryIndigo
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (adviceState) {
                is AiAdviceState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryIndigo, modifier = Modifier.size(28.dp))
                    }
                }
                is AiAdviceState.Success -> {
                    val summaryText = if (displayLang == AppLanguage.ENGLISH) {
                        adviceState.summary.englishSummary
                    } else {
                        adviceState.summary.hindiSummary
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Slate50)
                            .border(1.dp, Slate200, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = summaryText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = Slate900
                        )
                    }
                }
                is AiAdviceState.Error -> {
                    Text(
                        text = adviceState.message,
                        fontSize = 12.sp,
                        color = StatusSAM
                    )
                }
                AiAdviceState.Idle -> {
                    Text(
                        text = "मार्गदर्शन पाहण्यासाठी रीफ्रेश करा.",
                        fontSize = 12.sp,
                        color = Slate600
                    )
                }
            }
        }
    }
}
