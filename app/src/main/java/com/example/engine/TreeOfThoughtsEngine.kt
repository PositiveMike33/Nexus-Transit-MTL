package com.example.engine

import com.example.model.*

object TreeOfThoughtsEngine {

    fun generateRouteBranches(
        origin: String,
        destination: String,
        weather: WeatherObservation,
        profile: PiecesPassengerProfile
    ): List<ToTBranch> {
        val branches = mutableListOf<ToTBranch>()

        // Branch 1: Métro Express + Bus local
        val branch1Nodes = listOf(
            TransitNode("Départ: $origin", TransitType.WALK, "PIED", 4, 15),
            TransitNode("Ligne Orange (Bonaventure -> Berri-UQAM)", TransitType.METRO_ORANGE, "ORANGE", 8, 65),
            TransitNode("Ligne Verte (Berri-UQAM -> $destination)", TransitType.METRO_GREEN, "VERTE", 10, 45),
            TransitNode("Arrivée: $destination", TransitType.WALK, "PIED", 3, 10)
        )
        val score1 = calculateBranchScore(branch1Nodes, weather, profile)
        branches.add(
            ToTBranch(
                id = "branch_1",
                title = "Trajectoire Alpha: Métro Réseau Souterrain (Sécurisé)",
                nodes = branch1Nodes,
                overallScore = score1,
                isBacktracked = false,
                totalDurationMinutes = branch1Nodes.sumOf { it.durationMinutes },
                skyFiRiskLevel = if (weather.pavementTempCelsius < -2) "MODÉRÉ" else "FAIBLE"
            )
        )

        // Branch 2: Bus Express (i3 API + GTFS-RT)
        val busDelay = if (weather.pavementTempCelsius < -4) 14 else 2
        val isIceCondition = weather.pavementTempCelsius < 0 || weather.iceAlertLevel == "ÉLEVÉ"
        val incidentDesc = if (isIceCondition) "Ralentissement verglas St-Laurent / i3 STM Alert" else "Circulation fluide / GTFS-RT STM Temps Réel"
        val branch2Nodes = listOf(
            TransitNode("Départ: $origin", TransitType.WALK, "PIED", 5, 20),
            TransitNode("STM Bus 55 St-Laurent Express", TransitType.BUS, "BUS 55", 22 + busDelay, 65, delayMinutes = busDelay, isIncidentActive = busDelay > 8, incidentDescription = incidentDesc),
            TransitNode("Arrivée: $destination", TransitType.WALK, "PIED", 4, 10)
        )
        val score2 = calculateBranchScore(branch2Nodes, weather, profile)
        val isBacktracked2 = score2 < 0.85f
        branches.add(
            ToTBranch(
                id = "branch_2",
                title = "Trajectoire Beta: Bus 55 St-Laurent Direct",
                nodes = branch2Nodes,
                overallScore = score2,
                isBacktracked = isBacktracked2,
                backtrackingReason = if (isBacktracked2) "Score $score2 < 0.85. Backtracking au nœud Berri-UQAM (Retard Bus + Perturbation $busDelay min)." else "",
                totalDurationMinutes = branch2Nodes.sumOf { it.durationMinutes },
                skyFiRiskLevel = if (isIceCondition) "ÉLEVÉ" else "FAIBLE"
            )
        )

        // Branch 3: REM + BIXI / Marche Rapide
        val branch3Nodes = listOf(
            TransitNode("Départ: $origin", TransitType.WALK, "PIED", 3, 10),
            TransitNode("REM Gare Centrale -> McGill", TransitType.REM, "REM", 6, 40),
            TransitNode("Ligne Verte (McGill -> $destination)", TransitType.METRO_GREEN, "VERTE", 7, 50),
            TransitNode("Arrivée: $destination", TransitType.WALK, "PIED", 2, 5)
        )
        val score3 = calculateBranchScore(branch3Nodes, weather, profile)
        branches.add(
            ToTBranch(
                id = "branch_3",
                title = "Trajectoire Gamma: Hybride REM + Métro Verte",
                nodes = branch3Nodes,
                overallScore = score3,
                isBacktracked = false,
                totalDurationMinutes = branch3Nodes.sumOf { it.durationMinutes },
                skyFiRiskLevel = "FAIBLE"
            )
        )

        return branches.sortedByDescending { it.overallScore }
    }

    private fun calculateBranchScore(
        nodes: List<TransitNode>,
        weather: WeatherObservation,
        profile: PiecesPassengerProfile
    ): Float {
        var baseScore = 0.95f

        // Penalize for bus delay & incidents
        nodes.forEach { node ->
            if (node.isIncidentActive) baseScore -= 0.18f
            if (node.delayMinutes > 5) baseScore -= (node.delayMinutes * 0.015f)
        }

        // Penalize if walk time exceeds profile limits on ice
        val totalWalk = nodes.filter { it.type == TransitType.WALK }.sumOf { it.durationMinutes }
        if (totalWalk > profile.maxWalkMinutes && weather.pavementTempCelsius < 0) {
            baseScore -= 0.12f
        }

        // Weather adjustment
        if (weather.iceAlertLevel == "ÉLEVÉ" && nodes.any { it.type == TransitType.BUS }) {
            baseScore -= 0.10f
        }

        return baseScore.coerceIn(0.10f, 0.99f)
    }
}
