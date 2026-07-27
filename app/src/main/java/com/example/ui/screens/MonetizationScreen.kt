package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RevenueLeadEntity
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun MonetizationScreen(viewModel: MainViewModel) {
    val b2cCount by viewModel.numB2cUsers.collectAsState()
    val b2bCount by viewModel.numB2bClients.collectAsState()
    val includeSta by viewModel.includeStaAllowance.collectAsState()
    val investCad by viewModel.hardwareSoftwareInvestCad.collectAsState()
    val leads by viewModel.revenueLeads.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }

    // Calculations
    val b2cMrr = b2cCount * 9.99
    val b2bMrr = b2bCount * 297.0
    val totalMrr = b2cMrr + b2bMrr
    val totalArr = totalMrr * 12

    val staAnnualStipend = if (includeSta) 26000.0 else 0.0 // $500/week * 52
    val taxRecoveryCad = investCad * 0.14975 // Combined Quebec TPS (5%) + TVQ (9.975%)

    val grandTotalYear1 = totalArr + staAnnualStipend + taxRecoveryCad

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ImmersiveGlassBorder, RoundedCornerShape(24.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(StmGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachMoney,
                                contentDescription = null,
                                tint = StmGreen,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Portail de Monétisation Souveraine",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Licences B2C/B2B + Allocation STA Emploi-Québec + CTI/RTI",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = ImmersiveCardBg,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StmGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "PROJECTION ANNÉE 1 (REVENUS TOTAL)",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("$%,.2f CAD", grandTotalYear1),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = StmGreen
                            )
                        }
                    }
                }
            }
        }

        // B2C & B2B Calculator Sliders / Controls
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
                        Text("Simulateur MRR & Subventions", fontWeight = FontWeight.Bold, color = PrimaryCyan)
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = PrimaryCyan)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // B2C Pass ($9.99/mo)
                    Text(
                        text = "Abonnés B2C (9,99 $/mois): $b2cCount usagers",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = b2cCount.toFloat(),
                        onValueChange = { viewModel.numB2cUsers.value = it.toInt() },
                        valueRange = 0f..1000f,
                        colors = SliderDefaults.colors(thumbColor = PrimaryCyan, activeTrackColor = PrimaryCyan),
                        modifier = Modifier.testTag("slider_b2c")
                    )
                    Text(
                        text = "MRR B2C: ${String.format("%.2f $ CAD/m", b2cMrr)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryLight
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // B2B Enterprise ($297/mo)
                    Text(
                        text = "Clients B2B Enterprise (297 $/mois): $b2bCount entreprises",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = b2bCount.toFloat(),
                        onValueChange = { viewModel.numB2bClients.value = it.toInt() },
                        valueRange = 0f..50f,
                        colors = SliderDefaults.colors(thumbColor = StmGreen, activeTrackColor = StmGreen),
                        modifier = Modifier.testTag("slider_b2b")
                    )
                    Text(
                        text = "MRR B2B: ${String.format("%.2f $ CAD/m", b2bMrr)}",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondaryLight
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = ImmersiveGlassBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // STA Emploi-Québec Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Soutien au Travail Autonome (STA)", fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Allocation Emploi-Québec (500 $/semaine)", style = MaterialTheme.typography.labelSmall, color = TextSecondaryLight)
                        }
                        Switch(
                            checked = includeSta,
                            onCheckedChange = { viewModel.includeStaAllowance.value = it },
                            modifier = Modifier.testTag("switch_sta")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hardware/Software CTI/RTI Input
                    Text("Investissement matériel/logiciel pour CTI/RTI TPS/TVQ:", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = investCad.toString(),
                            onValueChange = {
                                val parse = it.toDoubleOrNull()
                                if (parse != null) viewModel.hardwareSoftwareInvestCad.value = parse
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_invest_cad"),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryCyan,
                                unfocusedBorderColor = ImmersiveGlassBorder,
                                focusedContainerColor = ImmersiveCardBg,
                                unfocusedContainerColor = ImmersiveCardBg,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Remboursement CTI/RTI: ${String.format("%.2f $", taxRecoveryCad)}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = StmGreen
                        )
                    }
                }
            }
        }

        // PFP 5-Question Qualification Card
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
                        text = "Produit pour Prospects (PFP) - Qualification 5 Questions",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("1. Quel est l'impact financier de l'optimisation des déplacements de vos employés?", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                    Text("2. Utilisez-vous la télémétrie GTFS-RT STM?", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                    Text("3. Avez-vous inscrit votre entreprise au REQ (TPS/TVQ)?", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                    Text("4. Budget mensuel disponible ($297/mois entreprise)?", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                    Text("5. Urgence de déploiement (1-10)?", style = MaterialTheme.typography.bodySmall, color = TextSecondaryLight)
                }
            }
        }

        // Revenue Pipeline Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Pipeline Prospects & Clients", fontWeight = FontWeight.Bold, color = Color.White)
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan, contentColor = ImmersiveBg),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("btn_add_lead")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter Client")
                }
            }
        }

        items(leads) { lead ->
            RevenueLeadCard(lead = lead)
        }
    }

    if (showAddDialog) {
        AddLeadDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, company, isB2b ->
                viewModel.addRevenueLead(name, company, isB2b)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun RevenueLeadCard(lead: RevenueLeadEntity) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ImmersiveGlassBorder, RoundedCornerShape(20.dp))
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(lead.clientName, fontWeight = FontWeight.Bold, color = Color.White)
                Text(
                    text = "${lead.planType} • Tax CTI/RTI: ${String.format("%.2f $", lead.taxRecoveryCad)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )
            }

            Text(
                text = "${String.format("%.2f $", lead.monthlyValueCad)}/m",
                fontWeight = FontWeight.Bold,
                color = StmGreen
            )
        }
    }
}

@Composable
fun AddLeadDialog(onDismiss: () -> Unit, onAdd: (String, String, Boolean) -> Unit) {
    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var isB2b by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau Lead Client HighLevel / monday") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du contact") },
                    modifier = Modifier.testTag("input_lead_name")
                )
                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Entreprise / Organisme") },
                    modifier = Modifier.testTag("input_lead_company")
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isB2b, onCheckedChange = { isB2b = it })
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Licence B2B Enterprise ($297/mois)")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) onAdd(name, company, isB2b)
                },
                modifier = Modifier.testTag("btn_confirm_add_lead")
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
