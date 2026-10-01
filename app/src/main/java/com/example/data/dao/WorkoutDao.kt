package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ExerciseRecord
import com.example.data.model.ExerciseSet
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("""
        SELECT e.name AS exerciseName, MAX(s.weightKg) AS maxWeightKg, s.reps AS reps, MAX(sess.date) AS sessionDate 
        FROM workout_exercises e 
        INNER JOIN exercise_sets s ON e.id = s.exerciseId 
        INNER JOIN workout_sessions sess ON e.sessionId = sess.id 
        WHERE s.isCompleted = 1 AND s.weightKg > 0 
        GROUP BY e.name 
        ORDER BY maxWeightKg DESC
    """)
    fun getPersonalBests(): Flow<List<ExerciseRecord>>

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): WorkoutSession?

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    fun observeSessionById(sessionId: Long): Flow<WorkoutSession?>

    @Query("SELECT * FROM workout_sessions WHERE completed = 1 ORDER BY date DESC LIMIT :limit")
    fun getRecentCompletedSessions(limit: Int = 10): Flow<List<WorkoutSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Update
    suspend fun updateSession(session: WorkoutSession)

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    // Exercises
    @Query("SELECT * FROM workout_exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    fun getExercisesForSession(sessionId: Long): Flow<List<WorkoutExercise>>

    @Query("SELECT * FROM workout_exercises WHERE sessionId = :sessionId ORDER BY orderIndex ASC")
    suspend fun getExercisesForSessionList(sessionId: Long): List<WorkoutExercise>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: WorkoutExercise): Long

    @Query("DELETE FROM workout_exercises WHERE id = :exerciseId")
    suspend fun deleteExercise(exerciseId: Long)

    // Sets
    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY setNumber ASC")
    fun getSetsForExercise(exerciseId: Long): Flow<List<ExerciseSet>>

    @Query("SELECT * FROM exercise_sets WHERE exerciseId = :exerciseId ORDER BY setNumber ASC")
    suspend fun getSetsForExerciseList(exerciseId: Long): List<ExerciseSet>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: ExerciseSet): Long

    @Update
    suspend fun updateSet(set: ExerciseSet)

    @Query("DELETE FROM exercise_sets WHERE id = :setId")
    suspend fun deleteSet(setId: Long)

    @Query("SELECT SUM(caloriesBurned) FROM workout_sessions WHERE completed = 1 AND date >= :startOfDayTimestamp AND date <= :endOfDayTimestamp")
    fun getCaloriesBurnedForDay(startOfDayTimestamp: Long, endOfDayTimestamp: Long): Flow<Int?>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE completed = 1")
    fun getTotalCompletedWorkoutsCount(): Flow<Int>
}
