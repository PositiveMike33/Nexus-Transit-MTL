package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM saved_routes ORDER BY timestamp DESC")
    fun getAllRoutes(): Flow<List<RouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity)

    @Query("DELETE FROM saved_routes WHERE id = :id")
    suspend fun deleteRoute(id: Long)
}

@Dao
interface McpDao {
    @Query("SELECT * FROM mcp_servers")
    fun getAllMcpServers(): Flow<List<McpServerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMcpServer(server: McpServerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(servers: List<McpServerEntity>)
}

@Dao
interface LeadDao {
    @Query("SELECT * FROM revenue_leads ORDER BY createdTimestamp DESC")
    fun getAllLeads(): Flow<List<RevenueLeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: RevenueLeadEntity)
}
