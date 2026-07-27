package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de données Room locale dédiée à la mise en cache hors-ligne de la télémétrie et des données GTFS-RT de la STM.
 */
@Database(
    entities = [VehiclePositionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StmDatabase : RoomDatabase() {

    abstract fun vehiclePositionDao(): VehiclePositionDao

    companion object {
        @Volatile
        private var INSTANCE: StmDatabase? = null

        fun getInstance(context: Context): StmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StmDatabase::class.java,
                    "stm_local_cache_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
