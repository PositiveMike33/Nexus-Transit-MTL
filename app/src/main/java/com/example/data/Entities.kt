package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val origin: String,
    val destination: String,
    val durationMinutes: Int,
    val totScore: Float,
    val isBacktracked: Boolean,
    val weatherCondition: String,
    val modeDetails: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "mcp_servers")
data class McpServerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val status: String, // ONLINE, DEGRADED, SYNCING
    val pingMs: Int,
    val tokenSavingPercent: Int,
    val lastLogMessage: String
)

@Entity(tableName = "revenue_leads")
data class RevenueLeadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientName: String,
    val planType: String, // B2C or B2B
    val monthlyValueCad: Double,
    val staStipendEligible: Boolean,
    val taxRecoveryCad: Double,
    val createdTimestamp: Long = System.currentTimeMillis()
)
