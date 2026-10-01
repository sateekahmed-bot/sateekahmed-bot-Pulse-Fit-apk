package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BodyMeasurement
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM body_measurements ORDER BY timestamp ASC")
    fun getAllMeasurementsAsc(): Flow<List<BodyMeasurement>>

    @Query("SELECT * FROM body_measurements ORDER BY timestamp DESC")
    fun getAllMeasurementsDesc(): Flow<List<BodyMeasurement>>

    @Query("SELECT * FROM body_measurements ORDER BY timestamp DESC LIMIT 1")
    fun getLatestMeasurement(): Flow<BodyMeasurement?>

    @Query("SELECT * FROM body_measurements ORDER BY timestamp ASC LIMIT 1")
    fun getFirstMeasurement(): Flow<BodyMeasurement?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: BodyMeasurement): Long

    @Query("DELETE FROM body_measurements WHERE id = :id")
    suspend fun deleteMeasurement(id: Long)
}
