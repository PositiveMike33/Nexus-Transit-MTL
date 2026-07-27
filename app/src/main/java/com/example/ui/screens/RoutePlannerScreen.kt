package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.RouteEntity
import com.example.data.remote.StmVehicleLocation
import com.example.model.ToTBranch
import com.example.model.TransitType
import com.example.model.WeatherObservation
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun RoutePlannerScreen(viewModel: MainViewModel) {
    val origin by viewModel.originState.collectAsState()
    val destination by viewModel.destinationState.collectAsState()
    val weather by viewModel.weatherObservation.collectAsState()
    val branches by viewModel.routeBranches.collectAsState()
    val savedRoutes by viewModel.savedRoutes.collectAsState(initial = emptyList())
    val vehicles by viewModel.liveVehiclePositions.collectAsState()
    val isVehicleLoading by viewModel.isVehicleLoading.collectAsState()

    val montrealHubs = listOf(
        "Berri-UQAM", "Station Bonaventure", "Guy-Concordia",
        "McGill", "Pie-IX", "Longueuil", "Côte-des-Neiges", "Snowdon"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Image Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner_1785135641512),
                        contentDescription = "Bannière NexusTransit Montréal",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryCyan.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsTransit,
                                        contentDescription = null,
                                        tint = PrimaryCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "NexusTransit Montréal",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ToT Reasoning • SkyFi Telemetry • MCP STM",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondaryLight)
                            )
                        }
                    }
                }
            }
        }

        // ToT Tree of Thoughts Reasoning Gauge Card (Immersive UI Feature)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ImmersiveGlassBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TREE OF THOUGHTS REASONING",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 1.sp
                        )
                        Surface(
                            color = StmGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, StmGreen.copy(alpha = 0.25f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(StmGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ToT Logic Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StmGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "0.94",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "Predictability Score",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { 0.94f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = PrimaryCyan,
                        trackColor = ImmersiveCardBg,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("NODE_ID: 0x8FA2", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text("THRESHOLD: 0.85", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                    }
                }
            }
        }

        // SkyFi Weather Alert & Micro-Climate Tile
        item {
            WeatherRadarCard(weather = weather)
        }

        // Origin / Destination Selectors
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ImmersiveGlassBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Point de départ & Destination",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Départ:", style = MaterialTheme.typography.labelMedium, color = TextSecondaryLight)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(montrealHubs) { hub ->
                            FilterChip(
                                selected = origin == hub,
                                onClick = {
                                    viewModel.originState.value = hub
                                    viewModel.calculateRoutes()
                                },
                                label = { Text(hub) },
                                modifier = Modifier.testTag("chip_origin_$hub")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Destination:", style = MaterialTheme.typography.labelMedium, color = TextSecondaryLight)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(montrealHubs) { hub ->
                            FilterChip(
                                selected = destination == hub,
                                onClick = {
                                    viewModel.destinationState.value = hub
                                    viewModel.calculateRoutes()
                                },
                                label = { Text(hub) },
                                modifier = Modifier.testTag("chip_dest_$hub")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.calculateRoutes() },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = ImmersiveBg),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_recalculate_tot")
                    ) {
                        Icon(Icons.Default.AccountTree, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simuler l'Arbre de Résonance ToT", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Live GTFS-RT STM Vehicle Telemetry Card
        item {
            StmRealtimeVehicleCard(
                vehicles = vehicles,
                isLoading = isVehicleLoading,
                onRefresh = { viewModel.fetchStmVehiclePositions() }
            )
        }

        // ToT Branches List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trajectoires ToT Évaluées (>0.85 Seuil)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${branches.size} options",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondaryLight
                )
            }
        }

        items(branches) { branch ->
            ToTBranchCard(
                branch = branch,
                onSave = { viewModel.saveRouteToHistory(branch) }
            )
        }

        // Saved Routes History
        if (savedRoutes.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Historique des Trajectoires Enregistrées (Room DB)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryCyan
                )
            }

            items(savedRoutes) { route ->
                SavedRouteCard(route = route)
            }
        }
    }
}

@Composable
fun WeatherRadarCard(weather: WeatherObservation) {
    val isSummer = weather.pavementTempCelsius > 10
    val isIceRisk = weather.iceAlertLevel == "ÉLEVÉ"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isIceRisk) Color(0xFF2C1518) else ImmersiveSurface
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isIceRisk) DangerRed.copy(alpha = 0.5f) else ImmersiveGlassBorder,
                RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isIceRisk) DangerRed.copy(alpha = 0.2f) else WarmGold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSummer) Icons.Default.WbSunny else Icons.Default.AcUnit,
                    contentDescription = null,
                    tint = if (isIceRisk) DangerRed else WarmGold,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isSummer) "SkyFi Satellite: MÉTÉO ÉTÉ 27 JUILLET" else "SkyFi Satellite: ${weather.iceAlertLevel} VERGLAS",
                        fontWeight = FontWeight.Bold,
                        color = if (isIceRisk) DangerRed else WarmGold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "(${weather.skyFiImageryAgeMinutes}m ago)",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = weather.condition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Chaussée: ${weather.pavementTempCelsius}°C",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryLight
                    )
                    Text(
                        text = if (isSummer) "Réseau BIXI & Voies: ${weather.snowClearanceIndex}%" else "Déneigement: ${weather.snowClearanceIndex}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = StmGreen
                    )
                }
            }
        }
    }
}

