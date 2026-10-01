package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateKey: String = getCurrentDateKey(timestamp),
    val weightKg: Float,
    val bodyFatPercentage: Float? = null,
    val waistCm: Float? = null,
    val chestCm: Float? = null,
    val armsCm: Float? = null,
    val notes: String = ""
) {
    companion object {
        fun getCurrentDateKey(time: Long = System.currentTimeMillis()): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date(time))
        }
    }
}

data class ExerciseRecord(
    val exerciseName: String,
    val maxWeightKg: Float,
    val reps: Int = 1,
    val sessionDate: Long = System.currentTimeMillis()
)

data class DailyCalorieStat(
    val dateKey: String,
    val dateLabel: String, // e.g. "Mon" or "Sep 28"
    val timestamp: Long,
    val calories: Int,
    val targetCalories: Int,
    val proteinGrams: Float,
    val carbsGrams: Float,
    val fatsGrams: Float
)

data class WorkoutTrendStat(
    val sessionId: Long,
    val dateLabel: String,
    val timestamp: Long,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val totalVolumeKg: Float,
    val exerciseCount: Int,
    val category: String,
    val title: String
)
