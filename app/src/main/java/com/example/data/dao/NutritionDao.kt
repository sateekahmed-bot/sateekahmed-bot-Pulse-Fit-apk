package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyTarget
import com.example.data.model.MealEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface NutritionDao {
    @Query("SELECT * FROM meal_entries WHERE dateKey = :dateKey ORDER BY timestamp DESC")
    fun getMealsForDate(dateKey: String): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries ORDER BY timestamp ASC")
    fun getAllMeals(): Flow<List<MealEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntry): Long

    @Query("DELETE FROM meal_entries WHERE id = :mealId")
    suspend fun deleteMeal(mealId: Long)

    @Query("SELECT * FROM meal_entries ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMeals(limit: Int = 20): Flow<List<MealEntry>>

    // Daily Targets
    @Query("SELECT * FROM daily_targets WHERE id = 1")
    fun getDailyTarget(): Flow<DailyTarget?>

    @Query("SELECT * FROM daily_targets WHERE id = 1")
    suspend fun getDailyTargetSync(): DailyTarget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setDailyTarget(target: DailyTarget)

    @Query("SELECT SUM(calories) FROM meal_entries WHERE dateKey = :dateKey")
    fun getTotalCaloriesForDate(dateKey: String): Flow<Int?>

    @Query("SELECT SUM(waterMl) FROM meal_entries WHERE dateKey = :dateKey")
    fun getTotalWaterForDate(dateKey: String): Flow<Int?>
}
