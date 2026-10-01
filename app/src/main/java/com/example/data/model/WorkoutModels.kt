package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Strength", "HIIT", "Cardio", "Hypertrophy", "Mobility"
    val date: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val caloriesBurned: Int = 0,
    val completed: Boolean = false,
    val planId: Long? = null,
    val notes: String = ""
)

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val name: String,
    val targetMuscle: String, // "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio"
    val orderIndex: Int = 0,
    val notes: String = ""
)

@Entity(
    tableName = "exercise_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId")]
)
data class ExerciseSet(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val exerciseId: Long,
    val setNumber: Int,
    val reps: Int = 10,
    val weightKg: Float = 0f,
    val isCompleted: Boolean = false
)

data class FullExercise(
    val exercise: WorkoutExercise,
    val sets: List<ExerciseSet> = emptyList()
)

data class FullWorkoutSession(
    val session: WorkoutSession,
    val exercises: List<FullExercise> = emptyList()
)
