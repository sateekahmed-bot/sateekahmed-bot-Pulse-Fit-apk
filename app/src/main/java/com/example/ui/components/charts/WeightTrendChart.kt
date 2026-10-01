package com.example.ui.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BodyMeasurement
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonLime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeightTrendChart(
    measurements: List<BodyMeasurement>,
    modifier: Modifier = Modifier
) {
    var showBodyFat by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header with metric toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (showBodyFat) "Body Fat % Trend" else "Body Weight Trend",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val first = measurements.firstOrNull()
                    val latest = measurements.lastOrNull()
                    if (first != null && latest != null && measurements.size > 1) {
                        val delta = if (showBodyFat) {
                            (latest.bodyFatPercentage ?: 0f) - (first.bodyFatPercentage ?: 0f)
                        } else {
                            latest.weightKg - first.weightKg
                        }
                        val unit = if (showBodyFat) "%" else "kg"
                        val isLoss = delta <= 0f
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isLoss) Icons.Default.TrendingDown else Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = if (isLoss) EmeraldGreen else FlameOrange,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${String.format("%.1f", delta)} $unit total change",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = if (isLoss) EmeraldGreen else FlameOrange
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = !showBodyFat,
                        onClick = {
                            showBodyFat = false
                            selectedIndex = null
                        },
                        label = { Text("Weight", style = MaterialTheme.typography.labelSmall) }
                    )
                    FilterChip(
                        selected = showBodyFat,
                        onClick = {
                            showBodyFat = true
                            selectedIndex = null
                        },
                        label = { Text("Body Fat %", style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (measurements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No measurements logged yet. Add your first check-in below!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val dataPoints = remember(measurements, showBodyFat) {
                    measurements.mapNotNull { m ->
                        val value = if (showBodyFat) m.bodyFatPercentage else m.weightKg
                        if (value != null && value > 0f) Pair(m, value) else null
                    }
                }

                if (dataPoints.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No body fat % recorded in logs.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val values = dataPoints.map { it.second }
                    val minVal = (values.minOrNull() ?: 50f) * 0.98f
                    val maxVal = (values.maxOrNull() ?: 100f) * 1.02f
                    val range = (maxVal - minVal).coerceAtLeast(1f)

                    val strokeColor = if (showBodyFat) CyanNeon else FlameOrange

                    // Selected item tooltip
                    val activeSelection = selectedIndex?.let { dataPoints.getOrNull(it) } ?: dataPoints.lastOrNull()
                    if (activeSelection != null) {
                        val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sdf.format(Date(activeSelection.first.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (showBodyFat) "${String.format("%.1f", activeSelection.second)}% Body Fat" else "${String.format("%.1f", activeSelection.second)} kg",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = strokeColor
                                )
                                if (activeSelection.first.waistCm != null) {
                                    Text(
                                        text = "Waist: ${activeSelection.first.waistCm}cm",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .pointerInput(dataPoints) {
                                detectTapGestures { offset ->
                                    val count = dataPoints.size
                                    if (count > 0) {
                                        val stepX = size.width / (if (count == 1) 1 else count - 1).toFloat()
                                        val clickedIdx = (offset.x / stepX).toInt().coerceIn(0, count - 1)
                                        selectedIndex = clickedIdx
                                    }
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxWidth().height(170.dp)) {
                            val w = size.width
                            val h = size.height - 30.dp.toPx()
                            val count = dataPoints.size
                            val stepX = if (count > 1) w / (count - 1) else w

                            // Draw subtle horizontal grid lines
                            val gridLines = 3
                            for (i in 0..gridLines) {
                                val y = (h / gridLines) * i
                                drawLine(
                                    color = Color.White.copy(alpha = 0.06f),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            // Compute point coordinates
                            val points = dataPoints.mapIndexed { index, pair ->
                                val x = if (count > 1) index * stepX else w / 2f
                                val normalizedY = 1f - ((pair.second - minVal) / range)
                                val y = normalizedY * h
                                Offset(x, y)
                            }

                            // Draw gradient fill area under the path
                            if (points.size > 1) {
                                val fillPath = Path().apply {
                                    moveTo(points.first().x, points.first().y)
                                    for (i in 1 until points.size) {
                                        val prev = points[i - 1]
                                        val curr = points[i]
                                        val midX = (prev.x + curr.x) / 2f
                                        cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                                    }
                                    lineTo(points.last().x, h)
                                    lineTo(points.first().x, h)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(strokeColor.copy(alpha = 0.35f), Color.Transparent),
                                        startY = 0f,
                                        endY = h
                                    )
                                )

                                // Draw smooth spline line
                                val linePath = Path().apply {
                                    moveTo(points.first().x, points.first().y)
                                    for (i in 1 until points.size) {
                                        val prev = points[i - 1]
                                        val curr = points[i]
                                        val midX = (prev.x + curr.x) / 2f
                                        cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                                    }
                                }
                                drawPath(
                                    path = linePath,
                                    color = strokeColor,
                                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            // Draw points
                            points.forEachIndexed { idx, pt ->
                                val isSelected = idx == selectedIndex || (selectedIndex == null && idx == points.size - 1)
                                val ptRadius = if (isSelected) 6.dp.toPx() else 4.dp.toPx()
                                drawCircle(
                                    color = Color(0xFF0F172A),
                                    radius = ptRadius + 2.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = if (isSelected) NeonLime else strokeColor,
                                    radius = ptRadius,
                                    center = pt
                                )
                            }
                        }
                    }

                    // Bottom labels for date
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                        Text(
                            text = sdf.format(Date(dataPoints.first().first.timestamp)),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (dataPoints.size > 2) {
                            Text(
                                text = sdf.format(Date(dataPoints[dataPoints.size / 2].first.timestamp)),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = sdf.format(Date(dataPoints.last().first.timestamp)),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
