package com.example.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.UserProfile
import com.example.ui.FitnessViewModel
import com.example.ui.theme.CarbsColor
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.FatsColor
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.ProteinColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: FitnessViewModel
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val dailyTarget by viewModel.dailyTarget.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember(userProfile) { mutableStateOf(userProfile.name) }
    var ageText by remember(userProfile) { mutableStateOf(userProfile.age.toString()) }
    var weightText by remember(userProfile) { mutableStateOf(userProfile.weightKg.toString()) }
    var heightText by remember(userProfile) { mutableStateOf(userProfile.heightCm.toString()) }
    var selectedGoal by remember(userProfile) { mutableStateOf(userProfile.fitnessGoal) }
    var selectedLevel by remember(userProfile) { mutableStateOf(userProfile.fitnessLevel) }
    var selectedEquipment by remember(userProfile) { mutableStateOf(userProfile.equipment) }
    var selectedDays by remember(userProfile) { mutableIntStateOf(userProfile.daysPerWeek) }

    val goals = listOf("Build Muscle", "Fat Loss & Cut", "Strength & Power", "Athletic Endurance")
    val levels = listOf("Beginner", "Intermediate", "Advanced")
    val equipmentList = listOf("Full Gym", "Dumbbells Only", "Bodyweight & Bands")
    val daysOptions = listOf(3, 4, 5, 6)

    // Dynamic calculations based on current inputs
    val currentWeight = weightText.toFloatOrNull() ?: 75f
    val currentHeight = heightText.toFloatOrNull() ?: 178f
    val currentAge = ageText.toIntOrNull() ?: 26
    val tempProfile = UserProfile(
        name = name,
        age = currentAge,
        gender = userProfile.gender,
        weightKg = currentWeight,
        heightCm = currentHeight,
        fitnessGoal = selectedGoal,
        fitnessLevel = selectedLevel,
        equipment = selectedEquipment,
        daysPerWeek = selectedDays
    )
    val bmr = tempProfile.calculateBmr()
    val recommendedCalories = tempProfile.calculateRecommendedCalories()
    val (recP, recC, recF) = tempProfile.calculateRecommendedMacros()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile & Targets",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
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
            // TDEE & Scientific Calibration Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Calculate, contentDescription = null, tint = CyanNeon)
                            Text(
                                text = "METABOLIC TDEE CALIBRATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = CyanNeon
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Base Metabolic Rate (BMR)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$bmr kcal/day",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Calculated Target",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$recommendedCalories kcal",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = FlameOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Protein: ${recP}g",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = ProteinColor
                            )
                            Text(
                                text = "Carbs: ${recC}g",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = CarbsColor
                            )
                            Text(
                                text = "Fats: ${recF}g",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = FatsColor
                            )
                        }
                    }
                }
            }

            // Profile Inputs
            item {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Your Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = { ageText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("profile_age_input")
                            )
                            OutlinedTextField(
                                value = weightText,
                                onValueChange = { weightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Weight (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("profile_weight_input")
                            )
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = { heightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Height (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("profile_height_input")
                            )
                        }
                    }
                }
            }

            // Training Preferences
            item {
                Text(
                    text = "Fitness Goals & Training Split",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Fitness Goal",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            goals.forEach { g ->
                                FilterChip(
                                    selected = selectedGoal == g,
                                    onClick = { selectedGoal = g },
                                    label = { Text(g) }
                                )
                            }
                        }

                        Text(
                            text = "Level",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            levels.forEach { lvl ->
                                FilterChip(
                                    selected = selectedLevel == lvl,
                                    onClick = { selectedLevel = lvl },
                                    label = { Text(lvl) }
                                )
                            }
                        }

                        Text(
                            text = "Equipment",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            equipmentList.forEach { eq ->
                                FilterChip(
                                    selected = selectedEquipment == eq,
                                    onClick = { selectedEquipment = eq },
                                    label = { Text(eq) }
                                )
                            }
                        }

                        Text(
                            text = "Training Days Per Week",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            daysOptions.forEach { d ->
                                FilterChip(
                                    selected = selectedDays == d,
                                    onClick = { selectedDays = d },
                                    label = { Text("$d Days") }
                                )
                            }
                        }
                    }
                }
            }

            // Save Changes Button
            item {
                Button(
                    onClick = {
                        val profileToSave = tempProfile.copy(
                            name = name.ifBlank { "Alex" },
                            age = currentAge,
                            weightKg = currentWeight,
                            heightCm = currentHeight,
                            fitnessGoal = selectedGoal,
                            fitnessLevel = selectedLevel,
                            equipment = selectedEquipment,
                            daysPerWeek = selectedDays
                        )
                        viewModel.saveUserProfile(profileToSave)
                        scope.launch {
                            snackbarHostState.showSnackbar("Profile & daily targets updated!")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_profile_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Profile & Calibrate Targets", fontWeight = FontWeight.Bold)
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
