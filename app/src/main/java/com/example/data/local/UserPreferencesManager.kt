package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Modèle de données pour les préférences initiales de transport configurées durant l'onboarding.
 */
data class UserTransitPreferences(
    val hasCompletedOnboarding: Boolean = false,
    val preferredTransitMode: String = "MULTIMODAL", // "METRO", "BUS", "MULTIMODAL"
    val favoriteLines: Set<String> = setOf("Ligne 1 - Verte", "Ligne 2 - Orange"),
    val delayToleranceMinutes: Int = 5,
    val homeStation: String = "Berri-UQAM",
    val workStation: String = "Station Bonaventure"
)

/**
 * Gestionnaire basé sur SharedPreferences pour la persistance locale des préférences d'onboarding
 * et de personnalisation de transport urbain pour NexusTransit Mtl.
 */
class UserPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "nexus_transit_user_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
        private const val KEY_PREFERRED_MODE = "key_preferred_mode"
        private const val KEY_FAVORITE_LINES = "key_favorite_lines"
        private const val KEY_DELAY_TOLERANCE = "key_delay_tolerance"
        private const val KEY_HOME_STATION = "key_home_station"
        private const val KEY_WORK_STATION = "key_work_station"

        @Volatile
        private var INSTANCE: UserPreferencesManager? = null

        fun getInstance(context: Context): UserPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    fun hasCompletedOnboarding(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun getPreferences(): UserTransitPreferences {
        return UserTransitPreferences(
            hasCompletedOnboarding = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false),
            preferredTransitMode = prefs.getString(KEY_PREFERRED_MODE, "MULTIMODAL") ?: "MULTIMODAL",
            favoriteLines = prefs.getStringSet(KEY_FAVORITE_LINES, setOf("Ligne 1 - Verte", "Ligne 2 - Orange")) ?: setOf("Ligne 1 - Verte", "Ligne 2 - Orange"),
            delayToleranceMinutes = prefs.getInt(KEY_DELAY_TOLERANCE, 5),
            homeStation = prefs.getString(KEY_HOME_STATION, "Berri-UQAM") ?: "Berri-UQAM",
            workStation = prefs.getString(KEY_WORK_STATION, "Station Bonaventure") ?: "Station Bonaventure"
        )
    }

    fun savePreferences(
        preferredMode: String,
        favoriteLines: Set<String>,
        delayToleranceMinutes: Int,
        homeStation: String,
        workStation: String
    ) {
        prefs.edit()
            .putBoolean(KEY_ONBOARDING_COMPLETED, true)
            .putString(KEY_PREFERRED_MODE, preferredMode)
            .putStringSet(KEY_FAVORITE_LINES, favoriteLines)
            .putInt(KEY_DELAY_TOLERANCE, delayToleranceMinutes)
            .putString(KEY_HOME_STATION, homeStation)
            .putString(KEY_WORK_STATION, workStation)
            .apply()
    }

    fun resetOnboarding() {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, false).apply()
    }
}
