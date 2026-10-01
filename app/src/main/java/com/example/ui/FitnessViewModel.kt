package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.FitnessRepository
import com.example.data.model.BodyMeasurement
import com.example.data.model.DailyCalorieStat
import com.example.data.model.DailyTarget
import com.example.data.model.ExerciseCatalog
import com.example.data.model.ExerciseRecord
import com.example.data.model.ExerciseSet
import com.example.data.model.FullWorkoutSession
import com.example.data.model.MealEntry
import com.example.data.model.PlanDayWorkout
import com.example.data.model.TrainingPlan
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import com.example.data.model.WorkoutTrendStat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ActiveWorkoutUiState(
    val isActive: Boolean = false,
    val sessionId: Long = 0,
    val sessionName: String = "",
    val category: String = "Strength",
    val elapsedSeconds: Int = 0,
    val currentSession: FullWorkoutSession? = null,
    val restTimerActive: Boolean = false,
    val restTimerRemainingSeconds: Int = 0,
    val restTimerInitialSeconds: Int = 60
)

data class DayNutritionState(
    val dateKey: String = MealEntry.getCurrentDateKey(),
    val totalCalories: Int = 0,
    val totalProtein: Float = 0f,
    val totalCarbs: Float = 0f,
    val totalFats: Float = 0f,
    val totalWaterMl: Int = 0,
    val meals: List<MealEntry> = emptyList()
)

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FitnessRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = FitnessRepository(
            workoutDao = db.workoutDao(),
            nutritionDao = db.nutritionDao(),
            userDao = db.userDao(),
            trainingPlanDao = db.trainingPlanDao(),
            measurementDao = db.measurementDao()
        )
        viewModelScope.launch {
            repository.ensureInitialized()
        }
    }

    // Body Measurements
    val measurementsAsc: StateFlow<List<BodyMeasurement>> = repository.allMeasurementsAsc
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestMeasurement: StateFlow<BodyMeasurement?> = repository.latestMeasurement
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val firstMeasurement: StateFlow<BodyMeasurement?> = repository.firstMeasurement
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Personal Bests
    val personalBests: StateFlow<List<ExerciseRecord>> = repository.personalBests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Time range filter for analytics charts ("7D", "30D", "90D", "All")
    private val _selectedTimeRange = MutableStateFlow("30D")
    val selectedTimeRange: StateFlow<String> = _selectedTimeRange.asStateFlow()

    fun setTimeRange(range: String) {
        _selectedTimeRange.value = range
    }

    // Historical Calorie Intake Stats
    val dailyCalorieStats: StateFlow<List<DailyCalorieStat>> = repository.allMealEntries
        .combine(repository.getDailyTarget()) { allMeals, target ->
            val targetCal = target?.calorieGoal ?: 2300
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val labelSdf = SimpleDateFormat("MMM d", Locale.getDefault())

            val grouped = allMeals.groupBy { it.dateKey }
            grouped.map { (dateKey, meals) ->
                val totalCal = meals.sumOf { it.calories }
                val totalP = meals.sumOf { it.proteinGrams.toDouble() }.toFloat()
                val totalC = meals.sumOf { it.carbsGrams.toDouble() }.toFloat()
                val totalF = meals.sumOf { it.fatsGrams.toDouble() }.toFloat()
                val parsedDate = try { sdf.parse(dateKey) ?: Date() } catch (_: Exception) { Date() }
                DailyCalorieStat(
                    dateKey = dateKey,
                    dateLabel = labelSdf.format(parsedDate),
                    timestamp = parsedDate.time,
                    calories = totalCal,
                    targetCalories = targetCal,
                    proteinGrams = totalP,
                    carbsGrams = totalC,
                    fatsGrams = totalF
                )
            }.sortedBy { it.timestamp }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Workout Trend Stats
    val workoutTrendStats: StateFlow<List<WorkoutTrendStat>> = repository.allSessions
        .map { sessions ->
            val labelSdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sessions.filter { it.completed }.map { s ->
                val full = repository.getFullSession(s.id)
                val totalVol = full?.exercises?.sumOf { ex ->
                    ex.sets.filter { it.isCompleted }.sumOf { (it.weightKg * it.reps).toDouble() }
                }?.toFloat() ?: 0f
                val exCount = full?.exercises?.size ?: 0

                WorkoutTrendStat(
                    sessionId = s.id,
                    dateLabel = labelSdf.format(Date(s.date)),
                    timestamp = s.date,
                    durationMinutes = (s.durationSeconds / 60).coerceAtLeast(1),
                    caloriesBurned = s.caloriesBurned,
                    totalVolumeKg = totalVol,
                    exerciseCount = exCount,
                    category = s.category,
                    title = s.name
                )
            }.sortedBy { it.timestamp }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun logBodyMeasurement(
        weightKg: Float,
        bodyFatPercentage: Float? = null,
        waistCm: Float? = null,
        chestCm: Float? = null,
        armsCm: Float? = null,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.logBodyMeasurement(weightKg, bodyFatPercentage, waistCm, chestCm, armsCm, notes)
            val profile = repository.userProfile.firstOrNull()
            if (profile != null) {
                repository.saveUserProfile(profile.copy(weightKg = weightKg))
            }
        }
    }

    fun deleteMeasurement(id: Long) {
        viewModelScope.launch {
            repository.deleteMeasurement(id)
        }
    }

    // User Profile & Targets
    val userProfile: StateFlow<UserProfile> = repository.userProfile
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserProfile()
        ).let { flow ->
            // Fallback default
            flow
        } as StateFlow<UserProfile>

    val dailyTarget: StateFlow<DailyTarget> = repository.getDailyTarget()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DailyTarget()
        ).let { flow -> flow } as StateFlow<DailyTarget>

    // Date for nutrition view
    private val _selectedNutritionDate = MutableStateFlow(MealEntry.getCurrentDateKey())
    val selectedNutritionDate: StateFlow<String> = _selectedNutritionDate.asStateFlow()

    // Nutrition for selected date
    val dayNutritionState: StateFlow<DayNutritionState> = _selectedNutritionDate
        .flatMapLatest { dateKey ->
            repository.getMealsForDate(dateKey).combine(repository.getDailyTarget()) { meals, _ ->
                val totalCal = meals.sumOf { it.calories }
                val totalP = meals.sumOf { it.proteinGrams.toDouble() }.toFloat()
                val totalC = meals.sumOf { it.carbsGrams.toDouble() }.toFloat()
                val totalF = meals.sumOf { it.fatsGrams.toDouble() }.toFloat()
                val totalWater = meals.sumOf { it.waterMl }
                DayNutritionState(
                    dateKey = dateKey,
                    totalCalories = totalCal,
                    totalProtein = totalP,
                    totalCarbs = totalC,
                    totalFats = totalF,
                    totalWaterMl = totalWater,
                    meals = meals
                )
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            DayNutritionState()
        )

    // Workouts
    val allWorkoutSessions: StateFlow<List<WorkoutSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentCompletedSessions: StateFlow<List<WorkoutSession>> = repository.recentCompletedSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayCaloriesBurned: StateFlow<Int> = repository.getTodayCaloriesBurned()
        .combine(MutableStateFlow(0)) { burned, _ -> burned ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val completedWorkoutsCount: StateFlow<Int> = repository.totalCompletedWorkoutsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Training Plans
    val allPlans: StateFlow<List<TrainingPlan>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activePlan: StateFlow<TrainingPlan?> = repository.activePlan
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _activePlanDays = MutableStateFlow<List<PlanDayWorkout>>(emptyList())
    val activePlanDays: StateFlow<List<PlanDayWorkout>> = _activePlanDays.asStateFlow()

    init {
        viewModelScope.launch {
            activePlan.collect { plan ->
                if (plan != null) {
                    repository.getDaysForPlan(plan.id).collect { days ->
                        _activePlanDays.value = days
                    }
                } else {
                    _activePlanDays.value = emptyList()
                }
            }
        }
    }

    // Active Workout State
    private val _activeWorkout = MutableStateFlow(ActiveWorkoutUiState())
    val activeWorkout: StateFlow<ActiveWorkoutUiState> = _activeWorkout.asStateFlow()

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    fun startNewWorkout(name: String, category: String, planId: Long? = null) {
        viewModelScope.launch {
            val sessionId = repository.createWorkoutSession(name, category, planId)
            _activeWorkout.value = ActiveWorkoutUiState(
                isActive = true,
                sessionId = sessionId,
                sessionName = name,
                category = category,
                elapsedSeconds = 0
            )
            refreshCurrentActiveSession()
            startWorkoutTimer()
        }
    }

    fun startWorkoutFromPlanDay(day: PlanDayWorkout) {
        viewModelScope.launch {
            val sessionId = repository.createWorkoutSession(
                name = day.dayTitle,
                category = "Strength",
                planId = day.planId,
                notes = "Focus: ${day.targetMuscles}"
            )
            // Parse exercises from summary or catalog
            val exerciseNames = day.exercisesSummary.split(",").map { it.trim() }
            exerciseNames.forEachIndexed { index, rawName ->
                // Clean exercise name e.g. "Barbell Bench Press (4x8)" -> "Barbell Bench Press"
                val cleanName = rawName.substringBefore("(").trim()
                if (cleanName.isNotBlank()) {
                    repository.addExerciseToSession(
                        sessionId = sessionId,
                        name = cleanName,
                        targetMuscle = day.targetMuscles.substringBefore(",").trim(),
                        orderIndex = index,
                        defaultSetsCount = 3,
                        defaultReps = 10,
                        defaultWeightKg = 25f
                    )
                }
            }

            _activeWorkout.value = ActiveWorkoutUiState(
                isActive = true,
                sessionId = sessionId,
                sessionName = day.dayTitle,
                category = "Strength",
                elapsedSeconds = 0
            )
            refreshCurrentActiveSession()
            startWorkoutTimer()
        }
    }

    fun addExerciseToActiveWorkout(name: String, targetMuscle: String, sets: Int, reps: Int, weight: Float) {
        val currentSessionId = _activeWorkout.value.sessionId
        if (currentSessionId == 0L) return
        viewModelScope.launch {
            val existingCount = _activeWorkout.value.currentSession?.exercises?.size ?: 0
            repository.addExerciseToSession(
                sessionId = currentSessionId,
                name = name,
                targetMuscle = targetMuscle,
                orderIndex = existingCount,
                defaultSetsCount = sets,
                defaultReps = reps,
                defaultWeightKg = weight
            )
            refreshCurrentActiveSession()
        }
    }

    fun addSetToExercise(exerciseId: Long, currentSetsCount: Int, lastReps: Int, lastWeight: Float) {
        viewModelScope.launch {
            repository.addSetToExercise(
                exerciseId = exerciseId,
                setNumber = currentSetsCount + 1,
                reps = lastReps,
                weightKg = lastWeight
            )
            refreshCurrentActiveSession()
        }
    }

    fun toggleSetCompleted(set: ExerciseSet) {
        viewModelScope.launch {
            val updated = set.copy(isCompleted = !set.isCompleted)
            repository.updateSet(updated)
            refreshCurrentActiveSession()
            // If completed, trigger subtle haptic and optionally auto-start rest timer
            if (updated.isCompleted) {
                triggerHapticFeedback()
            }
        }
    }

    fun updateSetValues(setId: Long, reps: Int, weightKg: Float, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateSet(
                ExerciseSet(
                    id = setId,
                    exerciseId = 0, // Ignored by update if query only updates fields or room matches PK
                    setNumber = 0,
                    reps = reps,
                    weightKg = weightKg,
                    isCompleted = isCompleted
                )
            )
            refreshCurrentActiveSession()
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch {
            repository.deleteSet(setId)
            refreshCurrentActiveSession()
        }
    }

    fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch {
            repository.deleteExercise(exerciseId)
            refreshCurrentActiveSession()
        }
    }

    fun startRestTimer(seconds: Int = 60) {
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value.copy(
            restTimerActive = true,
            restTimerRemainingSeconds = seconds,
            restTimerInitialSeconds = seconds
        )
        restTimerJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _activeWorkout.value = _activeWorkout.value.copy(restTimerRemainingSeconds = remaining)
            }
            triggerHapticFeedback(longVibrate = true)
            _activeWorkout.value = _activeWorkout.value.copy(restTimerActive = false)
        }
    }

    fun cancelRestTimer() {
        restTimerJob?.cancel()
        _activeWorkout.value = _activeWorkout.value.copy(restTimerActive = false)
    }

    fun finishActiveWorkout(notes: String = "") {
        val state = _activeWorkout.value
        if (!state.isActive) return
        viewModelScope.launch {
            // Calculate estimated calories burned based on duration and sets
            val totalSets = state.currentSession?.exercises?.sumOf { ex -> ex.sets.count { it.isCompleted } } ?: 12
            val durationMinutes = (state.elapsedSeconds / 60).coerceAtLeast(1)
            // Estimated ~ 7-10 kcal per minute for strength training
            val calories = (durationMinutes * 7.5f + totalSets * 6f).toInt().coerceAtLeast(60)

            repository.finishWorkoutSession(
                sessionId = state.sessionId,
                durationSeconds = state.elapsedSeconds,
                caloriesBurned = calories,
                notes = notes
            )
            workoutTimerJob?.cancel()
            restTimerJob?.cancel()
            _activeWorkout.value = ActiveWorkoutUiState(isActive = false)
        }
    }

    fun discardActiveWorkout() {
        val state = _activeWorkout.value
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        viewModelScope.launch {
            repository.deleteWorkoutSession(state.sessionId)
            _activeWorkout.value = ActiveWorkoutUiState(isActive = false)
        }
    }

    fun deleteWorkoutSession(sessionId: Long) {
        viewModelScope.launch {
            repository.deleteWorkoutSession(sessionId)
        }
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (_activeWorkout.value.isActive) {
                delay(1000)
                _activeWorkout.value = _activeWorkout.value.copy(
                    elapsedSeconds = _activeWorkout.value.elapsedSeconds + 1
                )
            }
        }
    }

    private suspend fun refreshCurrentActiveSession() {
        val sessionId = _activeWorkout.value.sessionId
        if (sessionId != 0L) {
            val full = repository.getFullSession(sessionId)
            _activeWorkout.value = _activeWorkout.value.copy(currentSession = full)
        }
    }

    // Nutrition actions
    fun setSelectedNutritionDate(dateKey: String) {
        _selectedNutritionDate.value = dateKey
    }

    fun logMeal(
        mealType: String,
        name: String,
        calories: Int,
        protein: Float,
        carbs: Float,
        fats: Float
    ) {
        viewModelScope.launch {
            repository.logMeal(
                mealType = mealType,
                name = name,
                calories = calories,
                proteinGrams = protein,
                carbsGrams = carbs,
                fatsGrams = fats,
                dateKey = _selectedNutritionDate.value
            )
        }
    }

    fun logWater(ml: Int) {
        viewModelScope.launch {
            repository.logWater(ml, _selectedNutritionDate.value)
        }
    }

    fun deleteMeal(mealId: Long) {
        viewModelScope.launch {
            repository.deleteMeal(mealId)
        }
    }

    fun updateDailyTarget(target: DailyTarget) {
        viewModelScope.launch {
            repository.updateDailyTarget(target)
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.saveUserProfile(profile)
        }
    }

    fun setActivePlan(planId: Long) {
        viewModelScope.launch {
            repository.setActivePlan(planId)
        }
    }

    fun deletePlan(planId: Long) {
        viewModelScope.launch {
            repository.deletePlan(planId)
        }
    }

    suspend fun getFullSession(sessionId: Long): FullWorkoutSession? {
        return repository.getFullSession(sessionId)
    }

    suspend fun getFullTrainingPlan(planId: Long): com.example.data.model.FullTrainingPlan? {
        return repository.getFullTrainingPlan(planId)
    }

    // Personalized Training Plan Generator
    fun generatePersonalizedPlan(
        title: String,
        goal: String,
        level: String,
        equipment: String,
        daysPerWeek: Int,
        durationWeeks: Int = 8,
        makeActive: Boolean = true
    ) {
        viewModelScope.launch {
            val days = mutableListOf<PlanDayWorkout>()

            when (daysPerWeek) {
                3 -> {
                    // Full Body Split A, B, C
                    days.add(
                        PlanDayWorkout(
                            planId = 0,
                            dayNumber = 1,
                            dayTitle = "Day 1: Full Body Foundation & Power",
                            estimatedMinutes = 50,
                            targetMuscles = "Quads, Chest, Back",
                            exercisesSummary = "Barbell Back Squat (4x8), Barbell Bench Press (4x8), Barbell Bent-Over Row (3x8), Dumbbell Lateral Raise (3x12)"
                        )
                    )
                    days.add(
                        PlanDayWorkout(
                            planId = 0,
                            dayNumber = 2,
                            dayTitle = "Day 2: Posterior Chain & Pull Strength",
                            estimatedMinutes = 50,
                            targetMuscles = "Hamstrings, Upper Back, Biceps",
                            exercisesSummary = "Barbell Deadlift (4x6), Lat Pulldown (3x10), Incline Dumbbell Press (3x10), Barbell Bicep Curl (3x10)"
                        )
                    )
                    days.add(
                        PlanDayWorkout(
                            planId = 0,
                            dayNumber = 3,
                            dayTitle = "Day 3: Hypertrophy & Core Conditioning",
                            estimatedMinutes = 45,
                            targetMuscles = "Shoulders, Arms, Core",
                            exercisesSummary = "Overhead Barbell Press (4x8), Walking Dumbbell Lunges (3x12), Triceps Rope Pushdown (3x12), Hanging Leg Raise (3x12)"
                        )
                    )
                }
                4 -> {
                    // Upper / Lower Split
                    days.add(
                        PlanDayWorkout(
                            planId = 0,
                            dayNumber = 1,
                            dayTitle = "Day 1: Upper Body Power (Push & Pull)",
                            estimatedMinutes = 50,
                            targetMuscles = "Chest, Upper Back, Shoulders",
                            exercisesSummary = "Barbell Bench Press (4x6), Barbell Bent-Over Row (4x6), Overhead Barbell Press (3x8), Pull-Ups (3x8)"
                        )
                    )
                    days.add(
                        PlanDayWorkout(
                            planId = 0,
                            dayNumber = 2,
                            dayTitle = "Day 2: Lower Body Strength & Core",
                            estimatedMinutes = 55,
                            targetMuscles = "Quads, Hamstrings, Glutes",
                            exercisesSummary = "Barbell Back Squat (4x6), Romanian Deadlift (3x8), Leg Press (3x10), Hanging Leg Raise (3x12)"
                        )
                    )
                    days.add(
                        PlanDayWorkout(
                            planId = 0,
                            dayNumber = 3,
                            dayTitle = "Day 3: Upper Body Hypertrophy & Arms",
                            estimatedMinutes = 45,
                            targetMuscles = "Chest, Lats, Delts, Arms",
                            exercisesSummary = "Incline Dumbbell Press (3x10), Lat Pulldown (3x10), Dumbbell Lateral Raise (4x12), Incline Dumbbell Curl (3x12), Triceps Rope Pushdown (3x12)"
                        )
                    )
                    days.add(
                        PlanDayWorkout(
                            planId = 0,
                            dayNumber = 4,
                            dayTitle = "Day 4: Lower Body Volume & Calves",
                            estimatedMinutes = 45,
                            targetMuscles = "Legs & Core Conditioning",
                            exercisesSummary = "Walking Dumbbell Lunges (3x12), Standing Calf Raises (4x15), Cable Woodchoppers (3x12), HIIT Jump Rope (4x60s)"
                        )
                    )
                }
                5 -> {
                    // Push - Pull - Legs - Upper - Conditioning
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 1, dayTitle = "Day 1: Push (Chest & Shoulders)", estimatedMinutes = 50, targetMuscles = "Chest, Delts, Triceps", exercisesSummary = "Barbell Bench Press (4x8), Incline Dumbbell Press (3x10), Overhead Barbell Press (3x8), Dips (3x10)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 2, dayTitle = "Day 2: Pull (Back & Biceps)", estimatedMinutes = 50, targetMuscles = "Back & Biceps", exercisesSummary = "Barbell Deadlift (4x6), Pull-Ups (3x8), Barbell Bent-Over Row (3x8), Barbell Bicep Curl (3x10)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 3, dayTitle = "Day 3: Legs & Core Power", estimatedMinutes = 55, targetMuscles = "Quads, Hamstrings, Core", exercisesSummary = "Barbell Back Squat (4x8), Romanian Deadlift (3x10), Leg Press (3x12), Hanging Leg Raise (3x12)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 4, dayTitle = "Day 4: Upper Body Density & Arms", estimatedMinutes = 45, targetMuscles = "Arms & Shoulders", exercisesSummary = "Dumbbell Lateral Raise (4x15), Incline Dumbbell Curl (3x12), Triceps Rope Pushdown (3x12), Face Pulls (3x15)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 5, dayTitle = "Day 5: Athletic Conditioning & VO2 Max", estimatedMinutes = 40, targetMuscles = "Full Body & Cardio", exercisesSummary = "HIIT Jump Rope (5x60s), Treadmill Incline Run (1x20m), Rowing Machine Sprint (4x250m), Plank Hold (3x60s)"))
                }
                else -> {
                    // 6-day PPL x 2
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 1, dayTitle = "Day 1: Push Heavy", estimatedMinutes = 50, targetMuscles = "Chest & Shoulders", exercisesSummary = "Barbell Bench Press (4x6), Overhead Barbell Press (3x8), Cable Chest Flyes (3x12)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 2, dayTitle = "Day 2: Pull Heavy", estimatedMinutes = 50, targetMuscles = "Back & Traps", exercisesSummary = "Barbell Deadlift (4x5), Pull-Ups (3x8), Seated Cable Row (3x10)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 3, dayTitle = "Day 3: Legs Heavy", estimatedMinutes = 55, targetMuscles = "Quads & Glutes", exercisesSummary = "Barbell Back Squat (4x6), Romanian Deadlift (3x8), Standing Calf Raises (4x15)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 4, dayTitle = "Day 4: Push Hypertrophy", estimatedMinutes = 45, targetMuscles = "Chest, Front & Side Delts", exercisesSummary = "Incline Dumbbell Press (3x10), Dumbbell Lateral Raise (4x15), Triceps Rope Pushdown (3x12)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 5, dayTitle = "Day 5: Pull Hypertrophy", estimatedMinutes = 45, targetMuscles = "Lats & Biceps", exercisesSummary = "Lat Pulldown (3x10), Incline Dumbbell Curl (3x12), Face Pulls (3x15)"))
                    days.add(PlanDayWorkout(planId = 0, dayNumber = 6, dayTitle = "Day 6: Legs Hypertrophy & Abs", estimatedMinutes = 45, targetMuscles = "Hamstrings, Calves, Core", exercisesSummary = "Leg Press (3x12), Walking Dumbbell Lunges (3x12), Hanging Leg Raise (3x12)"))
                }
            }

            val finalTitle = title.ifBlank { "$goal $daysPerWeek-Day ($equipment)" }
            val description = "Tailored $goal program calibrated for $level level using $equipment across $daysPerWeek days per week."

            val planId = repository.createPlan(
                title = finalTitle,
                goal = goal,
                level = level,
                equipment = equipment,
                durationWeeks = durationWeeks,
                daysPerWeek = daysPerWeek,
                description = description,
                days = days,
                makeActive = makeActive
            )
        }
    }

    private fun triggerHapticFeedback(longVibrate: Boolean = false) {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                val effect = if (longVibrate) {
                    VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1)
                } else {
                    VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (longVibrate) {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(300)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(50)
                }
            }
        } catch (_: Exception) {
            // Vibrator not available or permission denied in test env
        }
    }
}
