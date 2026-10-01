package com.example.ui.screens.nutrition

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.MealEntry
import com.example.ui.FitnessViewModel
import com.example.ui.components.CalorieProgressRing
import com.example.ui.components.MacroSummaryRow
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FatsColor
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.ProteinColor
import com.example.ui.theme.WaterColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionScreen(
    viewModel: FitnessViewModel
) {
    val nutritionState by viewModel.dayNutritionState.collectAsStateWithLifecycle()
    val dailyTarget by viewModel.dailyTarget.collectAsStateWithLifecycle()
    val todayBurned by viewModel.todayCaloriesBurned.collectAsStateWithLifecycle()

    var showAddMealDialog by remember { mutableStateOf(false) }
    var showEditGoalsDialog by remember { mutableStateOf(false) }
    var selectedMealTypeForDialog by remember { mutableStateOf("Breakfast") }

    if (showAddMealDialog) {
        AddMealDialog(
            initialMealType = selectedMealTypeForDialog,
            onDismiss = { showAddMealDialog = false },
            onSaveMeal = { type, name, cal, p, c, f ->
                viewModel.logMeal(type, name, cal, p, c, f)
            }
        )
    }

    if (showEditGoalsDialog) {
        EditDailyGoalsDialog(
            currentTarget = dailyTarget,
            onDismiss = { showEditGoalsDialog = false },
            onSaveTarget = { newTarget ->
                viewModel.updateDailyTarget(newTarget)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Calorie & Nutrition",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showEditGoalsDialog = true },
                        modifier = Modifier.testTag("top_bar_edit_goals_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Edit Goals",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    selectedMealTypeForDialog = "Breakfast"
                    showAddMealDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Log Food") },
                containerColor = FlameOrange,
                contentColor = Color.White,
                modifier = Modifier.testTag("log_food_fab")
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
            // Visual Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nutrition_banner_1790792174576),
                        contentDescription = "Nutrition Inspiration",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xDD0B0F19))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "FUEL YOUR RECOVERY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = CyanNeon
                        )
                        Text(
                            text = "Macro & Micronutrient Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }

            // Date Navigation Row
            item {
                DateNavRow(
                    currentDateKey = nutritionState.dateKey,
                    onDateChanged = { newKey -> viewModel.setSelectedNutritionDate(newKey) }
                )
            }

            // Calorie Ring & Targets Card
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clickable { showEditGoalsDialog = true }
                                    .testTag("edit_goals_chip")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = FlameOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Edit Target",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = FlameOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        CalorieProgressRing(
                            consumedCalories = nutritionState.totalCalories,
                            targetCalories = dailyTarget.calorieGoal,
                            burnedCalories = todayBurned
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${nutritionState.totalCalories}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Consumed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${dailyTarget.calorieGoal}",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Goal Target",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$todayBurned",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = FlameOrange
                                )
                                Text(
                                    text = "Workout Burn",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Macro Calorie Ratio Breakdown
                        val totalMacroCalories = (nutritionState.totalProtein * 4f + nutritionState.totalCarbs * 4f + nutritionState.totalFats * 9f).coerceAtLeast(1f)
                        val pPercent = ((nutritionState.totalProtein * 4f / totalMacroCalories) * 100).toInt()
                        val cPercent = ((nutritionState.totalCarbs * 4f / totalMacroCalories) * 100).toInt()
                        val fPercent = (100 - pPercent - cPercent).coerceAtLeast(0)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Macro Ratio",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Protein $pPercent% • Carbs $cPercent% • Fats $fPercent%",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Macros Breakdown
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Macronutrient Targets",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Grams Remaining",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                MacroSummaryRow(
                    currentProtein = nutritionState.totalProtein,
                    targetProtein = dailyTarget.proteinGoalGrams,
                    currentCarbs = nutritionState.totalCarbs,
                    targetCarbs = dailyTarget.carbsGoalGrams,
                    currentFats = nutritionState.totalFats,
                    targetFats = dailyTarget.fatsGoalGrams
                )
            }

            // Water Tracker Card
            item {
                WaterTrackerCard(
                    currentWaterMl = nutritionState.totalWaterMl,
                    targetWaterMl = dailyTarget.waterGoalMl,
                    onAddWater = { ml -> viewModel.logWater(ml) }
                )
            }

            // Grouped Meals
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Meals & Intake Logs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${nutritionState.meals.size} Items Logged",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            val mealGroups = listOf("Breakfast", "Lunch", "Dinner", "Snack", "Post-Workout")
            items(mealGroups) { mealType ->
                val groupEntries = nutritionState.meals.filter { it.mealType.equals(mealType, ignoreCase = true) }
                MealGroupCard(
                    mealType = mealType,
                    entries = groupEntries,
                    onAddClick = {
                        selectedMealTypeForDialog = mealType
                        showAddMealDialog = true
                    },
                    onDeleteEntry = { id -> viewModel.deleteMeal(id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun DateNavRow(
    currentDateKey: String,
    onDateChanged: (String) -> Unit
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displaySdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val parsedDate = try { sdf.parse(currentDateKey) ?: Date() } catch (_: Exception) { Date() }

    val isToday = currentDateKey == MealEntry.getCurrentDateKey()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    cal.time = parsedDate
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                    onDateChanged(sdf.format(cal.time))
                },
                modifier = Modifier.testTag("prev_date_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Day")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = CyanNeon,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = if (isToday) "Today (${displaySdf.format(parsedDate)})" else displaySdf.format(parsedDate),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = {
                    val cal = Calendar.getInstance()
                    cal.time = parsedDate
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                    onDateChanged(sdf.format(cal.time))
                },
                modifier = Modifier.testTag("next_date_button")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Day")
            }
        }
    }
}

@Composable
fun WaterTrackerCard(
    currentWaterMl: Int,
    targetWaterMl: Int,
    onAddWater: (Int) -> Unit
) {
    val progress = if (targetWaterMl > 0) (currentWaterMl.toFloat() / targetWaterMl).coerceIn(0f, 1f) else 0f

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        imageVector = Icons.Default.LocalDrink,
                        contentDescription = "Water",
                        tint = WaterColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Water Intake",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$currentWaterMl / $targetWaterMl ml",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = WaterColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = WaterColor,
                trackColor = WaterColor.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onAddWater(250) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_water_250_button")
                ) {
                    Text("+250 ml (Glass)")
                }
                OutlinedButton(
                    onClick = { onAddWater(500) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_water_500_button")
                ) {
                    Text("+500 ml (Bottle)")
                }
            }
        }
    }
}

@Composable
fun MealGroupCard(
    mealType: String,
    entries: List<MealEntry>,
    onAddClick: () -> Unit,
    onDeleteEntry: (Long) -> Unit
) {
    val totalCalories = entries.sumOf { it.calories }
    val totalProtein = entries.sumOf { it.proteinGrams.toDouble() }.toInt()
    val totalCarbs = entries.sumOf { it.carbsGrams.toDouble() }.toInt()
    val totalFats = entries.sumOf { it.fatsGrams.toDouble() }.toInt()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = mealType,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (entries.isNotEmpty()) {
                        Text(
                            text = "${totalCalories} kcal • ${totalProtein}g P • ${totalCarbs}g C • ${totalFats}g F",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onAddClick, modifier = Modifier.testTag("add_${mealType.lowercase().replace("-", "_")}_button")) {
                    Icon(Icons.Default.Add, contentDescription = "Add $mealType", tint = FlameOrange)
                }
            }

            if (entries.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "No items logged yet",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                entries.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${item.calories} kcal • ${item.proteinGrams.toInt()}g P • ${item.carbsGrams.toInt()}g C • ${item.fatsGrams.toInt()}g F",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { onDeleteEntry(item.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Item",
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
