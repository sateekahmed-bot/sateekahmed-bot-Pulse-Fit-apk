package com.example.ui.screens.dashboard

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.FitnessViewModel
import com.example.ui.components.CalorieProgressRing
import com.example.ui.components.MacroSummaryRow
import com.example.ui.components.WeeklyStreakCard
import com.example.ui.screens.nutrition.AddMealDialog
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonLime
import com.example.ui.theme.WaterColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: FitnessViewModel,
    onNavigateToWorkouts: () -> Unit,
    onNavigateToActiveWorkout: () -> Unit,
    onNavigateToNutrition: () -> Unit,
    onNavigateToPlans: () -> Unit,
    onNavigateToProgress: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val dailyTarget by viewModel.dailyTarget.collectAsStateWithLifecycle()
    val nutritionState by viewModel.dayNutritionState.collectAsStateWithLifecycle()
    val todayBurned by viewModel.todayCaloriesBurned.collectAsStateWithLifecycle()
    val activeState by viewModel.activeWorkout.collectAsStateWithLifecycle()
    val activePlan by viewModel.activePlan.collectAsStateWithLifecycle()
    val activePlanDays by viewModel.activePlanDays.collectAsStateWithLifecycle()
    val completedCount by viewModel.completedWorkoutsCount.collectAsStateWithLifecycle()

    var showQuickMealDialog by remember { mutableStateOf(false) }

    if (showQuickMealDialog) {
        AddMealDialog(
            initialMealType = "Lunch",
            onDismiss = { showQuickMealDialog = false },
            onSaveMeal = { type, name, cal, p, c, f ->
                viewModel.logMeal(type, name, cal, p, c, f)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PulseFit",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = FlameOrange
                        )
                        Text(
                            text = "${userProfile.name} • ${userProfile.fitnessGoal}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile, modifier = Modifier.testTag("dashboard_profile_button")) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
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
            // Live In-Progress Workout Banner (if active)
            if (activeState.isActive) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToActiveWorkout() }
                            .testTag("dashboard_active_workout_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "WORKOUT IN PROGRESS",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = activeState.sessionName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }

                            val minutes = activeState.elapsedSeconds / 60
                            val seconds = activeState.elapsedSeconds % 60
                            Button(
                                onClick = onNavigateToActiveWorkout,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = FlameOrange),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(String.format("%02d:%02d", minutes, seconds), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Calorie & Target Summary Ring
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daily Calorie Budget",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "View Details",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = CyanNeon,
                                modifier = Modifier.clickable { onNavigateToNutrition() }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        CalorieProgressRing(
                            consumedCalories = nutritionState.totalCalories,
                            targetCalories = dailyTarget.calorieGoal,
                            burnedCalories = todayBurned
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        MacroSummaryRow(
                            currentProtein = nutritionState.totalProtein,
                            targetProtein = dailyTarget.proteinGoalGrams,
                            currentCarbs = nutritionState.totalCarbs,
                            targetCarbs = dailyTarget.carbsGoalGrams,
                            currentFats = nutritionState.totalFats,
                            targetFats = dailyTarget.fatsGoalGrams
                        )
                    }
                }
            }

            // Today's Scheduled Workout from Personalized Plan
            item {
                val nextDay = activePlanDays.firstOrNull()
                if (activePlan != null && nextDay != null) {
                    ElevatedCard(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = CyanNeon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "TODAY'S SCHEDULED WORKOUT",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            letterSpacing = 1.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = CyanNeon
                                    )
                                }

                                Text(
                                    text = "~${nextDay.estimatedMinutes}m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = nextDay.dayTitle,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = nextDay.exercisesSummary,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.startWorkoutFromPlanDay(nextDay)
                                        onNavigateToActiveWorkout()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("dashboard_start_today_workout_button")
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Workout", color = Color.White, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = onNavigateToPlans,
                                    modifier = Modifier.testTag("dashboard_view_plan_button")
                                ) {
                                    Text("View Plan")
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No Active Training Plan",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Generate a personalized program tailored to your goals.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = onNavigateToPlans) {
                                Text("Browse & Personalize Plans")
                            }
                        }
                    }
                }
            }

            // Water Quick Hydration Bar
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.LocalDrink, contentDescription = null, tint = WaterColor)
                            Column {
                                Text(
                                    text = "Hydration Today",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${nutritionState.totalWaterMl} / ${dailyTarget.waterGoalMl} ml",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WaterColor
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.logWater(250) },
                                modifier = Modifier.height(36.dp).testTag("quick_water_250")
                            ) {
                                Text("+250ml", style = MaterialTheme.typography.labelSmall)
                            }
                            OutlinedButton(
                                onClick = { viewModel.logWater(500) },
                                modifier = Modifier.height(36.dp).testTag("quick_water_500")
                            ) {
                                Text("+500ml", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Weekly Consistency Streak
            item {
                WeeklyStreakCard(
                    streakDays = 4,
                    completedDaysThisWeek = setOf(0, 1, 3)
                )
            }

            // Quick Actions Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showQuickMealDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_quick_meal_button")
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Meal", style = MaterialTheme.typography.labelMedium)
                    }

                    OutlinedButton(
                        onClick = onNavigateToProgress,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_progress_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Analytics", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
