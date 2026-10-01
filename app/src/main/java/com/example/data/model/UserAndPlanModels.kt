package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "Alex Vance",
    val age: Int = 26,
    val gender: String = "Male", // "Male", "Female", "Other"
    val weightKg: Float = 75f,
    val heightCm: Float = 178f,
    val fitnessGoal: String = "Build Muscle", // "Build Muscle", "Fat Loss & Cut", "Strength & Power", "Athletic Endurance"
    val fitnessLevel: String = "Intermediate", // "Beginner", "Intermediate", "Advanced"
    val equipment: String = "Full Gym", // "Full Gym", "Dumbbells Only", "Bodyweight & Bands"
    val daysPerWeek: Int = 4,
    val targetWeightKg: Float = 78f
) {
    // Mifflin-St Jeor TDEE estimation
    fun calculateBmr(): Int {
        val s = if (gender.equals("Female", ignoreCase = true)) -161 else 5
        val bmr = (10 * weightKg) + (6.25f * heightCm) - (5 * age) + s
        return bmr.toInt().coerceAtLeast(1200)
    }

    fun calculateRecommendedCalories(): Int {
        val activityMultiplier = when (daysPerWeek) {
            1, 2 -> 1.25f
            3, 4 -> 1.45f
            5 -> 1.6f
            else -> 1.75f
        }
        val tdee = calculateBmr() * activityMultiplier
        return when (fitnessGoal) {
            "Fat Loss & Cut" -> (tdee - 450).toInt().coerceAtLeast(1400)
            "Build Muscle" -> (tdee + 300).toInt()
            "Strength & Power" -> (tdee + 200).toInt()
            else -> tdee.toInt()
        }
    }

    fun calculateRecommendedMacros(): Triple<Int, Int, Int> { // Protein, Carbs, Fats
        val calories = calculateRecommendedCalories()
        // Protein: 2.0g per kg for muscle/strength, 2.2g for cut, 1.6g for endurance
        val proteinPerKg = when (fitnessGoal) {
            "Fat Loss & Cut" -> 2.2f
            "Build Muscle", "Strength & Power" -> 2.0f
            else -> 1.6f
        }
        val proteinGrams = (weightKg * proteinPerKg).toInt().coerceIn(80, 260)
        val proteinCalories = proteinGrams * 4

        // Fats: ~25% of total calories
        val fatCalories = (calories * 0.25f).toInt()
        val fatGrams = (fatCalories / 9).coerceIn(40, 110)

        // Remaining calories for carbs
        val carbCalories = (calories - proteinCalories - fatCalories).coerceAtLeast(400)
        val carbGrams = (carbCalories / 4).coerceIn(100, 450)

        return Triple(proteinGrams, carbGrams, fatGrams)
    }
}

@Entity(tableName = "training_plans")
data class TrainingPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val goal: String,
    val level: String,
    val equipment: String,
    val durationWeeks: Int = 8,
    val daysPerWeek: Int = 4,
    val description: String,
    val isActive: Boolean = false
)

@Entity(
    tableName = "plan_day_workouts",
    foreignKeys = [
        ForeignKey(
            entity = TrainingPlan::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planId")]
)
data class PlanDayWorkout(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val planId: Long,
    val dayNumber: Int, // 1 to 7
    val dayTitle: String, // "Day 1: Upper Body Push"
    val estimatedMinutes: Int = 45,
    val targetMuscles: String = "Chest, Shoulders, Triceps",
    val exercisesSummary: String // JSON-like or comma separated overview
)

data class FullTrainingPlan(
    val plan: TrainingPlan,
    val days: List<PlanDayWorkout> = emptyList()
)
