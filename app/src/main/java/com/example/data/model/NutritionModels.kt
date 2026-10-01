package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "meal_entries")
data class MealEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateKey: String = getCurrentDateKey(), // "yyyy-MM-dd"
    val mealType: String, // "Breakfast", "Lunch", "Dinner", "Snack", "Water"
    val name: String,
    val calories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatsGrams: Float,
    val waterMl: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun getCurrentDateKey(timestamp: Long = System.currentTimeMillis()): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }
}

@Entity(tableName = "daily_targets")
data class DailyTarget(
    @PrimaryKey
    val id: Int = 1,
    val calorieGoal: Int = 2200,
    val proteinGoalGrams: Int = 160,
    val carbsGoalGrams: Int = 220,
    val fatsGoalGrams: Int = 60,
    val waterGoalMl: Int = 3000,
    val workoutDaysGoal: Int = 5
)

data class DayNutritionSummary(
    val dateKey: String,
    val totalCalories: Int,
    val totalProtein: Float,
    val totalCarbs: Float,
    val totalFats: Float,
    val totalWaterMl: Int,
    val entries: List<MealEntry>
)
