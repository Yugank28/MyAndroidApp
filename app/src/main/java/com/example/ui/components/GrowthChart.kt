package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthRecord
import com.example.ui.theme.AppLanguage
import com.example.ui.theme.PoshanPrimary
import com.example.ui.theme.PoshanSecondary

enum class ChartMetric {
    WEIGHT,
    HEIGHT
}

@Composable
fun GrowthChart(
    records: List<HealthRecord>,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(ChartMetric.WEIGHT) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("growth_chart_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Metric Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ShowChart,
                        contentDescription = "Growth Trend",
                        tint = PoshanPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when (language) {
                            AppLanguage.ENGLISH -> "Growth Trajectory"
                            AppLanguage.MARATHI -> "शारीरिक वाढ आलेख"
                            AppLanguage.HINDI -> "शारीरिक वृद्धि चार्ट"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedMetric == ChartMetric.WEIGHT,
                        onClick = { selectedMetric = ChartMetric.WEIGHT },
                        label = {
                            Text(
                                text = when (language) {
                                    AppLanguage.ENGLISH -> "Weight (kg)"
                                    AppLanguage.MARATHI -> "वजन (kg)"
                                    AppLanguage.HINDI -> "वजन (kg)"
                                },
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PoshanPrimary.copy(alpha = 0.15f),
                            selectedLabelColor = PoshanPrimary
                        )
                    )
                    FilterChip(
                        selected = selectedMetric == ChartMetric.HEIGHT,
                        onClick = { selectedMetric = ChartMetric.HEIGHT },
                        label = {
                            Text(
                                text = when (language) {
                                    AppLanguage.ENGLISH -> "Height (cm)"
                                    AppLanguage.MARATHI -> "उंची (cm)"
                                    AppLanguage.HINDI -> "लंबाई (cm)"
                                },
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PoshanSecondary.copy(alpha = 0.15f),
                            selectedLabelColor = PoshanSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (records.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (language) {
                            AppLanguage.ENGLISH -> "No checkup records available yet"
                            AppLanguage.MARATHI -> "सध्या कोणतीही नोंद उपलब्ध नाही"
                            AppLanguage.HINDI -> "कोई पूर्व जांच रिकॉर्ड उपलब्ध नहीं है"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                GrowthCanvas(
                    records = records,
                    metric = selectedMetric,
                    language = language,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // WHO Standard Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (selectedMetric == ChartMetric.WEIGHT) PoshanPrimary else PoshanSecondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH -> "Child's Measurement"
                        AppLanguage.MARATHI -> "बालकाची प्रत्यक्ष नोंद"
                        AppLanguage.HINDI -> "बच्चे की वास्तविक माप"
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .width(16.dp)
                        .height(2.dp)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when (language) {
                        AppLanguage.ENGLISH -> "WHO Reference Standard"
                        AppLanguage.MARATHI -> "WHO मानक रेषा"
                        AppLanguage.HINDI -> "WHO मानक रेखा"
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun GrowthCanvas(
    records: List<HealthRecord>,
    metric: ChartMetric,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val lineColor = if (metric == ChartMetric.WEIGHT) PoshanPrimary else PoshanSecondary
    val gradientColor = if (metric == ChartMetric.WEIGHT) PoshanPrimary.copy(alpha = 0.15f) else PoshanSecondary.copy(alpha = 0.15f)

    val values = records.map { if (metric == ChartMetric.WEIGHT) it.weightKg else it.heightCm }
    val minVal = (values.minOrNull() ?: 5f) * 0.85f
    val maxVal = (values.maxOrNull() ?: 20f) * 1.15f

    val animProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 800),
        label = "chartAnim"
    )

    Canvas(modifier = modifier.padding(horizontal = 8.dp, vertical = 12.dp)) {
        val width = size.width
        val height = size.height
        val paddingLeft = 40f
        val paddingBottom = 40f
        val chartWidth = width - paddingLeft - 20f
        val chartHeight = height - paddingBottom - 20f

        if (records.isEmpty()) return@Canvas

        // Draw horizontal grid lines
        val gridSteps = 4
        val range = (maxVal - minVal).coerceAtLeast(1f)
        for (i in 0..gridSteps) {
            val y = 20f + chartHeight * (1f - i.toFloat() / gridSteps)
            val gridVal = minVal + range * (i.toFloat() / gridSteps)

            drawLine(
                color = Color.LightGray.copy(alpha = 0.5f),
                start = Offset(paddingLeft, y),
                end = Offset(width - 10f, y),
                strokeWidth = 1f
            )

            // Y-axis label
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f", gridVal),
                10f,
                y + 8f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.LEFT
                }
            )
        }

        // Draw WHO Reference Target Line (Dashed)
        val whoMedian = if (metric == ChartMetric.WEIGHT) 11.5f else 84.0f
        if (whoMedian in minVal..maxVal) {
            val whoY = 20f + chartHeight * (1f - (whoMedian - minVal) / range)
            drawLine(
                color = Color.Gray.copy(alpha = 0.6f),
                start = Offset(paddingLeft, whoY),
                end = Offset(width - 10f, whoY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
            )
        }

        // Compute Points
        val points = records.mapIndexed { index, record ->
            val value = if (metric == ChartMetric.WEIGHT) record.weightKg else record.heightCm
            val x = if (records.size > 1) {
                paddingLeft + chartWidth * (index.toFloat() / (records.size - 1))
            } else {
                paddingLeft + chartWidth / 2f
            }
            val normalizedY = ((value - minVal) / range).coerceIn(0f, 1f)
            val targetY = 20f + chartHeight * (1f - normalizedY)
            val animatedY = 20f + chartHeight - (chartHeight - targetY + 20f) * animProgress
            Offset(x, animatedY)
        }

        // Area Path
        val areaPath = Path().apply {
            moveTo(points.first().x, 20f + chartHeight)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, 20f + chartHeight)
            close()
        }
        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(gradientColor, Color.Transparent),
                startY = 20f,
                endY = 20f + chartHeight
            )
        )

        // Line Path
        val strokePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }
        drawPath(
            path = strokePath,
            color = lineColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        // Draw Data Points and value text
        points.forEachIndexed { index, point ->
            val value = if (metric == ChartMetric.WEIGHT) records[index].weightKg else records[index].heightCm
            val isLatest = index == records.size - 1

            // Outer ring
            drawCircle(
                color = if (isLatest) lineColor else Color.White,
                radius = if (isLatest) 9f else 7f,
                center = point
            )
            drawCircle(
                color = lineColor,
                radius = if (isLatest) 6f else 4f,
                center = point
            )

            // Value text above dot
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f", value),
                point.x,
                point.y - 14f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 28f
                    isFakeBoldText = isLatest
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )

            // X-axis date label
            val dateLabel = try {
                val parts = records[index].date.split("-")
                if (parts.size >= 2) "${parts[1]}/${parts[0].takeLast(2)}" else records[index].date
            } catch (_: Exception) {
                records[index].date
            }

            drawContext.canvas.nativeCanvas.drawText(
                dateLabel,
                point.x,
                height - 6f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}
