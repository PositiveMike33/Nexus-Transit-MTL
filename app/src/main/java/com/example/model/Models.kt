package com.example.model

enum class TransitType {
    METRO_GREEN, METRO_ORANGE, METRO_BLUE, METRO_YELLOW, BUS, REM, BIXI, WALK
}

data class TransitNode(
    val name: String,
    val type: TransitType,
    val lineCode: String,
    val durationMinutes: Int,
    val occupancyRate: Int, // 0 - 100%
    val delayMinutes: Int = 0,
    val isIncidentActive: Boolean = false,
    val incidentDescription: String = ""
)

data class ToTBranch(
    val id: String,
    val title: String,
    val nodes: List<TransitNode>,
    val overallScore: Float, // 0.0 to 1.0
    val isBacktracked: Boolean = false,
    val backtrackingReason: String = "",
    val totalDurationMinutes: Int,
    val skyFiRiskLevel: String = "FAIBLE" // FAIBLE, MODERE, ELEVE, CRITIQUE
)

data class WeatherObservation(
    val condition: String,
    val pavementTempCelsius: Float,
    val snowClearanceIndex: Int, // 0-100%
    val windChillCelsius: Float,
    val iceAlertLevel: String,
    val skyFiImageryAgeMinutes: Int
)

data class PiecesPassengerProfile(
    val contextMonths: Int = 9,
    val preferenceMobility: String = "Transit Express & pistes BIXI fluides",
    val maxWalkMinutes: Int = 12,
    val favoriteHubs: List<String> = listOf("Berri-UQAM", "Station Bonaventure", "Guy-Concordia"),
    val heatColdSensitivity: String = "Préférence pour trajets métropolitains fluides et stations BIXI ombragées"
)

data class GeminiMessage(
    val id: String,
    val sender: String, // USER or GEMINI
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
