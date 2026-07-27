package com.example.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.McpToolRegistry
import com.example.ui.theme.*
import com.example.utils.DateUtils
import com.example.viewmodel.MainViewModel
import java.util.Locale

/**
 * SandDashboard.kt - Executive 7-Figure Enterprise Analytics & Recharts-style Visualizer.
 *
 * Visualise les métriques de performance clés (KPIs), les télémétries de transport STM GTFS-RT en temps réel,
 * les télé-observations satellites SkyFi, la mémoire Pieces LTM et l'optimisation des jetons MCP (78-95% de réduction).
 */
@Composable
fun SandDashboard(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val liveVehicles by viewModel.liveVehiclePositions.collectAsState()
    val isVehiclesLoading by viewModel.isVehicleLoading.collectAsState()
    val weatherObs by viewModel.weatherObservation.collectAsState()

    val tokenMetrics = remember { McpToolRegistry.calculateOptimizationMetrics() }
    val toolGroups = remember { McpToolRegistry.getToolGroups() }

    var selectedChartTab by remember { mutableStateOf(ChartTab.TOKEN_REDUCTION) }
    var selectedTimeframe by remember { mutableStateOf("Journée (27 Juil 2026)") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ImmersiveBg)
            .padding(16.dp)
            .testTag("sand_dashboard_container"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- HEADER SECTION ---
        item {
            ExecutiveDashboardHeader(
                referenceDate = DateUtils.getPredictiveEngineContextDate(),
                onRefreshVehicles = { viewModel.fetchStmVehiclePositions() }
            )
        }

        // --- KPI METRICS GRID ---
        item {
            KpiExecutiveGrid(
                tokenReductionPercent = tokenMetrics.reductionPercentage,
                activeVehiclesCount = liveVehicles.size,
                mcpServersCount = toolGroups.size,
                surfaceTemp = weatherObs.pavementTempCelsius
            )
        }

        // --- RECHARTS-STYLE INTERACTIVE CHART SECTION ---
        item {
            RechartsInteractiveContainer(
                selectedTab = selectedChartTab,
                onTabSelect = { selectedChartTab = it },
                selectedTimeframe = selectedTimeframe,
                onTimeframeSelect = { selectedTimeframe = it },
                tokenMetrics = tokenMetrics,
                liveVehiclesCount = liveVehicles.size
            )
        }

        // --- MCP TOOL GROUPING TOKEN FOOTPRINT COMPARISON ---
        item {
            TokenFootprintReductionCard(
                toolGroups = toolGroups,
                metrics = tokenMetrics
            )
        }

        // --- REALTIME STM TELEMETRY & SATELLITE WEATHER STATUS ---
        item {
            RealtimeTelemetryBoard(
                liveVehicles = liveVehicles,
                isLoading = isVehiclesLoading,
                weatherObs = weatherObs
            )
        }

        // --- 7-FIGURE ENTERPRISE FINANCIAL BREAKDOWN ---
        item {
            EnterpriseFinancialFrameworkCard(
                numB2c = viewModel.numB2cUsers.collectAsState().value,
                numB2b = viewModel.numB2bClients.collectAsState().value,
                includeSta = viewModel.includeStaAllowance.collectAsState().value
            )
        }
    }
}

enum class ChartTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    TOKEN_REDUCTION("Empreinte Jetons (-88%)", Icons.Default.Memory),
    STM_TELEMETRY("Véhicules STM Live", Icons.Default.DirectionsBus),
    REVENUE_RUNRATE("Projection ARR (CAD)", Icons.Default.TrendingUp)
}

@Composable
private fun ExecutiveDashboardHeader(
    referenceDate: String,
    onRefreshVehicles: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("executive_dashboard_header"),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveGlassBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(StmGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TABLEAU DE BORD EXÉCUTIF 7-FIGURES",
                        style = MaterialTheme.typography.labelMedium,
                        color = StmGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "NexusTransit • Frame Analytics & Recharts",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Horodatage d'Analyse : $referenceDate (Montréal, QC)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )
            }

            IconButton(
                onClick = onRefreshVehicles,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryCyan.copy(alpha = 0.15f))
                    .testTag("refresh_dashboard_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Rafraîchir les données",
                    tint = PrimaryCyan
                )
            }
        }
    }
}

