package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaccinationRecord
import com.example.data.model.VaccineStatus
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.PoshanSecondary
import com.example.ui.theme.StatusMAM
import com.example.ui.theme.StatusNormal

@Composable
fun VaccinationCard(
    vaccinations: List<VaccinationRecord>,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("vaccination_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vaccines,
                        contentDescription = "Vaccines",
                        tint = PoshanSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.ENGLISH -> "Vaccination Schedule"
                            AppLanguage.MARATHI -> "लसीकरण वेळापत्रक (Vaccines)"
                            AppLanguage.HINDI -> "टीकाकरण अनुसूची (Vaccines)"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                val completedCount = vaccinations.count { it.status == VaccineStatus.COMPLETED }
                Text(
                    text = "$completedCount / ${vaccinations.size}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PoshanSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (vaccinations.isEmpty()) {
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH -> "No vaccine records available"
                        AppLanguage.MARATHI -> "कोणतेही लस तपशील उपलब्ध नाहीत"
                        AppLanguage.HINDI -> "कोई टीका विवरण उपलब्ध नहीं"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            } else {
                vaccinations.forEachIndexed { index, vax ->
                    VaccineItemRow(vax = vax, language = language)
                    if (index < vaccinations.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VaccineItemRow(
    vax: VaccinationRecord,
    language: AppLanguage
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            val (icon, tint) = when (vax.status) {
                VaccineStatus.COMPLETED -> Icons.Default.CheckCircle to StatusNormal
                VaccineStatus.DUE -> Icons.Default.Warning to StatusMAM
                VaccineStatus.UPCOMING -> Icons.Default.Schedule to Color.Gray
            }

            Icon(
                imageVector = icon,
                contentDescription = vax.status.name,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = vax.vaccineName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${vax.dose} • Due: ${vax.dueDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Status Tag
        val (bg, txt, label) = when (vax.status) {
            VaccineStatus.COMPLETED -> Triple(
                StatusNormal.copy(alpha = 0.12f),
                StatusNormal,
                when (language) {
                    AppLanguage.ENGLISH -> "Administered"
                    AppLanguage.MARATHI -> "दिली गेली"
                    AppLanguage.HINDI -> "लग चुका"
                }
            )
            VaccineStatus.DUE -> Triple(
                StatusMAM.copy(alpha = 0.15f),
                StatusMAM,
                when (language) {
                    AppLanguage.ENGLISH -> "Due Now"
                    AppLanguage.MARATHI -> "देय / लस द्या"
                    AppLanguage.HINDI -> "अतिदेय / लगवाएं"
                }
            )
            VaccineStatus.UPCOMING -> Triple(
                Color.LightGray.copy(alpha = 0.25f),
                Color.DarkGray,
                when (language) {
                    AppLanguage.ENGLISH -> "Upcoming"
                    AppLanguage.MARATHI -> "आगामी"
                    AppLanguage.HINDI -> "आगामी"
                }
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(bg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = txt
            )
        }
    }
}
