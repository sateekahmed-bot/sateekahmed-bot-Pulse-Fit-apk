package com.example.data

import com.example.data.model.DailyTarget
import com.example.data.model.MealEntry
import com.example.data.model.PlanDayWorkout
import com.example.data.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SampleData {

    suspend fun seedInitialData(repo: FitnessRepository) {
        // 1. Seed User Profile
        val defaultProfile = UserProfile(
            id = 1,
            name = "Alex Vance",
            age = 26,
            gender = "Male",
            weightKg = 75f,
            heightCm = 178f,
            fitnessGoal = "Build Muscle",
            fitnessLevel = "Intermediate",
            equipment = "Full Gym",
            daysPerWeek = 4,
            targetWeightKg = 78f
        )
        repo.saveUserProfile(defaultProfile)

        // 2. Daily target
        repo.updateDailyTarget(
            DailyTarget(
                id = 1,
                calorieGoal = 2350,
                proteinGoalGrams = 160,
                carbsGoalGrams = 240,
                fatsGoalGrams = 65,
                waterGoalMl = 3000,
                workoutDaysGoal = 4
            )
        )

        // 3. Seed Training Plans
        val pplDays = listOf(
            PlanDayWorkout(
                planId = 0,
                dayNumber = 1,
                dayTitle = "Day 1: Upper Body Push (Chest, Shoulders, Triceps)",
                estimatedMinutes = 50,
                targetMuscles = "Chest, Front Delts, Triceps",
                exercisesSummary = "Barbell Bench Press (4x8), Incline Dumbbell Press (3x10), Overhead Barbell Press (3x8), Triceps Rope Pushdown (3x12)"
            ),
            PlanDayWorkout(
                planId = 0,
                dayNumber = 2,
                dayTitle = "Day 2: Upper Body Pull (Back & Biceps)",
                estimatedMinutes = 50,
                targetMuscles = "Lats, Upper Back, Biceps",
                exercisesSummary = "Barbell Deadlift (4x6), Pull-Ups (3x8), Lat Pulldown (3x10), Barbell Bicep Curl (3x10), Face Pulls (3x15)"
            ),
            PlanDayWorkout(
                planId = 0,
                dayNumber = 3,
                dayTitle = "Day 3: Lower Body Power & Core",
                estimatedMinutes = 55,
                targetMuscles = "Quads, Hamstrings, Glutes, Abs",
                exercisesSummary = "Barbell Back Squat (4x8), Romanian Deadlift (3x10), Leg Press (3x12), Hanging Leg Raise (3x12)"
            ),
            PlanDayWorkout(
                planId = 0,
                dayNumber = 4,
                dayTitle = "Day 4: Athletic Hypertrophy & Conditioning",
                estimatedMinutes = 45,
                targetMuscles = "Full Body & Delts",
                exercisesSummary = "Dumbbell Lateral Raise (4x15), Dips (3x10), Walking Dumbbell Lunges (3x12), HIIT Jump Rope (4x60s)"
            )
        )
        repo.createPlan(
            title = "Hypertrophy Push-Pull 4-Day",
            goal = "Build Muscle",
            level = "Intermediate",
            equipment = "Full Gym",
            durationWeeks = 8,
            daysPerWeek = 4,
            description = "High-efficiency 4-day split emphasizing compound lifts, progressive overload, and hypertrophy accessories.",
            days = pplDays,
            makeActive = true
        )

        val shredDays = listOf(
            PlanDayWorkout(
                planId = 0,
                dayNumber = 1,
                dayTitle = "Day 1: Full Body Metabolic Circuit",
                estimatedMinutes = 40,
                targetMuscles = "Full Body & Cardio",
                exercisesSummary = "Barbell Back Squat (3x12), Push-ups (3x15), Seated Cable Row (3x12), Plank Hold (3x45s)"
            ),
            PlanDayWorkout(
                planId = 0,
                dayNumber = 2,
                dayTitle = "Day 2: HIIT Intervals & Core",
                estimatedMinutes = 35,
                targetMuscles = "Cardio, Core",
                exercisesSummary = "HIIT Jump Rope (5x60s), Hanging Leg Raise (4x12), Cable Woodchoppers (3x12), Rowing Machine Sprint (4x250m)"
            ),
            PlanDayWorkout(
                planId = 0,
                dayNumber = 3,
                dayTitle = "Day 3: Upper Body Tone & Density",
                estimatedMinutes = 45,
                targetMuscles = "Chest, Back, Arms",
                exercisesSummary = "Incline Dumbbell Press (3x12), Lat Pulldown (3x12), Dumbbell Lateral Raise (4x15), Incline Dumbbell Curl (3x12)"
            )
        )
        repo.createPlan(
            title = "Metabolic Shred & Core (3-Day)",
            goal = "Fat Loss & Cut",
            level = "Beginner/Intermediate",
            equipment = "Full Gym / Dumbbells",
            durationWeeks = 6,
            daysPerWeek = 3,
            description = "Calorie-torching circuit routines engineered to maximize EPOC afterburn while protecting lean muscle mass.",
            days = shredDays,
            makeActive = false
        )

        // 4. Seed Historical Body Measurements (past 4 weeks)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        // 28 days ago
        cal.add(Calendar.DAY_OF_YEAR, -28)
        repo.logBodyMeasurement(
            weightKg = 77.2f,
            bodyFatPercentage = 18.5f,
            waistCm = 84f,
            chestCm = 101f,
            armsCm = 35.5f,
            notes = "Starting phase baseline measurement."
        )

        // 21 days ago
        cal.add(Calendar.DAY_OF_YEAR, 7)
        repo.logBodyMeasurement(
            weightKg = 76.6f,
            bodyFatPercentage = 18.0f,
            waistCm = 83.2f,
            chestCm = 101.5f,
            armsCm = 35.8f,
            notes = "Waist down, good energy throughout training."
        )

        // 14 days ago
        cal.add(Calendar.DAY_OF_YEAR, 7)
        repo.logBodyMeasurement(
            weightKg = 75.9f,
            bodyFatPercentage = 17.3f,
            waistCm = 82.5f,
            chestCm = 102f,
            armsCm = 36.2f,
            notes = "Visible upper ab definition appearing."
        )

        // 7 days ago
        cal.add(Calendar.DAY_OF_YEAR, 7)
        repo.logBodyMeasurement(
            weightKg = 75.3f,
            bodyFatPercentage = 16.7f,
            waistCm = 81.8f,
            chestCm = 102.5f,
            armsCm = 36.5f,
            notes = "Maintaining high protein, strength continuing to climb."
        )

        // Yesterday
        cal.add(Calendar.DAY_OF_YEAR, 6)
        repo.logBodyMeasurement(
            weightKg = 75.0f,
            bodyFatPercentage = 16.2f,
            waistCm = 81.0f,
            chestCm = 103f,
            armsCm = 36.8f,
            notes = "Target weight reached! Lean muscle building phase working well."
        )

        // 5. Seed Historical Meals for the last 6 days
        val pastDaysCal = Calendar.getInstance()
        val dayOffsets = listOf(-6, -5, -4, -3, -2, -1)
        val calorieProfiles = listOf(
            Triple(2280, 155f, 230f),
            Triple(2340, 162f, 245f),
            Triple(2190, 148f, 220f),
            Triple(2410, 168f, 250f),
            Triple(2300, 158f, 235f),
            Triple(2380, 165f, 242f)
        )

        dayOffsets.forEachIndexed { idx, offset ->
            pastDaysCal.time = Date()
            pastDaysCal.add(Calendar.DAY_OF_YEAR, offset)
            val dateKey = sdf.format(pastDaysCal.time)
            val (calVal, prot, carbs) = calorieProfiles[idx]

            repo.logMeal(
                mealType = "Breakfast",
                name = "Morning Oats & Whey Shake",
                calories = (calVal * 0.25f).toInt(),
                proteinGrams = (prot * 0.28f),
                carbsGrams = (carbs * 0.35f),
                fatsGrams = 14f,
                dateKey = dateKey
            )
            repo.logMeal(
                mealType = "Lunch",
                name = "Steak / Chicken Rice Bowl",
                calories = (calVal * 0.40f).toInt(),
                proteinGrams = (prot * 0.42f),
                carbsGrams = (carbs * 0.40f),
                fatsGrams = 22f,
                dateKey = dateKey
            )
            repo.logMeal(
                mealType = "Dinner",
                name = "Salmon / Turkey & Greens",
                calories = (calVal * 0.35f).toInt(),
                proteinGrams = (prot * 0.30f),
                carbsGrams = (carbs * 0.25f),
                fatsGrams = 20f,
                dateKey = dateKey
            )
        }

        // Today's Meals
        val todayKey = MealEntry.getCurrentDateKey()
        repo.logMeal(
            mealType = "Breakfast",
            name = "Steel-Cut Oats with Whey, Blueberries & Chia",
            calories = 520,
            proteinGrams = 38f,
            carbsGrams = 68f,
            fatsGrams = 11f,
            dateKey = todayKey
        )
        repo.logMeal(
            mealType = "Lunch",
            name = "Grilled Chicken Breast, Brown Rice & Broccoli",
            calories = 640,
            proteinGrams = 52f,
            carbsGrams = 70f,
            fatsGrams = 12f,
            dateKey = todayKey
        )
        repo.logMeal(
            mealType = "Snack",
            name = "Greek Yogurt (0% Fat) with Honey & Almonds",
            calories = 290,
            proteinGrams = 24f,
            carbsGrams = 22f,
            fatsGrams = 10f,
            dateKey = todayKey
        )
        repo.logWater(750, todayKey)
        repo.logWater(500, todayKey)
        repo.logWater(500, todayKey)

        // 6. Seed Past Completed Workout Sessions
        // Session A: 4 days ago - Legs Power (Squat PR: 85kg)
        val s0 = repo.createWorkoutSession(
            name = "Day 3: Lower Body Power & Core",
            category = "Strength",
            notes = "Hit personal best on Back Squat!"
        )
        repo.addExerciseToSession(s0, "Barbell Back Squat", "Legs", 1, 4, 8, 85f)
        repo.addExerciseToSession(s0, "Romanian Deadlift", "Legs", 2, 3, 10, 65f)
        repo.addExerciseToSession(s0, "Leg Press", "Legs", 3, 3, 12, 160f)
        repo.finishWorkoutSession(s0, 3300, 510, "Personal best on squats.")

        // Session B: 2 days ago - Push Power (Bench PR: 70kg, OHP: 45kg)
        val s2 = repo.createWorkoutSession(
            name = "Day 1: Upper Body Push Power",
            category = "Strength",
            notes = "Clean form, strict 90s rest intervals."
        )
        repo.addExerciseToSession(s2, "Barbell Bench Press", "Chest", 1, 4, 8, 70f)
        repo.addExerciseToSession(s2, "Overhead Barbell Press", "Shoulders", 2, 4, 8, 45f)
        repo.addExerciseToSession(s2, "Dips", "Chest", 3, 3, 10, 0f)
        repo.finishWorkoutSession(s2, 3100, 470, "Heavy bench press breakthrough.")

        // Session C: Yesterday - Pull Strength (Deadlift PR: 95kg)
        val s1 = repo.createWorkoutSession(
            name = "Day 2: Upper Body Pull & Lats",
            category = "Strength",
            notes = "New deadlift record achieved!"
        )
        repo.addExerciseToSession(s1, "Barbell Deadlift", "Back", 1, 4, 6, 95f)
        repo.addExerciseToSession(s1, "Pull-Ups", "Back", 2, 3, 8, 0f)
        repo.addExerciseToSession(s1, "Barbell Bicep Curl", "Arms", 3, 3, 10, 32.5f)
        repo.finishWorkoutSession(s1, 2900, 440, "Deadlift 95kg felt solid.")
    }
}
