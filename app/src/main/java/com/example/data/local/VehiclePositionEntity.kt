package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.remote.StmVehicleLocation

/**
 * Entité de persistance Room pour mettre en cache les positions des véhicules STM GTFS-RT.
 */
@Entity(tableName = "vehicle_positions")
data class VehiclePositionEntity(
    @PrimaryKey val vehicleId: String,
    val routeId: String,
    val tripId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Float,
    val bearing: Float,
    val occupancyStatus: String,
    val timestamp: Long,
    val currentStatus: String,
    val lastCachedTimestamp: Long = System.currentTimeMillis()
) {
    fun toStmVehicleLocation(): StmVehicleLocation {
        return StmVehicleLocation(
            vehicleId = vehicleId,
            routeId = routeId,
            tripId = tripId,
            latitude = latitude,
            longitude = longitude,
            speedKmh = speedKmh,
            bearing = bearing,
            occupancyStatus = occupancyStatus,
            timestamp = timestamp,
            currentStatus = currentStatus
        )
    }

    companion object {
        fun fromStmVehicleLocation(location: StmVehicleLocation): VehiclePositionEntity {
            return VehiclePositionEntity(
                vehicleId = location.vehicleId,
                routeId = location.routeId,
                tripId = location.tripId,
                latitude = location.latitude,
                longitude = location.longitude,
                speedKmh = location.speedKmh,
                bearing = location.bearing,
                occupancyStatus = location.occupancyStatus,
                timestamp = location.timestamp,
                currentStatus = location.currentStatus
            )
        }
    }
}
