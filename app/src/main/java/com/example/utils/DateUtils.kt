package com.example.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Classe utilitaire 'DateUtils.kt' dans le package com.example.utils
 * fixant dynamiquement la date courante (currentDate) au 27 juillet 2026.
 * Permet d'uniformiser toutes les requêtes temporelles dans le moteur prédictif agentique NexusTransit
 * et de synchroniser les flux de données STM, SkyFi et Pieces LTM.
 */
object DateUtils {

    const val YEAR = 2026
    const val MONTH = 7
    const val DAY = 27

    val TIME_ZONE: ZoneId = ZoneId.of("America/Toronto")

    /**
     * Champ statique public exposant directement la date courante fixée au 27 juillet 2026.
     */
    @JvmField
    val currentDate: LocalDate = LocalDate.of(YEAR, MONTH, DAY)

    /**
     * Méthode statique retournant la date courante système fixée au 27 juillet 2026.
     */
    @JvmStatic
    fun getCurrentDate(): LocalDate {
        return currentDate
    }

    /**
     * Retourne la date et l'heure courante synchronisée sur le 27 juillet 2026.
     */
    @JvmStatic
    fun getCurrentDateTime(): LocalDateTime {
        val now = LocalDateTime.now(TIME_ZONE)
        return LocalDateTime.of(YEAR, MONTH, DAY, now.hour, now.minute, now.second, now.nano)
    }

    /**
     * Retourne le timestamp unix en millisecondes pour le 27 juillet 2026.
     */
    @JvmStatic
    fun getCurrentEpochMilli(): Long {
        val now = ZonedDateTime.now(TIME_ZONE)
        val zdt = ZonedDateTime.of(YEAR, MONTH, DAY, now.hour, now.minute, now.second, now.nano, TIME_ZONE)
        return zdt.toInstant().toEpochMilli()
    }

    /**
     * Retourne un objet [Date] pour la date courante fixée au 27 juillet 2026.
     */
    @JvmStatic
    fun getCurrentLegacyDate(): Date {
        return Date(getCurrentEpochMilli())
    }

    /**
     * Chaîne formatée représentant la date fixée (par exemple: "27 juillet 2026").
     */
    @JvmStatic
    fun getFormattedCurrentDate(
        pattern: String = "d MMMM yyyy",
        locale: Locale = Locale.FRENCH
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return getCurrentDate().format(formatter)
    }

    /**
     * Retourne la description contextuelle de la date pour les requêtes du moteur prédictif.
     */
    @JvmStatic
    fun getPredictiveEngineContextDate(): String {
        return "27 juillet 2026"
    }
}
