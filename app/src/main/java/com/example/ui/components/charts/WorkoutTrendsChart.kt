package com.example.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkoutTrendStat
import com.example.ui.theme.CalorieBurnColor
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonLime

enum class WorkoutChartMetric {
    DURATION, VOLUME, CALORIES
}

@Composable
fun WorkoutTrendsChart(
    workoutStats: List<WorkoutTrendStat>,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(WorkoutChartMetric.VOLUME) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = CyanNeon,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Workout Intensity & Trends",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metric Selector Chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = selectedMetric == WorkoutChartMetric.VOLUME,
                    onClick = {
                        selectedMetric = WorkoutChartMetric.VOLUME
                        selectedIndex = null
                    },
                    label = { Text("Volume (kg)", style = MaterialTheme.typography.labelSmall) }
                )
                FilterChip(
                    selected = selectedMetric == WorkoutChartMetric.DURATION,
                    onClick = {
                        selectedMetric = WorkoutChartMetric.DURATION
                        selectedIndex = null
                    },
                    label = { Text("Duration", style = MaterialTheme.typography.labelSmall) }
                )
                FilterChip(
                    selected = selectedMetric == WorkoutChartMetric.CALORIES,
                    onClick = {
                        selectedMetric = WorkoutChartMetric.CALORIES
                        selectedIndex = null
                    },
                    label = { Text("Calories", style = MaterialTheme.typography.labelSmall) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (workoutStats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No completed workouts yet to chart.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val activeItem = selectedIndex?.let { workoutStats.getOrNull(it) } ?: workoutStats.lastOrNull()
                if (activeItem != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = activeItem.title,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = activeItem.dateLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text(
                                    text = "🏋️ ${activeItem.totalVolumeKg.toInt()} kg Vol",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = CyanNeon
                                )
                                Text(
                                    text = "⏱️ ${activeItem.durationMinutes} min",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = FlameOrange
                                )
                                Text(
                                    text = "🔥 ${activeItem.caloriesBurned} kcal",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = CalorieBurnColor
                                )
                            }
                        }
                    }
                }

                val values = workoutStats.map { stat ->
                    when (selectedMetric) {
                        WorkoutChartMetric.VOLUME -> stat.totalVolumeKg
                        WorkoutChartMetric.DURATION -> stat.durationMinutes.toFloat()
                        WorkoutChartMetric.CALORIES -> stat.caloriesBurned.toFloat()
                    }
                }
                val maxVal = (values.maxOrNull() ?: 100f).coerceAtLeast(10f) * 1.15f

                val primaryColor = when (selectedMetric) {
                    WorkoutChartMetric.VOLUME -> CyanNeon
                    WorkoutChartMetric.DURATION -> FlameOrange
                    WorkoutChartMetric.CALORIES -> CalorieBurnColor
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .pointerInput(workoutStats) {
                            detectTapGestures { offset ->
                                val count = workoutStats.size
                                if (count > 0) {
                                    val slotWidth = size.width / count
                                    val idx = (offset.x / slotWidth).toInt().coerceIn(0, count - 1)
                                    selectedIndex = idx
                                }
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        val w = size.width
                        val h = size.height - 24.dp.toPx()
                        val count = workoutStats.size
                        val slotWidth = w / count
                        val barWidth = (slotWidth * 0.55f).coerceAtMost(28.dp.toPx())

                        workoutStats.forEachIndexed { index, stat ->
                            val isSelected = index == selectedIndex || (selectedIndex == null && index == count - 1)
                            val rawValue = when (selectedMetric) {
                                WorkoutChartMetric.VOLUME -> stat.totalVolumeKg
                                WorkoutChartMetric.DURATION -> stat.durationMinutes.toFloat()
                                WorkoutChartMetric.CALORIES -> stat.caloriesBurned.toFloat()
                            }
                            val barHeight = ((rawValue / maxVal) * h).coerceAtLeast(4.dp.toPx())
                            val x = (index * slotWidth) + (slotWidth - barWidth) / 2f
                            val y = h - barHeight

                            // Background track
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.05f),
                                topLeft = Offset(x, 0f),
                                size = Size(barWidth, h),
                                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                            )

                            // Foreground bar
                            drawRoundRect(
                                brush = Brush.verticalGradient(
                                    colors = if (isSelected) {
                                        listOf(primaryColor, primaryColor.copy(alpha = 0.6f))
                                    } else {
                                        listOf(primaryColor.copy(alpha = 0.7f), primaryColor.copy(alpha = 0.3f))
                                    },
                                    startY = y,
                                    endY = h
                                ),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                            )
                        }
                    }
                }

                // X labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    workoutStats.forEach { stat ->
                        Text(
                            text = stat.dateLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
