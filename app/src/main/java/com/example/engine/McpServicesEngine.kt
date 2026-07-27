package com.example.engine

import com.example.data.McpServerEntity

object McpServicesEngine {

    fun getDefaultMcpServers(): List<McpServerEntity> {
        return listOf(
            McpServerEntity(
                id = "mcp_stm",
                name = "MCP STM Transit GTFS-RT",
                description = "Télémétrie GTFS-RT v2 et API i3 incidents STM Montréal",
                status = "ONLINE",
                pingMs = 18,
                tokenSavingPercent = 88,
                lastLogMessage = "Flux GTFS-RT v2 actif: 247 bus en direct, 4 lignes Métro nominales.",
                endpointUrl = "https://api.stm.info/pub/v2/gtfs/rt/"
            ),
            McpServerEntity(
                id = "mcp_skyfi",
                name = "MCP SkyFi Observation",
                description = "Télé-observation satellite optique et micro-climat métropolitain",
                status = "ONLINE",
                pingMs = 28,
                tokenSavingPercent = 91,
                lastLogMessage = "Endpoint https://mcp.skyfi.com/mcp actif: Télémétrie optique 0.5m connectée.",
                endpointUrl = "https://mcp.skyfi.com/mcp"
            ),
            McpServerEntity(
                id = "mcp_pieces",
                name = "MCP Pieces LTM Memory",
                description = "Mémoire contextuelle LTM sur 9 mois & profil passager",
                status = "ONLINE",
                pingMs = 12,
                tokenSavingPercent = 94,
                lastLogMessage = "Profil LTM chargé: Préférence pour passages couverts et Métro Orange.",
                endpointUrl = "mcp://localhost:1000"
            ),
            McpServerEntity(
                id = "mcp_sales",
                name = "MCP Sales & CRM Hub",
                description = "Automatisations HighLevel/monday, B2B SLA $297/m & aides STA",
                status = "ONLINE",
                pingMs = 24,
                tokenSavingPercent = 82,
                lastLogMessage = "Pipeline actif: 14 licences B2B qualifiées, 100% CTI/RTI calculés.",
                endpointUrl = "https://mcp.nexustransit.b2b/mcp"
            )
        )
    }
}