@Composable
private fun KpiExecutiveGrid(
    tokenReductionPercent: Double,
    activeVehiclesCount: Int,
    mcpServersCount: Int,
    surfaceTemp: Float
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                modifier = Modifier.weight(1f),
                title = "Réduction Jetons MCP",
                value = "-${String.format(Locale.US, "%.1f", tokenReductionPercent)}%",
                subtitle = "Économie de 78 à 95%",
                icon = Icons.Default.Speed,
                accentColor = StmGreen,
                tag = "kpi_token_reduction"
            )

            KpiCard(
                modifier = Modifier.weight(1f),
                title = "Positions STM GTFS",
                value = "$activeVehiclesCount bus/métros",
                subtitle = "Sync direct 27 Juil 2026",
                icon = Icons.Default.DirectionsTransit,
                accentColor = PrimaryCyan,
                tag = "kpi_stm_vehicles"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                modifier = Modifier.weight(1f),
                title = "Serveurs MCP Actifs",
                value = "$mcpServersCount Groupes",
                subtitle = "Pieces, STM, SkyFi, Sales",
                icon = Icons.Default.Dns,
                accentColor = WarmGold,
                tag = "kpi_mcp_servers"
            )

            KpiCard(
                modifier = Modifier.weight(1f),
                title = "Télédétection SkyFi",
                value = "${surfaceTemp}°C Sol",
                subtitle = "Ciel clair • 0.5m Optique",
                icon = Icons.Default.WbSunny,
                accentColor = Color(0xFFF97316),
                tag = "kpi_skyfi_temp"
            )
        }
    }
}

@Composable
private fun KpiCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    tag: String
) {
    Card(
        modifier = modifier.testTag(tag),
        colors = CardDefaults.cardColors(containerColor = ImmersiveCardBg),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveGlassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(Locale.ROOT),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryLight
            )
        }
    }
}

@Composable
private fun RechartsInteractiveContainer(
    selectedTab: ChartTab,
    onTabSelect: (ChartTab) -> Unit,
    selectedTimeframe: String,
    onTimeframeSelect: (String) -> Unit,
    tokenMetrics: com.example.engine.ToolOptimizationMetrics,
    liveVehiclesCount: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recharts_interactive_container"),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveGlassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Visualiseur Recharts Enterprise",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    color = ImmersiveCardBg,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveGlassBorder)
                ) {
                    Text(
                        text = selectedTimeframe,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TAB SELECTOR
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ChartTab.values()) { tab ->
                    val isSelected = tab == selectedTab
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTabSelect(tab) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(tab.label, fontSize = 12.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCyan.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryCyan,
                            containerColor = ImmersiveCardBg,
                            labelColor = TextSecondaryLight
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) PrimaryCyan else ImmersiveGlassBorder
                        ),
                        modifier = Modifier.testTag("chart_tab_${tab.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CANVAS RECHARTS RENDERER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ImmersiveCardBg)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                when (selectedTab) {
                    ChartTab.TOKEN_REDUCTION -> RechartsBarChart(
                        rawTokens = tokenMetrics.totalRawTokens,
                        groupedTokens = tokenMetrics.totalGroupedTokens
                    )
                    ChartTab.STM_TELEMETRY -> RechartsAreaChart(
                        activeVehicles = liveVehiclesCount
                    )
                    ChartTab.REVENUE_RUNRATE -> RechartsLineChart()
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LEGEND & METRICS FOOTER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = PrimaryCyan, label = "Charge Initiale (Raw Tokens)")
                LegendItem(color = StmGreen, label = "Optimisé MCP (-88%)")
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryLight
        )
    }
}

@Composable
private fun RechartsBarChart(rawTokens: Int, groupedTokens: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val maxVal = maxOf(rawTokens, 3500).toFloat()
        val barWidth = width / 8f

        // Draw background grid lines
        for (i in 1..4) {
            val y = height * (i / 5f)
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        // Raw Tokens Bar (Before Grouping)
        val rawHeight = (rawTokens / maxVal) * (height - 30f)
        val rawX = width * 0.25f - barWidth / 2f
        drawRoundRect(
            color = PrimaryCyan.copy(alpha = 0.85f),
            topLeft = Offset(rawX, height - rawHeight),
            size = Size(barWidth, rawHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )

        // Grouped Tokens Bar (After Grouping)
        val groupedHeight = (groupedTokens / maxVal) * (height - 30f)
        val groupedX = width * 0.75f - barWidth / 2f
        drawRoundRect(
            color = StmGreen,
            topLeft = Offset(groupedX, height - groupedHeight),
            size = Size(barWidth, groupedHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )
    }
}

@Composable
private fun RechartsAreaChart(activeVehicles: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val points = listOf(
            Offset(0f, height * 0.7f),
            Offset(width * 0.2f, height * 0.5f),
            Offset(width * 0.4f, height * 0.35f),
            Offset(width * 0.6f, height * 0.4f),
            Offset(width * 0.8f, height * 0.2f),
            Offset(width, height * (0.25f - (activeVehicles.coerceIn(0, 50) * 0.003f)))
        )

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(PrimaryCyan.copy(alpha = 0.35f), Color.Transparent)
            )
        )

        drawPath(
            path = path,
            color = PrimaryCyan,
            style = Stroke(width = 4f, cap = StrokeCap.Round)
        )

        points.forEach { point ->
            drawCircle(color = Color.White, radius = 5f, center = point)
            drawCircle(color = PrimaryCyan, radius = 3f, center = point)
        }
    }
}

