package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.PlanDayWorkout
import com.example.data.model.TrainingPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingPlanDao {
    @Query("SELECT * FROM training_plans ORDER BY id DESC")
    fun getAllPlans(): Flow<List<TrainingPlan>>

    @Query("SELECT * FROM training_plans WHERE isActive = 1 LIMIT 1")
    fun getActivePlan(): Flow<TrainingPlan?>

    @Query("SELECT * FROM training_plans WHERE isActive = 1 LIMIT 1")
    suspend fun getActivePlanSync(): TrainingPlan?

    @Query("SELECT * FROM training_plans WHERE id = :planId")
    suspend fun getPlanById(planId: Long): TrainingPlan?

    @Query("SELECT * FROM training_plans WHERE id = :planId")
    fun observePlanById(planId: Long): Flow<TrainingPlan?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: TrainingPlan): Long

    @Update
    suspend fun updatePlan(plan: TrainingPlan)

    @Query("UPDATE training_plans SET isActive = 0")
    suspend fun deactivateAllPlans()

    @Query("UPDATE training_plans SET isActive = 1 WHERE id = :planId")
    suspend fun activatePlan(planId: Long)

    @Query("DELETE FROM training_plans WHERE id = :planId")
    suspend fun deletePlan(planId: Long)

    // Plan Days
    @Query("SELECT * FROM plan_day_workouts WHERE planId = :planId ORDER BY dayNumber ASC")
    fun getDaysForPlan(planId: Long): Flow<List<PlanDayWorkout>>

    @Query("SELECT * FROM plan_day_workouts WHERE planId = :planId ORDER BY dayNumber ASC")
    suspend fun getDaysForPlanSync(planId: Long): List<PlanDayWorkout>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlanDays(days: List<PlanDayWorkout>)

    @Query("DELETE FROM plan_day_workouts WHERE planId = :planId")
    suspend fun deletePlanDays(planId: Long)
}