@Composable
fun ToTBranchCard(branch: ToTBranch, onSave: () -> Unit) {
    val isScoreValid = branch.overallScore >= 0.85f

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (branch.isBacktracked) Color(0xFF281416) else ImmersiveSurface
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (branch.isBacktracked) DangerRed.copy(alpha = 0.5f) else ImmersiveGlassBorder,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = branch.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = if (branch.isBacktracked) DangerRed.copy(alpha = 0.2f) else if (isScoreValid) StmGreen.copy(alpha = 0.15f) else WarningYellow.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (branch.isBacktracked) DangerRed else if (isScoreValid) StmGreen else WarningYellow
                    )
                ) {
                    Text(
                        text = "Score: ${String.format("%.2f", branch.overallScore)}",
                        color = if (branch.isBacktracked) DangerRed else if (isScoreValid) StmGreen else WarningYellow,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (branch.isBacktracked) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DangerRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BACKTRACKING TOT: ${branch.backtrackingReason}",
                        style = MaterialTheme.typography.bodySmall,
                        color = DangerRed,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = TextSecondaryLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${branch.totalDurationMinutes} min estimées",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (branch.skyFiRiskLevel == "ÉLEVÉ") DangerRed else StmGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Risque: ${branch.skyFiRiskLevel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (branch.skyFiRiskLevel == "ÉLEVÉ") DangerRed else StmGreen
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = CyberNavySurface)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Étapes ToT:", style = MaterialTheme.typography.labelSmall, color = TextSecondaryLight)
            Spacer(modifier = Modifier.height(4.dp))

            branch.nodes.forEach { node ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getTransitIcon(node.type),
                        contentDescription = null,
                        tint = getTransitColor(node.type),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = node.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        if (node.isIncidentActive) {
                            Text(
                                text = "⚠️ ${node.incidentDescription}",
                                style = MaterialTheme.typography.labelSmall,
                                color = DangerRed
                            )
                        }
                    }
                    Text(
                        text = "${node.durationMinutes}m",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            IconButton(
                onClick = onSave,
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("btn_save_route_${branch.id}")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bookmark, contentDescription = "Enregistrer", tint = PrimaryCyan)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sauvegarder", color = PrimaryCyan, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun SavedRouteCard(route: RouteEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CyberNavySurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${route.origin} ➔ ${route.destination}",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${route.modeDetails} • ${route.durationMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )
            }

            Surface(
                color = PrimaryCyan.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Score: ${String.format("%.2f", route.totScore)}",
                    color = PrimaryCyan,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

fun getTransitIcon(type: TransitType) = when (type) {
    TransitType.METRO_GREEN, TransitType.METRO_ORANGE, TransitType.METRO_BLUE, TransitType.METRO_YELLOW -> Icons.Default.Subway
    TransitType.BUS -> Icons.Default.DirectionsBus
    TransitType.REM -> Icons.Default.Train
    TransitType.BIXI -> Icons.Default.PedalBike
    TransitType.WALK -> Icons.Default.DirectionsWalk
}

fun getTransitColor(type: TransitType) = when (type) {
    TransitType.METRO_GREEN -> Color(0xFF00A859)
    TransitType.METRO_ORANGE -> Color(0xFFEF7C00)
    TransitType.METRO_BLUE -> Color(0xFF00A0DC)
    TransitType.METRO_YELLOW -> Color(0xFFFFD100)
    TransitType.BUS -> SecondaryOrangeStm
    TransitType.REM -> PrimaryCyan
    TransitType.BIXI -> StmGreen
    TransitType.WALK -> TextSecondaryLight
}

@Composable
fun StmRealtimeVehicleCard(
    vehicles: List<StmVehicleLocation>,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ImmersiveGlassBorder, RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StmBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = StmBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "GTFS-RT STM Telemetry",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Retrofit API • ${vehicles.size} véhicules suivis",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier.testTag("btn_refresh_gtfs")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = StmBlue,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rafraîchir GTFS-RT",
                            tint = StmBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (vehicles.isEmpty()) {
                Text(
                    text = "Recherche de flux GTFS-RT...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(vehicles) { vehicle ->
                        Surface(
                            color = ImmersiveCardBg,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveGlassBorder),
                            modifier = Modifier.width(220.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = vehicle.routeId,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = WarmGold
                                    )
                                    Surface(
                                        color = StmGreen.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${String.format("%.1f", vehicle.speedKmh)} km/h",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = StmGreen,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "ID: ${vehicle.vehicleId}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryLight
                                )
                                Text(
                                    text = "Pos: ${String.format("%.4f", vehicle.latitude)}, ${String.format("%.4f", vehicle.longitude)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(StmGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = vehicle.currentStatus,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondaryLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

