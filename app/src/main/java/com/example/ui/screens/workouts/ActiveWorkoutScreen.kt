package com.example.ui.screens.workouts

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExerciseCatalog
import com.example.data.model.ExerciseSet
import com.example.data.model.ExerciseTemplate
import com.example.data.model.FullExercise
import com.example.ui.FitnessViewModel
import com.example.ui.components.RestTimerDialog
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonLime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    viewModel: FitnessViewModel,
    onFinishOrDiscard: () -> Unit
) {
    val activeState by viewModel.activeWorkout.collectAsStateWithLifecycle()
    var showAddExerciseSheet by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var finishNotes by remember { mutableStateOf("") }

    // Intercept back button to prevent accidental workout loss
    BackHandler {
        showFinishDialog = true
    }

    if (activeState.restTimerActive) {
        RestTimerDialog(
            remainingSeconds = activeState.restTimerRemainingSeconds,
            initialSeconds = activeState.restTimerInitialSeconds,
            onAddSeconds = { delta ->
                viewModel.startRestTimer((activeState.restTimerRemainingSeconds + delta).coerceAtLeast(5))
            },
            onSkip = { viewModel.cancelRestTimer() }
        )
    }

    if (showAddExerciseSheet) {
        AddExerciseBottomSheet(
            onDismiss = { showAddExerciseSheet = false },
            onSelectExercise = { name, muscle, sets, reps, weight ->
                viewModel.addExerciseToActiveWorkout(name, muscle, sets, reps, weight)
                showAddExerciseSheet = false
            }
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = {
                Text(
                    text = "Finish Workout?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    val minutes = activeState.elapsedSeconds / 60
                    val seconds = activeState.elapsedSeconds % 60
                    Text(
                        text = "Great job! Workout Duration: ${String.format("%02d:%02d", minutes, seconds)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = finishNotes,
                        onValueChange = { finishNotes = it },
                        label = { Text("Workout Notes (optional)") },
                        placeholder = { Text("Felt strong on bench, pushed reps...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.finishActiveWorkout(finishNotes)
                        showFinishDialog = false
                        onFinishOrDiscard()
                    },
                    modifier = Modifier.testTag("confirm_finish_workout_button")
                ) {
                    Text("Complete & Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Keep Training")
                }
            }
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Workout?") },
            text = { Text("This will delete the current workout session and unsaved progress.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.discardActiveWorkout()
                        showDiscardDialog = false
                        onFinishOrDiscard()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_discard_workout_button")
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = activeState.sessionName.ifBlank { "Active Workout" },
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1
                        )
                        val minutes = activeState.elapsedSeconds / 60
                        val seconds = activeState.elapsedSeconds % 60
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = String.format("%02d:%02d", minutes, seconds),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = CyanNeon
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showFinishDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { showFinishDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = Color(0xFF133800)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("finish_workout_button")
                    ) {
                        Text("Finish", fontWeight = FontWeight.Bold)
                    }

                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Rest Timer (60s)") },
                            onClick = {
                                showMenu = false
                                viewModel.startRestTimer(60)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Discard Workout", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                showDiscardDialog = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        val exercises = activeState.currentSession?.exercises.orEmpty()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Quick Rest Timer shortcut banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = CyanNeon)
                            Text(
                                text = "Rest Timer Quick Start",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = { viewModel.startRestTimer(60) },
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("60s", style = MaterialTheme.typography.labelSmall)
                            }
                            OutlinedButton(
                                onClick = { viewModel.startRestTimer(90) },
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("90s", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            if (exercises.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No exercises added yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap the button below to pick an exercise from the library.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(exercises) { fullEx ->
                    ActiveExerciseCard(
                        fullExercise = fullEx,
                        onAddSet = {
                            val lastSet = fullEx.sets.lastOrNull()
                            viewModel.addSetToExercise(
                                exerciseId = fullEx.exercise.id,
                                currentSetsCount = fullEx.sets.size,
                                lastReps = lastSet?.reps ?: 10,
                                lastWeight = lastSet?.weightKg ?: 20f
                            )
                        },
                        onToggleSetCompleted = { set ->
                            viewModel.toggleSetCompleted(set)
                        },
                        onDeleteExercise = {
                            viewModel.deleteExercise(fullEx.exercise.id)
                        }
                    )
                }
            }

            item {
                Button(
                    onClick = { showAddExerciseSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("add_exercise_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Exercise", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun ActiveExerciseCard(
    fullExercise: FullExercise,
    onAddSet: () -> Unit,
    onToggleSetCompleted: (ExerciseSet) -> Unit,
    onDeleteExercise: () -> Unit
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fullExercise.exercise.targetMuscle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 0.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = FlameOrange
                    )
                    Text(
                        text = fullExercise.exercise.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDeleteExercise) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Exercise",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Sets Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SET",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(42.dp)
                )
                Text(
                    text = "WEIGHT (KG)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "REPS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "DONE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            fullExercise.sets.forEach { set ->
                SetRowItem(
                    set = set,
                    onToggleComplete = { onToggleSetCompleted(set) }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedButton(
                onClick = onAddSet,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("add_set_button_${fullExercise.exercise.id}")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Set", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
fun SetRowItem(
    set: ExerciseSet,
    onToggleComplete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (set.isCompleted) EmeraldGreen.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${set.setNumber}",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(36.dp)
        )

        Text(
            text = "${set.weightKg} kg",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "${set.reps} reps",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onToggleComplete,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (set.isCompleted) EmeraldGreen else Color.White.copy(alpha = 0.12f))
                .testTag("set_done_${set.id}")
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Complete Set",
                tint = if (set.isCompleted) Color(0xFF133800) else Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseBottomSheet(
    onDismiss: () -> Unit,
    onSelectExercise: (name: String, muscle: String, sets: Int, reps: Int, weight: Float) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf("All") }

    val muscleFilters = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio")

    val filteredExercises = remember(searchQuery, selectedMuscle) {
        ExerciseCatalog.allExercises.filter { ex ->
            (selectedMuscle == "All" || ex.muscleGroup.equals(selectedMuscle, ignoreCase = true)) &&
                    (searchQuery.isBlank() || ex.name.contains(searchQuery, ignoreCase = true))
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Select Exercise",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search exercise (e.g. Bench, Squat)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(muscleFilters) { muscle ->
                    FilterChip(
                        selected = selectedMuscle == muscle,
                        onClick = { selectedMuscle = muscle },
                        label = { Text(muscle) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(350.dp)
            ) {
                items(filteredExercises) { ex ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectExercise(
                                    ex.name,
                                    ex.muscleGroup,
                                    ex.defaultSets,
                                    ex.defaultReps,
                                    ex.defaultWeightKg
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ex.name,
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${ex.muscleGroup} • ${ex.equipment} • ${ex.defaultSets}x${ex.defaultReps}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = ex.formCue,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = FlameOrange,
                                    maxLines = 1
                                )
                            }
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = CyanNeon)
                        }
                    }
                }
            }
        }
    }
}
