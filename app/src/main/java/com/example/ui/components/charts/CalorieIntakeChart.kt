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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyCalorieStat
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FatsColor
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.ProteinColor

@Composable
fun CalorieIntakeChart(
    dailyStats: List<DailyCalorieStat>,
    modifier: Modifier = Modifier
) {
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
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = FlameOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Daily Calorie Intake",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (dailyStats.isNotEmpty()) {
                    val avgCal = dailyStats.map { it.calories }.average().toInt()
                    Text(
                        text = "Avg: $avgCal kcal",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = FlameOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (dailyStats.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No calorie intake data yet.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val targetCalories = dailyStats.lastOrNull()?.targetCalories ?: 2300
                val activeItem = selectedIndex?.let { dailyStats.getOrNull(it) } ?: dailyStats.lastOrNull()

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
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${activeItem.dateLabel} (${activeItem.dateKey})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${activeItem.calories} kcal / ${activeItem.targetCalories} goal",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (activeItem.calories <= activeItem.targetCalories + 150) EmeraldGreen else FlameOrange
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                Text(
                                    text = "P: ${activeItem.proteinGrams.toInt()}g",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = ProteinColor
                                )
                                Text(
                                    text = "C: ${activeItem.carbsGrams.toInt()}g",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = CarbsColor
                                )
                                Text(
                                    text = "F: ${activeItem.fatsGrams.toInt()}g",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = FatsColor
                                )
                            }
                        }
                    }
                }

                val maxCalorie = maxOf(
                    dailyStats.maxOfOrNull { it.calories } ?: 2500,
                    targetCalories
                ) * 1.15f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .pointerInput(dailyStats) {
                            detectTapGestures { offset ->
                                val count = dailyStats.size
                                if (count > 0) {
                                    val barSlotWidth = size.width / count
                                    val idx = (offset.x / barSlotWidth).toInt().coerceIn(0, count - 1)
                                    selectedIndex = idx
                                }
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                        val w = size.width
                        val h = size.height - 24.dp.toPx()
                        val count = dailyStats.size
                        val slotWidth = w / count
                        val barWidth = (slotWidth * 0.55f).coerceAtMost(28.dp.toPx())

                        // Draw target threshold line (dashed)
                        val targetY = h - (targetCalories / maxCalorie) * h
                        drawLine(
                            color = Color.White.copy(alpha = 0.35f),
                            start = Offset(0f, targetY),
                            end = Offset(w, targetY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f))
                        )

                        // Draw bars
                        dailyStats.forEachIndexed { index, stat ->
                            val isSelected = index == selectedIndex || (selectedIndex == null && index == count - 1)
                            val barHeight = ((stat.calories / maxCalorie) * h).coerceAtLeast(4.dp.toPx())
                            val x = (index * slotWidth) + (slotWidth - barWidth) / 2f
                            val y = h - barHeight

                            val barColor = when {
                                isSelected -> FlameOrange
                                stat.calories <= targetCalories -> EmeraldGreen.copy(alpha = 0.8f)
                                else -> FlameOrange.copy(alpha = 0.7f)
                            }

                            // Background pill track
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.05f),
                                topLeft = Offset(x, 0f),
                                size = Size(barWidth, h),
                                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                            )

                            // Actual bar fill
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                            )
                        }
                    }
                }

                // X Axis date labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dailyStats.forEach { stat ->
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