@Composable
private fun RechartsLineChart() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val arrPoints = listOf(
            Offset(0f, height * 0.85f),
            Offset(width * 0.25f, height * 0.65f),
            Offset(width * 0.5f, height * 0.45f),
            Offset(width * 0.75f, height * 0.25f),
            Offset(width, height * 0.1f)
        )

        val path = Path().apply {
            moveTo(arrPoints.first().x, arrPoints.first().y)
            for (i in 1 until arrPoints.size) {
                lineTo(arrPoints[i].x, arrPoints[i].y)
            }
        }

        drawPath(
            path = path,
            color = WarmGold,
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )

        arrPoints.forEach { point ->
            drawCircle(color = WarmGold, radius = 6f, center = point)
        }
    }
}

@Composable
private fun TokenFootprintReductionCard(
    toolGroups: List<com.example.engine.McpToolGroup>,
    metrics: com.example.engine.ToolOptimizationMetrics
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("token_reduction_card"),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveGlassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = StmGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Regroupement d'Outils MCP (Token Grouping)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    color = StmGreen.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "-${metrics.reductionPercentage}% Tokens",
                        style = MaterialTheme.typography.labelMedium,
                        color = StmGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                toolGroups.forEach { group ->
                    ToolGroupReductionProgressRow(group = group)
                }
            }
        }
    }
}

@Composable
private fun ToolGroupReductionProgressRow(group: com.example.engine.McpToolGroup) {
    val progressAnim by animateFloatAsState(
        targetValue = (group.tokenReductionPercent / 100f).coerceIn(0f, 1f),
        animationSpec = tween(1000)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = group.serviceName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${group.totalRawTokenCount} -> ${group.groupedTokenCount} tokens (-${group.tokenReductionPercent}%)",
                style = MaterialTheme.typography.labelSmall,
                color = StmGreen
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progressAnim },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = StmGreen,
            trackColor = ImmersiveCardBg
        )
    }
}

@Composable
private fun RealtimeTelemetryBoard(
    liveVehicles: List<com.example.data.remote.StmVehicleLocation>,
    isLoading: Boolean,
    weatherObs: com.example.model.WeatherObservation
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("realtime_telemetry_board"),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveGlassBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SatelliteAlt,
                        contentDescription = null,
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Télémétrie STM GTFS-RT & Métropolitaine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = PrimaryCyan,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "${liveVehicles.size} Unités Géolocalisées",
                        style = MaterialTheme.typography.labelSmall,
                        color = StmGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = ImmersiveCardBg,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Conditions Satellite SkyFi (27 Juillet 2026)",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${weatherObs.condition} • Température Chaussée : ${weatherObs.pavementTempCelsius}°C • Statut Verglas : ${weatherObs.iceAlertLevel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight
                    )
                }
            }
        }
    }
}

@Composable
private fun EnterpriseFinancialFrameworkCard(
    numB2c: Int,
    numB2b: Int,
    includeSta: Boolean
) {
    val b2cRevenue = numB2c * 15.0
    val b2bRevenue = numB2b * 497.0
    val staAllowance = if (includeSta) 2000.0 else 0.0
    val totalMrr = b2cRevenue + b2bRevenue + staAllowance
    val totalArr = totalMrr * 12.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("enterprise_financial_framework_card"),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarmGold.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = WarmGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Framework Financier 7-Figures (ROI & Subventions STA)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Revenu Mensuel Récurrent (MRR)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = "${String.format(Locale.US, "%,.2f", totalMrr)} $ CAD",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = WarmGold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Run-Rate Annuel (ARR)",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                    Text(
                        text = "${String.format(Locale.US, "%,.2f", totalArr)} $ CAD",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = StmGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "💡 Inclut l'optimisation fiscale CTI/RTI 100% (TPS/TVQ) et les allocations Emploi-Québec STA (500$/semaine).",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryLight
            )
        }
    }
}
