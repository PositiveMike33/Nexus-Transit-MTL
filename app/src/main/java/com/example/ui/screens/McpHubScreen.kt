package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.McpServerEntity
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun McpHubScreen(viewModel: MainViewModel) {
    val mcpServers by viewModel.mcpServers.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
                                .clip(CircleShape)
                                .background(PrimaryCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = PrimaryCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Centre d'Orchestration MCP",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Model Context Protocol • Aura MCP v2.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Token optimization banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ImmersiveCardBg, RoundedCornerShape(16.dp))
                            .border(1.dp, ImmersiveGlassBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("-88%", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PrimaryCyan)
                            Text("Tool Grouping", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Divider(modifier = Modifier.height(30.dp).width(1.dp), color = ImmersiveGlassBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("-40%", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = StmGreen)
                            Text("Markdown Strip", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                        Divider(modifier = Modifier.height(30.dp).width(1.dp), color = ImmersiveGlassBorder)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("<25ms", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = SecondaryOrangeStm)
                            Text("Latence stdio/WS", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Serveurs MCP Actifs",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(mcpServers) { server ->
            McpServerCard(
                server = server,
                onPing = { viewModel.pingMcpServer(server.id) }
            )
        }
    }
}

@Composable
fun McpServerCard(server: McpServerEntity, onPing: () -> Unit) {
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
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (server.status == "ONLINE") StmGreen else SecondaryOrangeStm)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = server.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }

                Surface(
                    color = StmGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StmGreen.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${server.pingMs} ms",
                        color = StmGreen,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = server.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ImmersiveCardBg, RoundedCornerShape(12.dp))
                    .border(1.dp, ImmersiveGlassBorder, RoundedCornerShape(12.dp))
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = PrimaryCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = server.lastLogMessage,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Économie de jetons: -${server.tokenSavingPercent}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryCyan,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedButton(
                    onClick = onPing,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("btn_ping_${server.id}")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Inspection Ping")
                }
            }
        }
    }
}
