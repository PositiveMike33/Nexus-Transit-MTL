package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.AppDatabase
import com.example.data.McpDao
import com.example.data.McpServerEntity
import com.example.data.RouteDao
import com.example.data.RouteEntity
import com.example.data.local.StmDatabase
import com.example.data.local.VehiclePositionDao
import com.example.data.local.VehiclePositionEntity
import com.example.data.remote.GtfsRtFeedMessage
import com.example.data.remote.StmApiService
import com.example.data.remote.StmVehicleLocation
import com.example.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Source unique de vérité pour les données de transport en commun STM GTFS-RT et la persistance Room.
 * Abstrait l'API Retrofit StmApiService et gère la mise en cache des véhicules et la persistance locale via StmDatabase.
 */
class StmRepository(
    private val apiService: StmApiService = NetworkModule.stmApiService,
    private val routeDao: RouteDao? = null,
    private val mcpDao: McpDao? = null,
    private val vehiclePositionDao: VehiclePositionDao? = null
) {
    companion object {
        private const val TAG = "StmRepository"

        @Volatile
        private var INSTANCE: StmRepository? = null

        fun getInstance(context: Context): StmRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context)
                val stmDb = StmDatabase.getInstance(context)
                val instance = StmRepository(
                    apiService = NetworkModule.stmApiService,
                    routeDao = db.routeDao(),
                    mcpDao = db.mcpDao(),
                    vehiclePositionDao = stmDb.vehiclePositionDao()
                )
                INSTANCE = instance
                instance
            }
        }
    }

    // Cache mémoire réactif pour les véhicules en temps réel
    private val _cachedVehiclePositions = MutableStateFlow<List<StmVehicleLocation>>(emptyList())
    val cachedVehiclePositions: StateFlow<List<StmVehicleLocation>> = _cachedVehiclePositions.asStateFlow()

    /**
     * Récupère la position des véhicules GTFS-RT en temps réel via l'API STM.
     * En cas d'erreur ou d'absence de réseau, utilise le cache mémoire ou les données simulées.
     */
    suspend fun getVehiclePositions(apiKey: String = ""): List<StmVehicleLocation> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getVehiclePositions(apiKey)
            if (response.isSuccessful && response.body() != null) {
                val parsedLocations = StmApiService.parseVehicleLocations(response.body())
                if (parsedLocations.isNotEmpty()) {
                    _cachedVehiclePositions.value = parsedLocations
                    // Sauvegarde dans la base de données Room pour le cache hors-ligne
                    vehiclePositionDao?.insertVehiclePositions(parsedLocations.map { VehiclePositionEntity.fromStmVehicleLocation(it) })
                    Log.i(TAG, "Mise à jour réussie de ${parsedLocations.size} véhicules STM dans l'API et la BDD Room.")
                    return@withContext parsedLocations
                }
            }
            Log.w(TAG, "Réponse API STM vide ou invalide. Basculement vers le cache Room ou données simulées.")
        } catch (e: Exception) {
            Log.e(TAG, "Erreur réseau lors de la récupération GTFS-RT STM: ${e.localizedMessage}")
        }

        // Stratégie de repli résiliente : Charger depuis le cache Room si disponible
        try {
            val dbVehicles = vehiclePositionDao?.getVehiclePositionsSync()?.map { it.toStmVehicleLocation() }
            if (!dbVehicles.isNullOrEmpty()) {
                _cachedVehiclePositions.value = dbVehicles
                Log.i(TAG, "Restauration de ${dbVehicles.size} véhicules depuis la BDD Room StmDatabase.")
                return@withContext dbVehicles
            }
        } catch (dbError: Exception) {
            Log.e(TAG, "Erreur lors de la lecture du cache Room: ${dbError.localizedMessage}")
        }

        if (_cachedVehiclePositions.value.isNotEmpty()) {
            _cachedVehiclePositions.value
        } else {
            val mockData = StmApiService.getMockVehiclePositions()
            _cachedVehiclePositions.value = mockData
            vehiclePositionDao?.insertVehiclePositions(mockData.map { VehiclePositionEntity.fromStmVehicleLocation(it) })
            mockData
        }
    }

    /**
     * Récupère les mises à jour de trajets (TripUpdates) GTFS-RT.
     */
    suspend fun getTripUpdates(apiKey: String = ""): Result<GtfsRtFeedMessage?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTripUpdates(apiKey)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Erreur HTTP STM GTFS-RT: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Récupère les alertes de service GTFS-RT.
     */
    suspend fun getServiceAlerts(apiKey: String = ""): Result<GtfsRtFeedMessage?> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getServiceAlerts(apiKey)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("Erreur HTTP STM GTFS-RT: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- LOGIQUE DE CACHE & PERSISTANCE ROOM ---

    /**
     * Flux réactif des itinéraires enregistrés dans la base de données Room.
     */
    fun getSavedRoutes(): Flow<List<RouteEntity>>? {
        return routeDao?.getAllRoutes()
    }

    /**
     * Enregistre un itinéraire calculé dans la base de données locale.
     */
    suspend fun saveRoute(route: RouteEntity) = withContext(Dispatchers.IO) {
        routeDao?.insertRoute(route)
    }

    /**
     * Supprime un itinéraire enregistré par son identifiant.
     */
    suspend fun deleteRoute(id: Long) = withContext(Dispatchers.IO) {
        routeDao?.deleteRoute(id)
    }

    /**
     * Obtenir tous les serveurs MCP enregistrés.
     */
    fun getMcpServers(): Flow<List<McpServerEntity>>? {
        return mcpDao?.getAllMcpServers()
    }

    /**
     * Insérer ou mettre à jour un serveur MCP.
     */
    suspend fun updateMcpServer(server: McpServerEntity) = withContext(Dispatchers.IO) {
        mcpDao?.insertOrUpdateMcpServer(server)
    }
}
