package com.example.ui.screens.progress

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BodyMeasurement
import com.example.ui.FitnessViewModel
import com.example.ui.components.charts.CalorieIntakeChart
import com.example.ui.components.charts.PersonalBestsSection
import com.example.ui.components.charts.WeightTrendChart
import com.example.ui.components.charts.WorkoutTrendsChart
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CalorieBurnColor
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: FitnessViewModel
) {
    val measurementsAsc by viewModel.measurementsAsc.collectAsStateWithLifecycle()
    val personalBests by viewModel.personalBests.collectAsStateWithLifecycle()
    val dailyCalorieStats by viewModel.dailyCalorieStats.collectAsStateWithLifecycle()
    val workoutTrends by viewModel.workoutTrendStats.collectAsStateWithLifecycle()
    val timeRange by viewModel.selectedTimeRange.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    var showAddMeasurementDialog by remember { mutableStateOf(false) }

    if (showAddMeasurementDialog) {
        val lastWeight = measurementsAsc.lastOrNull()?.weightKg ?: userProfile.weightKg
        AddMeasurementDialog(
            initialWeight = lastWeight,
            onDismiss = { showAddMeasurementDialog = false },
            onSave = { weight, bf, waist, chest, arms, notes ->
                viewModel.logBodyMeasurement(weight, bf, waist, chest, arms, notes)
            }
        )
    }

    // Filter data based on selected time range (7D, 30D, 90D, All)
    val now = System.currentTimeMillis()
    val rangeDurationMillis = when (timeRange) {
        "7D" -> 7L * 24 * 60 * 60 * 1000
        "30D" -> 30L * 24 * 60 * 60 * 1000
        "90D" -> 90L * 24 * 60 * 60 * 1000
        else -> Long.MAX_VALUE
    }

    val filteredMeasurements = remember(measurementsAsc, timeRange) {
        measurementsAsc.filter { now - it.timestamp <= rangeDurationMillis }
    }

    val filteredCalories = remember(dailyCalorieStats, timeRange) {
        dailyCalorieStats.filter { now - it.timestamp <= rangeDurationMillis }
    }

    val filteredWorkouts = remember(workoutTrends, timeRange) {
        workoutTrends.filter { now - it.timestamp <= rangeDurationMillis }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Progress & Analytics",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddMeasurementDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Log Metrics") },
                containerColor = FlameOrange,
                contentColor = Color.White,
                modifier = Modifier.testTag("log_metrics_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Time Range Filter Row (7D, 30D, 90D, All)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("7D", "30D", "90D", "All").forEach { range ->
                        FilterChip(
                            selected = timeRange == range,
                            onClick = { viewModel.setTimeRange(range) },
                            label = { Text(if (range == "All") "All Time" else "Last $range") },
                            modifier = Modifier.testTag("filter_range_$range")
                        )
                    }
                }
            }

            // Summary Metric Tiles
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Volume or Completed Workouts
                    MetricSummaryCard(
                        title = "Workouts",
                        value = "${workoutTrends.size}",
                        subtitle = "Total Completed",
                        iconColor = CyanNeon,
                        modifier = Modifier.weight(1f)
                    )

                    // Current Weight & Delta
                    val firstM = measurementsAsc.firstOrNull()
                    val latestM = measurementsAsc.lastOrNull()
                    val weightChange = if (firstM != null && latestM != null) latestM.weightKg - firstM.weightKg else 0f
                    val weightSub = if (measurementsAsc.size > 1) {
                        "${if (weightChange <= 0) "" else "+"}${String.format("%.1f", weightChange)} kg"
                    } else "Target: ${userProfile.targetWeightKg}kg"

                    MetricSummaryCard(
                        title = "Current Weight",
                        value = "${latestM?.weightKg ?: userProfile.weightKg} kg",
                        subtitle = weightSub,
                        iconColor = if (weightChange <= 0) EmeraldGreen else FlameOrange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 1. Personal Bests & Milestones Section
            item {
                PersonalBestsSection(records = personalBests)
            }

            // 2. Weight & Body Fat % Trend Chart
            item {
                WeightTrendChart(measurements = filteredMeasurements)
            }

            // 3. Workout Volume & Intensity Trends Chart
            item {
                WorkoutTrendsChart(workoutStats = filteredWorkouts)
            }

            // 4. Daily Calorie Intake History Chart
            item {
                CalorieIntakeChart(dailyStats = filteredCalories)
            }

            // 5. Recent Body Check-ins List
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Measurement History",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${measurementsAsc.size} Logs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (measurementsAsc.isEmpty()) {
                            Text(
                                text = "No measurement check-ins recorded yet.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                            measurementsAsc.reversed().take(6).forEach { m ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${m.weightKg} kg" + (m.bodyFatPercentage?.let { " • $it% BF" } ?: ""),
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        val subDetails = mutableListOf<String>()
                                        m.waistCm?.let { subDetails.add("Waist: ${it}cm") }
                                        m.chestCm?.let { subDetails.add("Chest: ${it}cm") }
                                        if (m.notes.isNotBlank()) subDetails.add(m.notes)
                                        Text(
                                            text = "${sdf.format(Date(m.timestamp))}" + if (subDetails.isNotEmpty()) " (${subDetails.joinToString(", ")})" else "",
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(onClick = { viewModel.deleteMeasurement(m.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun MetricSummaryCard(
    title: String,
    value: String,
    subtitle: String,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = iconColor
            )
        }
    }
}
