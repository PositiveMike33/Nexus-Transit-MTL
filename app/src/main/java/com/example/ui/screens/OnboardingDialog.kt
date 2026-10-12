package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserTransitPreferences
import com.example.ui.theme.*

/**
 * Diaporama interactif d'onboarding guidé pour NexusTransit Mtl.
 * Présente le moteur agentique, explique le fonctionnement des modules MCP (STM, SkyFi, Pieces)
 * et recueille les préférences initiales de l'utilisateur avec persistance locale.
 */
@Composable
fun OnboardingDialog(
    initialPreferences: UserTransitPreferences,
    onDismiss: () -> Unit,
    onComplete: (UserTransitPreferences) -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 4

    // États de préférences éditables
    var selectedMode by remember { mutableStateOf(initialPreferences.preferredTransitMode) }
    var favoriteLines by remember { mutableStateOf(initialPreferences.favoriteLines.toMutableSet()) }
    var selectedDelayTolerance by remember { mutableIntStateOf(initialPreferences.delayToleranceMinutes) }
    var homeStation by remember { mutableStateOf(initialPreferences.homeStation) }
    var workStation by remember { mutableStateOf(initialPreferences.workStation) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("onboarding_dialog_container"),
            shape = RoundedCornerShape(28.dp),
            color = ImmersiveBg,
            border = BorderStroke(1.dp, ImmersiveGlassBorder),
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Barre d'en-tête de progression
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = StmBlue.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsTransit,
                                    contentDescription = "NexusTransit Logo",
                                    tint = StmBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NexusTransit Mtl",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Étape ${currentStep + 1} sur $totalSteps",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted
                            )
                        }
                    }

                    // Bouton Fermer / Ignorer
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("onboarding_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer l'accueil",
                            tint = TextSecondaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Indicateurs de progression (Steps Dots)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until totalSteps) {
                        val isActive = i == currentStep
                        val isDone = i < currentStep
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(if (isActive) 28.dp else 12.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(
                                    when {
                                        isActive -> StmBlue
                                        isDone -> StmGreen
                                        else -> Color.White.copy(alpha = 0.2f)
                                    }
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contenu dynamique selon l'étape
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    when (currentStep) {
                        0 -> IntroStep()
                        1 -> McpSystemsStep()
                        2 -> PreferencesStep(
                            selectedMode = selectedMode,
                            onModeSelected = { selectedMode = it },
                            favoriteLines = favoriteLines,
                            onToggleLine = { line ->
                                val updated = favoriteLines.toMutableSet()
                                if (updated.contains(line)) {
                                    if (updated.size > 1) updated.remove(line)
                                } else {
                                    updated.add(line)
                                }
                                favoriteLines = updated
                            },
                            delayTolerance = selectedDelayTolerance,
                            onDelayToleranceChange = { selectedDelayTolerance = it },
                            homeStation = homeStation,
                            onHomeStationChange = { homeStation = it },
                            workStation = workStation,
                            onWorkStationChange = { workStation = it }
                        )
                        3 -> SummaryReadyStep(
                            mode = selectedMode,
                            lines = favoriteLines,
                            delayTolerance = selectedDelayTolerance,
                            homeStation = homeStation,
                            workStation = workStation
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Barre de navigation inférieure
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, ImmersiveGlassBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimaryLight),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("onboarding_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Précédent", fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("onboarding_skip_button")
                        ) {
                            Text("Passer l'intro", color = TextMuted)
                        }
                    }

                    if (currentStep < totalSteps - 1) {
                        Button(
                            onClick = { currentStep++ },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StmBlue),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("onboarding_next_button")
                        ) {
                            Text("Suivant", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                val finalized = UserTransitPreferences(
                                    hasCompletedOnboarding = true,
                                    preferredTransitMode = selectedMode,
                                    favoriteLines = favoriteLines,
                                    delayToleranceMinutes = selectedDelayTolerance,
                                    homeStation = homeStation,
                                    workStation = workStation
                                )
                                onComplete(finalized)
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = StmGreen),
                            modifier = Modifier
                                .height(50.dp)
                                .testTag("onboarding_complete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Activer NexusTransit", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ----------------- ÉTAPE 0 : PRÉSENTATION DE L'APPLICATION -----------------

@Composable
private fun IntroStep() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            color = StmBlue.copy(alpha = 0.15f),
            border = BorderStroke(1.5.dp, StmBlue.copy(alpha = 0.4f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = StmBlue,
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Bienvenue sur NexusTransit",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Le moteur prédictif et agentique de transport en commun pour la métropole montréalaise.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryLight,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Cartes des fonctionnalités majeures
        FeatureItemCard(
            icon = Icons.Default.AltRoute,
            iconTint = StmBlue,
            title = "Arborescence Tree of Thoughts (ToT)",
            description = "Explore simultanément les options métro, bus et intermodales pour retenir la branche au score optimal."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureItemCard(
            icon = Icons.Default.CloudSync,
            iconTint = StmGreen,
            title = "Résilience Hors-Ligne & Cache Room",
            description = "Bascule automatique et silencieuse sur les données locales en cas d'instabilité du réseau STM."
        )

        Spacer(modifier = Modifier.height(12.dp))

        FeatureItemCard(
            icon = Icons.Default.Psychology,
            iconTint = WarmGold,
            title = "Copilote IA Montréalais",
            description = "Assistance proactive prenant en compte la météo, le verglas et les événements locaux de Montréal."
        )
    }
}

// ----------------- ÉTAPE 1 : LES MODULES MCP -----------------

@Composable
private fun McpSystemsStep() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Moteur d'Orchestration MCP",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "NexusTransit s'appuie sur trois serveurs d'intelligence contextuelle décentralisés (Model Context Protocol) :",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryLight,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        McpSystemDetailCard(
            badge = "MCP #1",
            title = "STM GTFS-RT (Transport Temps Réel)",
            status = "ACTIF • 10 req/s",
            statusColor = StmGreen,
            icon = Icons.Default.DirectionsBus,
            description = "Surveille les positions exactes des autobus et rames de métro, synchronise les retards et garantit un repli instantané sur la base locale.",
            metric = "Flux officiel Société de transport de Montréal"
        )

        Spacer(modifier = Modifier.height(12.dp))

        McpSystemDetailCard(
            badge = "MCP #2",
            title = "SkyFi Satellite Vision",
            status = "ORBITE • 500km",
            statusColor = StmBlueLight,
            icon = Icons.Default.SatelliteAlt,
            description = "Capte les densités de congestion urbaine depuis l'orbite terrestre pour anticiper les ralentissements de surface avant les capteurs routiers.",
            metric = "Résolution optique 30-50 cm / pixel"
        )

        Spacer(modifier = Modifier.height(12.dp))

        McpSystemDetailCard(
            badge = "MCP #3",
            title = "Pieces Long-Term Memory",
            status = "CONTEXTE PERSISTANT",
            statusColor = WarmGold,
            icon = Icons.Default.Memory,
            description = "Enregistre votre profil de voyageur, retient vos lignes fétiches et affine les recommandations de trajet au fil de vos déplacements.",
            metric = "Mémoire sémantique vectorielle locale"
        )
    }
}

// ----------------- ÉTAPE 2 : RECUEIL DES PRÉFÉRENCES -----------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreferencesStep(
    selectedMode: String,
    onModeSelected: (String) -> Unit,
    favoriteLines: Set<String>,
    onToggleLine: (String) -> Unit,
    delayTolerance: Int,
    onDelayToleranceChange: (Int) -> Unit,
    homeStation: String,
    onHomeStationChange: (String) -> Unit,
    workStation: String,
    onWorkStationChange: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val availableModes = listOf(
        "MULTIMODAL" to "Mixte (Métro + Bus)",
        "METRO" to "Métro prioritaire",
        "BUS" to "Lignes Express Bus"
    )

    val commonMontrealLines = listOf(
        "Ligne 1 - Verte",
        "Ligne 2 - Orange",
        "Ligne 4 - Jaune",
        "Ligne 5 - Bleue",
        "Bus 24 Sherbrooke",
        "Bus 55 Saint-Laurent",
        "Bus 80 du Parc",
        "Bus 105 Sherbrooke Ouest"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "Personnalisez votre expérience",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = "Ces préférences sont sauvegardées localement et synchronisées avec Pieces MCP.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted,
            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
        )

        // 1. Mode de transport préféré
        Text(
            text = "1. Mode de transport privilégié",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryLight
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            availableModes.forEach { (modeKey, label) ->
                val isSelected = selectedMode == modeKey
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onModeSelected(modeKey) }
                        .testTag("onboarding_mode_$modeKey"),
                    color = if (isSelected) StmBlue.copy(alpha = 0.25f) else ImmersiveCardBg,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) StmBlue else ImmersiveGlassBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(4.dp)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) StmBlueLight else TextSecondaryLight,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Lignes favorites
        Text(
            text = "2. Vos lignes habituelles à surveiller",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryLight
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            commonMontrealLines.forEach { line ->
                val isFav = favoriteLines.contains(line)
                FilterChip(
                    selected = isFav,
                    onClick = { onToggleLine(line) },
                    label = {
                        Text(
                            text = line,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isFav) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = StmBlue.copy(alpha = 0.25f),
                        selectedLabelColor = StmBlueLight,
                        containerColor = ImmersiveCardBg,
                        labelColor = TextSecondaryLight
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isFav) StmBlue else ImmersiveGlassBorder
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("onboarding_line_chip_${line.replace(" ", "_")}")
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. Stations usuelles
        Text(
            text = "3. Votre trajet récurrent",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryLight
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = homeStation,
                onValueChange = onHomeStationChange,
                label = { Text("Départ / Domicile", style = MaterialTheme.typography.labelSmall) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StmBlue,
                    unfocusedBorderColor = ImmersiveGlassBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = ImmersiveCardBg,
                    unfocusedContainerColor = ImmersiveCardBg
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("onboarding_input_home")
            )

            OutlinedTextField(
                value = workStation,
                onValueChange = onWorkStationChange,
                label = { Text("Arrivée / Travail", style = MaterialTheme.typography.labelSmall) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StmBlue,
                    unfocusedBorderColor = ImmersiveGlassBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = ImmersiveCardBg,
                    unfocusedContainerColor = ImmersiveCardBg
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("onboarding_input_work")
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. Tolérance aux retards
        Text(
            text = "4. Alerte retard si retard supérieur à :",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimaryLight
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(3, 5, 10).forEach { mins ->
                val isSelected = delayTolerance == mins
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onDelayToleranceChange(mins) }
                        .testTag("onboarding_delay_${mins}m"),
                    color = if (isSelected) WarmGold.copy(alpha = 0.2f) else ImmersiveCardBg,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) WarmGold else ImmersiveGlassBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "$mins minutes",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) WarmGold else TextSecondaryLight
                        )
                    }
                }
            }
        }
    }
}

