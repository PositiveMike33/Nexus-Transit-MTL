package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import java.util.concurrent.TimeUnit

/**
 * Interface Retrofit pour interagir avec les endpoints GTFS-RT de la STM (Société de transport de Montréal).
 */
interface StmApiService {

    /**
     * Obtenir les positions des véhicules en temps réel (Bus & Métro STM).
     * @param apiKey Clé d'API développeur STM (passée via l'en-tête HTTP "apiKey")
     */
    @GET("gtfs-rt/v1/vehiclePositions")
    suspend fun getVehiclePositions(
        @Header("apiKey") apiKey: String = ""
    ): Response<GtfsRtFeedMessage>

    /**
     * Obtenir les mises à jour de trajets (horaires, retards, annulations) en temps réel.
     * @param apiKey Clé d'API développeur STM (passée via l'en-tête HTTP "apiKey")
     */
    @GET("gtfs-rt/v1/tripUpdates")
    suspend fun getTripUpdates(
        @Header("apiKey") apiKey: String = ""
    ): Response<GtfsRtFeedMessage>

    /**
     * Obtenir les alertes de service et perturbations du réseau STM.
     * @param apiKey Clé d'API développeur STM (passée via l'en-tête HTTP "apiKey")
     */
    @GET("gtfs-rt/v1/alerts")
    suspend fun getServiceAlerts(
        @Header("apiKey") apiKey: String = ""
    ): Response<GtfsRtFeedMessage>

    companion object {
        private const val BASE_URL = "https://api.stm.info/"

        /**
         * Crée et configure l'instance Retrofit pour StmApiService avec Moshi et OkHttp.
         */
        fun create(baseUrl: String = BASE_URL): StmApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(StmApiLoggingInterceptor())
                .addInterceptor(loggingInterceptor)
                .build()

            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

            return retrofit.create(StmApiService::class.java)
        }

        /**
         * Méthode utilitaire pour parser le flux GTFS-RT brut et extraire les localisations des véhicules STM.
         */
        fun parseVehicleLocations(feedMessage: GtfsRtFeedMessage?): List<StmVehicleLocation> {
            if (feedMessage?.entity == null) return emptyList()

            return feedMessage.entity.mapNotNull { entity ->
                val vehiclePos = entity.vehicle ?: return@mapNotNull null
                val pos = vehiclePos.position ?: return@mapNotNull null

                val vehicleId = vehiclePos.vehicle?.id
                    ?: vehiclePos.vehicle?.label
                    ?: entity.id.ifEmpty { "BUS-STM-UNKNOWN" }

                val routeId = vehiclePos.trip?.routeId ?: "Ligne-Inconnue"
                val tripId = vehiclePos.trip?.tripId ?: "Trip-Unknown"

                // Vitesse convertie de m/s en km/h
                val speedKmh = (pos.speed ?: 0f) * 3.6f

                StmVehicleLocation(
                    vehicleId = vehicleId,
                    routeId = routeId,
                    tripId = tripId,
                    latitude = pos.latitude,
                    longitude = pos.longitude,
                    speedKmh = speedKmh,
                    bearing = pos.bearing ?: 0f,
                    occupancyStatus = vehiclePos.occupancyStatus ?: "MANY_SEATS_AVAILABLE",
                    timestamp = vehiclePos.timestamp ?: System.currentTimeMillis() / 1000,
                    currentStatus = vehiclePos.currentStatus ?: "IN_TRANSIT_TO"
                )
            }
        }

        /**
         * Jeu de données simulées représentant la télémétrie STM temps réel à Montréal.
         */
        fun getMockVehiclePositions(): List<StmVehicleLocation> {
            val now = System.currentTimeMillis() / 1000
            return listOf(
                StmVehicleLocation(
                    vehicleId = "BUS-24-102",
                    routeId = "24 Sherbrooke",
                    tripId = "TRIP-24-A",
                    latitude = 45.5088,
                    longitude = -73.5878,
                    speedKmh = 28.5f,
                    bearing = 90f,
                    occupancyStatus = "MANY_SEATS_AVAILABLE",
                    timestamp = now,
                    currentStatus = "IN_TRANSIT_TO"
                ),
                StmVehicleLocation(
                    vehicleId = "BUS-55-408",
                    routeId = "55 Boulevard Saint-Laurent",
                    tripId = "TRIP-55-B",
                    latitude = 45.5180,
                    longitude = -73.5820,
                    speedKmh = 14.2f,
                    bearing = 180f,
                    occupancyStatus = "FEW_SEATS_AVAILABLE",
                    timestamp = now - 30,
                    currentStatus = "STOPPED_AT"
                ),
                StmVehicleLocation(
                    vehicleId = "METRO-L1-GREEN-08",
                    routeId = "Ligne Verte (1)",
                    tripId = "TRIP-METRO-01",
                    latitude = 45.5152,
                    longitude = -73.5605,
                    speedKmh = 52.0f,
                    bearing = 45f,
                    occupancyStatus = "STANDING_ROOM_ONLY",
                    timestamp = now - 15,
                    currentStatus = "IN_TRANSIT_TO"
                ),
                StmVehicleLocation(
                    vehicleId = "METRO-L2-ORANGE-14",
                    routeId = "Ligne Orange (2)",
                    tripId = "TRIP-METRO-02",
                    latitude = 45.5002,
                    longitude = -73.5670,
                    speedKmh = 48.0f,
                    bearing = 270f,
                    occupancyStatus = "MANY_SEATS_AVAILABLE",
                    timestamp = now - 10,
                    currentStatus = "IN_TRANSIT_TO"
                )
            )
        }
    }
}
