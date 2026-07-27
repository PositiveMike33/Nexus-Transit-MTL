package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Message racine pour les flux GTFS-Realtime (GTFS-RT) de la STM.
 */
@JsonClass(generateAdapter = true)
data class FeedMessage(
    @field:Json(name = "header") val header: FeedHeader? = null,
    @field:Json(name = "entity") val entity: List<FeedEntity> = emptyList()
)

/**
 * Entête du flux GTFS-RT décrivant la version et le horodatage.
 */
@JsonClass(generateAdapter = true)
data class FeedHeader(
    @field:Json(name = "gtfs_realtime_version") val gtfsRealtimeVersion: String? = "2.0",
    @field:Json(name = "incrementality") val incrementality: String? = "FULL_DATASET",
    @field:Json(name = "timestamp") val timestamp: Long? = null
)

/**
 * Entité du flux GTFS-RT contenant un véhicule, une mise à jour de trajet ou une alerte.
 */
@JsonClass(generateAdapter = true)
data class FeedEntity(
    @field:Json(name = "id") val id: String = "",
    @field:Json(name = "is_deleted") val isDeleted: Boolean? = false,
    @field:Json(name = "vehicle") val vehicle: VehiclePosition? = null,
    @field:Json(name = "trip_update") val tripUpdate: TripUpdate? = null,
    @field:Json(name = "alert") val alert: ServiceAlert? = null
)

/**
 * Modèle de données pour la position temps réel d'un véhicule STM.
 */
@JsonClass(generateAdapter = true)
data class VehiclePosition(
    @field:Json(name = "trip") val trip: TripDescriptor? = null,
    @field:Json(name = "position") val position: Position? = null,
    @field:Json(name = "current_stop_sequence") val currentStopSequence: Int? = null,
    @field:Json(name = "current_status") val currentStatus: String? = null,
    @field:Json(name = "timestamp") val timestamp: Long? = null,
    @field:Json(name = "congestion_level") val congestionLevel: String? = null,
    @field:Json(name = "stop_id") val stopId: String? = null,
    @field:Json(name = "vehicle") val vehicle: VehicleDescriptor? = null,
    @field:Json(name = "occupancy_status") val occupancyStatus: String? = null
)

/**
 * Modèle pour les mises à jour de trajets (retards, passages aux arrêts).
 */
@JsonClass(generateAdapter = true)
data class TripUpdate(
    @field:Json(name = "trip") val trip: TripDescriptor? = null,
    @field:Json(name = "vehicle") val vehicle: VehicleDescriptor? = null,
    @field:Json(name = "stop_time_update") val stopTimeUpdate: List<StopTimeUpdate> = emptyList(),
    @field:Json(name = "timestamp") val timestamp: Long? = null,
    @field:Json(name = "delay") val delay: Int? = null
)

/**
 * Descripteur de trajet GTFS.
 */
@JsonClass(generateAdapter = true)
data class TripDescriptor(
    @field:Json(name = "trip_id") val tripId: String? = null,
    @field:Json(name = "route_id") val routeId: String? = null,
    @field:Json(name = "direction_id") val directionId: Int? = null,
    @field:Json(name = "start_time") val startTime: String? = null,
    @field:Json(name = "start_date") val startDate: String? = null,
    @field:Json(name = "schedule_relationship") val scheduleRelationship: String? = null
)

/**
 * Coordonnées géographiques et vitesse du véhicule.
 */
@JsonClass(generateAdapter = true)
data class Position(
    @field:Json(name = "latitude") val latitude: Double = 0.0,
    @field:Json(name = "longitude") val longitude: Double = 0.0,
    @field:Json(name = "bearing") val bearing: Float? = 0f,
    @field:Json(name = "odometer") val odometer: Double? = 0.0,
    @field:Json(name = "speed") val speed: Float? = 0f
)

/**
 * Identifiant et étiquette du véhicule STM.
 */
@JsonClass(generateAdapter = true)
data class VehicleDescriptor(
    @field:Json(name = "id") val id: String? = null,
    @field:Json(name = "label") val label: String? = null,
    @field:Json(name = "license_plate") val licensePlate: String? = null
)

/**
 * Mise à jour de temps de passage à un arrêt spécifique.
 */
@JsonClass(generateAdapter = true)
data class StopTimeUpdate(
    @field:Json(name = "stop_sequence") val stopSequence: Int? = null,
    @field:Json(name = "stop_id") val stopId: String? = null,
    @field:Json(name = "arrival") val arrival: StopTimeEvent? = null,
    @field:Json(name = "departure") val departure: StopTimeEvent? = null,
    @field:Json(name = "schedule_relationship") val scheduleRelationship: String? = null
)

/**
 * Événement d'arrivée ou de départ estimé.
 */
@JsonClass(generateAdapter = true)
data class StopTimeEvent(
    @field:Json(name = "delay") val delay: Int? = 0,
    @field:Json(name = "time") val time: Long? = null,
    @field:Json(name = "uncertainty") val uncertainty: Int? = null
)

/**
 * Alerte de service STM.
 */
@JsonClass(generateAdapter = true)
data class ServiceAlert(
    @field:Json(name = "cause") val cause: String? = null,
    @field:Json(name = "effect") val effect: String? = null,
    @field:Json(name = "header_text") val headerText: TranslatedString? = null,
    @field:Json(name = "description_text") val descriptionText: TranslatedString? = null
)

@JsonClass(generateAdapter = true)
data class TranslatedString(
    @field:Json(name = "translation") val translation: List<Translation> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Translation(
    @field:Json(name = "text") val text: String = "",
    @field:Json(name = "language") val language: String? = null
)

/**
 * Représentation simplifiée pour la télémétrie UI STM.
 */
data class StmVehicleLocation(
    val vehicleId: String,
    val routeId: String,
    val tripId: String,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Float,
    val bearing: Float,
    val occupancyStatus: String,
    val timestamp: Long,
    val currentStatus: String
)
