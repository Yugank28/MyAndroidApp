package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NutritionStatus
import com.example.ui.theme.AppLanguage

@Composable
fun NutritionBadge(
    status: NutritionStatus,
    language: AppLanguage,
    modifier: Modifier = Modifier,
    isLarge: Boolean = false
) {
    val label = status.label(language)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .testTag("nutrition_badge_${status.name.lowercase()}")
            .clip(RoundedCornerShape(if (isLarge) 12.dp else 8.dp))
            .background(status.containerColor)
            .border(1.dp, status.badgeColor.copy(alpha = 0.5f), RoundedCornerShape(if (isLarge) 12.dp else 8.dp))
            .padding(
                horizontal = if (isLarge) 14.dp else 8.dp,
                vertical = if (isLarge) 8.dp else 4.dp
            )
    ) {
        // Status Indicator Dot
        Box(
            modifier = Modifier
                .size(if (isLarge) 12.dp else 8.dp)
                .clip(CircleShape)
                .background(status.badgeColor)
        )
        Spacer(modifier = Modifier.width(if (isLarge) 8.dp else 6.dp))
        Text(
            text = label,
            color = status.textColor,
            fontSize = if (isLarge) 15.sp else 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
