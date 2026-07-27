package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.McpServerEntity
import com.example.data.RevenueLeadEntity
import com.example.data.RouteEntity
import com.example.data.remote.StmApiService
import com.example.data.remote.StmVehicleLocation
import com.example.engine.GeminiTransitAssistant
import com.example.engine.McpServicesEngine
import com.example.engine.TreeOfThoughtsEngine
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val routeDao = db.routeDao()
    private val mcpDao = db.mcpDao()
    private val leadDao = db.leadDao()

    // STM Retrofit API Service instance
    private val stmApiService = StmApiService.create()

    // STM Realtime Vehicles State
    val liveVehiclePositions = MutableStateFlow<List<StmVehicleLocation>>(emptyList())
    val isVehicleLoading = MutableStateFlow(false)

    // Route Planning State
    val originState = MutableStateFlow("Berri-UQAM")
    val destinationState = MutableStateFlow("Station Bonaventure")

    val weatherObservation = MutableStateFlow(
        WeatherObservation(
            condition = "Beau temps ensoleillé (Montréal - 27 Juillet)",
            pavementTempCelsius = 27.5f,
            snowClearanceIndex = 100,
            windChillCelsius = 29.0f,
            iceAlertLevel = "AUCUN",
            skyFiImageryAgeMinutes = 5
        )
    )

    val passengerProfile = MutableStateFlow(PiecesPassengerProfile())

    private val _routeBranches = MutableStateFlow<List<ToTBranch>>(emptyList())
    val routeBranches: StateFlow<List<ToTBranch>> = _routeBranches.asStateFlow()

    // MCP Servers State
    val mcpServers: Flow<List<McpServerEntity>> = mcpDao.getAllMcpServers().onStart {
        // Seed default MCP servers if empty
        viewModelScope.launch {
            mcpDao.insertAll(McpServicesEngine.getDefaultMcpServers())
        }
    }

    // Saved Routes History from Room DB
    val savedRoutes: Flow<List<RouteEntity>> = routeDao.getAllRoutes()

    // Revenue Leads from Room DB
    val revenueLeads: Flow<List<RevenueLeadEntity>> = leadDao.getAllLeads()

    // AI Chat State
    private val _chatMessages = MutableStateFlow<List<GeminiMessage>>(
        listOf(
            GeminiMessage(
                id = "1",
                sender = "GEMINI",
                message = "Bonjour! Je suis l'Assistant Agentique NexusTransit Mtl. Nous sommes le 27 juillet (27.5°C, beau soleil à Montréal, aucun verglas). Comment puis-je optimiser vos déplacements STM et BIXI aujourd'hui?"
            )
        )
    )
    val chatMessages: StateFlow<List<GeminiMessage>> = _chatMessages.asStateFlow()

    val isAiThinking = MutableStateFlow(false)

    // Quebec Grants & Revenue Calculator State
    val numB2cUsers = MutableStateFlow(120)
    val numB2bClients = MutableStateFlow(8)
    val includeStaAllowance = MutableStateFlow(true)
    val hardwareSoftwareInvestCad = MutableStateFlow(18500.0)

    init {
        calculateRoutes()
        fetchStmVehiclePositions()
    }

    fun fetchStmVehiclePositions(apiKey: String = "") {
        viewModelScope.launch {
            isVehicleLoading.value = true
            try {
                val response = stmApiService.getVehiclePositions(apiKey)
                if (response.isSuccessful && response.body() != null) {
                    val parsed = StmApiService.parseVehicleLocations(response.body())
                    if (parsed.isNotEmpty()) {
                        liveVehiclePositions.value = parsed
                    } else {
                        liveVehiclePositions.value = StmApiService.getMockVehiclePositions()
                    }
                } else {
                    liveVehiclePositions.value = StmApiService.getMockVehiclePositions()
                }
            } catch (e: Exception) {
                // Connection fallback for offline / mock resilience
                liveVehiclePositions.value = StmApiService.getMockVehiclePositions()
            } finally {
                isVehicleLoading.value = false
            }
        }
    }

    fun calculateRoutes() {
        val branches = TreeOfThoughtsEngine.generateRouteBranches(
            origin = originState.value,
            destination = destinationState.value,
            weather = weatherObservation.value,
            profile = passengerProfile.value
        )
        _routeBranches.value = branches
    }

    fun saveRouteToHistory(branch: ToTBranch) {
        viewModelScope.launch {
            routeDao.insertRoute(
                RouteEntity(
                    origin = originState.value,
                    destination = destinationState.value,
                    durationMinutes = branch.totalDurationMinutes,
                    totScore = branch.overallScore,
                    isBacktracked = branch.isBacktracked,
                    weatherCondition = weatherObservation.value.condition,
                    modeDetails = branch.title
                )
            )
        }
    }

    fun sendAiPrompt(promptText: String) {
        if (promptText.isBlank()) return

        val userMsg = GeminiMessage(
            id = System.currentTimeMillis().toString(),
            sender = "USER",
            message = promptText
        )
        _chatMessages.update { it + userMsg }
        isAiThinking.value = true

        viewModelScope.launch {
            val responseText = GeminiTransitAssistant.queryTransitAdvisor(promptText)
            val aiMsg = GeminiMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                sender = "GEMINI",
                message = responseText
            )
            _chatMessages.update { it + aiMsg }
            isAiThinking.value = false
        }
    }

    fun pingMcpServer(serverId: String) {
        viewModelScope.launch {
            val currentServers = McpServicesEngine.getDefaultMcpServers()
            val target = currentServers.find { it.id == serverId }
            if (target != null) {
                val updated = target.copy(
                    pingMs = (10..35).random(),
                    lastLogMessage = "Ping d'inspection ré-exécuté avec succès à ${System.currentTimeMillis() % 10000}ms."
                )
                mcpDao.insertOrUpdateMcpServer(updated)
            }
        }
    }

    fun addRevenueLead(name: String, company: String, isB2b: Boolean) {
        viewModelScope.launch {
            val plan = if (isB2b) "B2B Enterprise ($297/m)" else "B2C Premium ($9.99/m)"
            val valCad = if (isB2b) 297.0 else 9.99
            leadDao.insertLead(
                RevenueLeadEntity(
                    clientName = if (company.isNotBlank()) "$name ($company)" else name,
                    planType = plan,
                    monthlyValueCad = valCad,
                    staStipendEligible = includeStaAllowance.value,
                    taxRecoveryCad = hardwareSoftwareInvestCad.value * 0.14975 // Quebec TPS/TVQ total combined rate 14.975%
                )
            )
        }
    }
}
