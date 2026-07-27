package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) Room pour gérer la mise en cache locale et les requêtes des véhicules STM.
 */
@Dao
interface VehiclePositionDao {

    @Query("SELECT * FROM vehicle_positions ORDER BY lastCachedTimestamp DESC")
    fun getAllVehiclePositions(): Flow<List<VehiclePositionEntity>>

    @Query("SELECT * FROM vehicle_positions ORDER BY lastCachedTimestamp DESC")
    suspend fun getVehiclePositionsSync(): List<VehiclePositionEntity>

    @Query("SELECT * FROM vehicle_positions WHERE routeId = :routeId")
    fun getVehiclePositionsForRoute(routeId: String): Flow<List<VehiclePositionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehiclePositions(vehicles: List<VehiclePositionEntity>)

    @Query("DELETE FROM vehicle_positions")
    suspend fun clearAll()
}