// ----------------- ÉTAPE 3 : RÉSUMÉ & ACTIVATION -----------------

@Composable
private fun SummaryReadyStep(
    mode: String,
    lines: Set<String>,
    delayTolerance: Int,
    homeStation: String,
    workStation: String
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape),
            color = StmGreen.copy(alpha = 0.15f),
            border = BorderStroke(1.5.dp, StmGreen.copy(alpha = 0.4f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = StmGreen,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Tout est configuré !",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Votre profil de mobilité est prêt et enregistré dans la mémoire Pieces MCP.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Récapitulatif Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = ImmersiveCardBg,
            border = BorderStroke(1.dp, ImmersiveGlassBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SummaryRow(
                    label = "Mode privilégié",
                    value = when (mode) {
                        "METRO" -> "Métro uniquement"
                        "BUS" -> "Bus express"
                        else -> "Multimodal STM"
                    },
                    icon = Icons.Default.DirectionsTransit
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = ImmersiveGlassBorder)
                SummaryRow(
                    label = "Trajet habituel",
                    value = "$homeStation ➔ $workStation",
                    icon = Icons.Default.Place
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = ImmersiveGlassBorder)
                SummaryRow(
                    label = "Lignes suivies",
                    value = "${lines.size} lignes actives",
                    icon = Icons.Default.Bookmark
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = ImmersiveGlassBorder)
                SummaryRow(
                    label = "Tolérance retard",
                    value = "$delayTolerance min avant réacheminement ToT",
                    icon = Icons.Default.Schedule
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Badge de résilience
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = StmBlue.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, StmBlue.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = StmBlueLight,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Protection active : Bascule instantanée sur cache local Room si coupure réseau STM.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextPrimaryLight
                )
            }
        }
    }
}

// ----------------- COMPOSANTS UTILITAIRES DE CARTE -----------------

@Composable
private fun FeatureItemCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ImmersiveCardBg,
        border = BorderStroke(1.dp, ImmersiveGlassBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = iconTint.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun McpSystemDetailCard(
    badge: String,
    title: String,
    status: String,
    statusColor: Color,
    icon: ImageVector,
    description: String,
    metric: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = ImmersiveCardBg,
        border = BorderStroke(1.dp, ImmersiveGlassBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = badge,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Surface(
                    color = statusColor.copy(alpha = 0.12f),
                    shape = CircleShape
                ) {
                    Text(
                        text = status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryLight,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = metric,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = StmBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondaryLight
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}
