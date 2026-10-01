package com.example.data

import com.example.data.dao.MeasurementDao
import com.example.data.dao.NutritionDao
import com.example.data.dao.TrainingPlanDao
import com.example.data.dao.UserDao
import com.example.data.dao.WorkoutDao
import com.example.data.model.BodyMeasurement
import com.example.data.model.DailyTarget
import com.example.data.model.ExerciseRecord
import com.example.data.model.ExerciseSet
import com.example.data.model.FullExercise
import com.example.data.model.FullTrainingPlan
import com.example.data.model.FullWorkoutSession
import com.example.data.model.MealEntry
import com.example.data.model.PlanDayWorkout
import com.example.data.model.TrainingPlan
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.Calendar

class FitnessRepository(
    private val workoutDao: WorkoutDao,
    private val nutritionDao: NutritionDao,
    private val userDao: UserDao,
    private val trainingPlanDao: TrainingPlanDao,
    private val measurementDao: MeasurementDao
) {
    // Body Measurements
    val allMeasurementsAsc: Flow<List<BodyMeasurement>> = measurementDao.getAllMeasurementsAsc()
    val allMeasurementsDesc: Flow<List<BodyMeasurement>> = measurementDao.getAllMeasurementsDesc()
    val latestMeasurement: Flow<BodyMeasurement?> = measurementDao.getLatestMeasurement()
    val firstMeasurement: Flow<BodyMeasurement?> = measurementDao.getFirstMeasurement()

    suspend fun logBodyMeasurement(
        weightKg: Float,
        bodyFatPercentage: Float? = null,
        waistCm: Float? = null,
        chestCm: Float? = null,
        armsCm: Float? = null,
        notes: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val m = BodyMeasurement(
            weightKg = weightKg,
            bodyFatPercentage = bodyFatPercentage,
            waistCm = waistCm,
            chestCm = chestCm,
            armsCm = armsCm,
            notes = notes
        )
        measurementDao.insertMeasurement(m)
    }

    suspend fun deleteMeasurement(id: Long) = withContext(Dispatchers.IO) {
        measurementDao.deleteMeasurement(id)
    }

    // Analytics & Personal Records
    val personalBests: Flow<List<ExerciseRecord>> = workoutDao.getPersonalBests()
    val allMealEntries: Flow<List<MealEntry>> = nutritionDao.getAllMeals()

    // Workouts
    val allSessions: Flow<List<WorkoutSession>> = workoutDao.getAllSessions()
    val recentCompletedSessions: Flow<List<WorkoutSession>> = workoutDao.getRecentCompletedSessions(10)
    val totalCompletedWorkoutsCount: Flow<Int> = workoutDao.getTotalCompletedWorkoutsCount()

    fun observeSession(sessionId: Long): Flow<WorkoutSession?> = workoutDao.observeSessionById(sessionId)
    fun getExercisesForSession(sessionId: Long): Flow<List<WorkoutExercise>> = workoutDao.getExercisesForSession(sessionId)
    fun getSetsForExercise(exerciseId: Long): Flow<List<ExerciseSet>> = workoutDao.getSetsForExercise(exerciseId)

    suspend fun getFullSession(sessionId: Long): FullWorkoutSession? = withContext(Dispatchers.IO) {
        val session = workoutDao.getSessionById(sessionId) ?: return@withContext null
        val exercises = workoutDao.getExercisesForSessionList(sessionId)
        val fullExercises = exercises.map { ex ->
            val sets = workoutDao.getSetsForExerciseList(ex.id)
            FullExercise(exercise = ex, sets = sets)
        }
        FullWorkoutSession(session = session, exercises = fullExercises)
    }

    suspend fun createWorkoutSession(
        name: String,
        category: String,
        planId: Long? = null,
        notes: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val session = WorkoutSession(
            name = name,
            category = category,
            date = System.currentTimeMillis(),
            durationSeconds = 0,
            caloriesBurned = 0,
            completed = false,
            planId = planId,
            notes = notes
        )
        workoutDao.insertSession(session)
    }

    suspend fun addExerciseToSession(
        sessionId: Long,
        name: String,
        targetMuscle: String,
        orderIndex: Int,
        defaultSetsCount: Int = 3,
        defaultReps: Int = 10,
        defaultWeightKg: Float = 20f
    ): Long = withContext(Dispatchers.IO) {
        val exercise = WorkoutExercise(
            sessionId = sessionId,
            name = name,
            targetMuscle = targetMuscle,
            orderIndex = orderIndex
        )
        val exerciseId = workoutDao.insertExercise(exercise)
        for (i in 1..defaultSetsCount) {
            workoutDao.insertSet(
                ExerciseSet(
                    exerciseId = exerciseId,
                    setNumber = i,
                    reps = defaultReps,
                    weightKg = defaultWeightKg,
                    isCompleted = false
                )
            )
        }
        exerciseId
    }

    suspend fun updateSet(set: ExerciseSet) = withContext(Dispatchers.IO) {
        workoutDao.updateSet(set)
    }

    suspend fun addSetToExercise(exerciseId: Long, setNumber: Int, reps: Int, weightKg: Float) = withContext(Dispatchers.IO) {
        workoutDao.insertSet(
            ExerciseSet(
                exerciseId = exerciseId,
                setNumber = setNumber,
                reps = reps,
                weightKg = weightKg,
                isCompleted = false
            )
        )
    }

    suspend fun deleteSet(setId: Long) = withContext(Dispatchers.IO) {
        workoutDao.deleteSet(setId)
    }

    suspend fun deleteExercise(exerciseId: Long) = withContext(Dispatchers.IO) {
        workoutDao.deleteExercise(exerciseId)
    }

    suspend fun finishWorkoutSession(
        sessionId: Long,
        durationSeconds: Int,
        caloriesBurned: Int,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val session = workoutDao.getSessionById(sessionId) ?: return@withContext
        workoutDao.updateSession(
            session.copy(
                durationSeconds = durationSeconds,
                caloriesBurned = caloriesBurned,
                completed = true,
                notes = notes,
                date = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteWorkoutSession(sessionId: Long) = withContext(Dispatchers.IO) {
        workoutDao.deleteSession(sessionId)
    }

    fun getTodayCaloriesBurned(): Flow<Int?> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        val endOfDay = startOfDay + 86400000L - 1L
        return workoutDao.getCaloriesBurnedForDay(startOfDay, endOfDay)
    }

    // Nutrition
    fun getMealsForDate(dateKey: String): Flow<List<MealEntry>> = nutritionDao.getMealsForDate(dateKey)
    fun getDailyTarget(): Flow<DailyTarget?> = nutritionDao.getDailyTarget()

    suspend fun logMeal(
        mealType: String,
        name: String,
        calories: Int,
        proteinGrams: Float,
        carbsGrams: Float,
        fatsGrams: Float,
        waterMl: Int = 0,
        dateKey: String = MealEntry.getCurrentDateKey()
    ): Long = withContext(Dispatchers.IO) {
        val entry = MealEntry(
            dateKey = dateKey,
            mealType = mealType,
            name = name,
            calories = calories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatsGrams = fatsGrams,
            waterMl = waterMl
        )
        nutritionDao.insertMeal(entry)
    }

    suspend fun logWater(ml: Int, dateKey: String = MealEntry.getCurrentDateKey()) = withContext(Dispatchers.IO) {
        val entry = MealEntry(
            dateKey = dateKey,
            mealType = "Water",
            name = "Water Log",
            calories = 0,
            proteinGrams = 0f,
            carbsGrams = 0f,
            fatsGrams = 0f,
            waterMl = ml
        )
        nutritionDao.insertMeal(entry)
    }

    suspend fun deleteMeal(mealId: Long) = withContext(Dispatchers.IO) {
        nutritionDao.deleteMeal(mealId)
    }

    suspend fun updateDailyTarget(target: DailyTarget) = withContext(Dispatchers.IO) {
        nutritionDao.setDailyTarget(target)
    }

    // User Profile
    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()

    suspend fun saveUserProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        userDao.insertOrUpdateProfile(profile)
        // Automatically sync calculated daily target
        val recCalories = profile.calculateRecommendedCalories()
        val (p, c, f) = profile.calculateRecommendedMacros()
        val currentTarget = nutritionDao.getDailyTargetSync() ?: DailyTarget()
        nutritionDao.setDailyTarget(
            currentTarget.copy(
                calorieGoal = recCalories,
                proteinGoalGrams = p,
                carbsGoalGrams = c,
                fatsGoalGrams = f,
                workoutDaysGoal = profile.daysPerWeek
            )
        )
    }

    // Training Plans
    val allPlans: Flow<List<TrainingPlan>> = trainingPlanDao.getAllPlans()
    val activePlan: Flow<TrainingPlan?> = trainingPlanDao.getActivePlan()

    fun getDaysForPlan(planId: Long): Flow<List<PlanDayWorkout>> = trainingPlanDao.getDaysForPlan(planId)
    fun observePlanById(planId: Long): Flow<TrainingPlan?> = trainingPlanDao.observePlanById(planId)

    suspend fun getFullTrainingPlan(planId: Long): FullTrainingPlan? = withContext(Dispatchers.IO) {
        val plan = trainingPlanDao.getPlanById(planId) ?: return@withContext null
        val days = trainingPlanDao.getDaysForPlanSync(planId)
        FullTrainingPlan(plan = plan, days = days)
    }

    suspend fun setActivePlan(planId: Long) = withContext(Dispatchers.IO) {
        trainingPlanDao.deactivateAllPlans()
        trainingPlanDao.activatePlan(planId)
    }

    suspend fun createPlan(
        title: String,
        goal: String,
        level: String,
        equipment: String,
        durationWeeks: Int,
        daysPerWeek: Int,
        description: String,
        days: List<PlanDayWorkout>,
        makeActive: Boolean = false
    ): Long = withContext(Dispatchers.IO) {
        if (makeActive) {
            trainingPlanDao.deactivateAllPlans()
        }
        val plan = TrainingPlan(
            title = title,
            goal = goal,
            level = level,
            equipment = equipment,
            durationWeeks = durationWeeks,
            daysPerWeek = daysPerWeek,
            description = description,
            isActive = makeActive
        )
        val planId = trainingPlanDao.insertPlan(plan)
        val daysWithPlanId = days.map { it.copy(planId = planId) }
        trainingPlanDao.insertPlanDays(daysWithPlanId)
        planId
    }

    suspend fun deletePlan(planId: Long) = withContext(Dispatchers.IO) {
        trainingPlanDao.deletePlan(planId)
    }

    // Check if initial seeding is needed
    suspend fun ensureInitialized() = withContext(Dispatchers.IO) {
        val existingProfile = userDao.getUserProfileSync()
        if (existingProfile == null) {
            SampleData.seedInitialData(this@FitnessRepository)
        }
    }
}
