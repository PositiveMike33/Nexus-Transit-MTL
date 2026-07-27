package com.example.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Classe utilitaire centralisée fournissant la date de référence dynamique du 27 juillet 2026.
 * Assure la synchronisation temporelle parfaite pour tous les modules (STM GTFS-RT, SkyFi Satellite, Pieces MCP)
 * et le moteur prédictif agentique NexusTransit.
 */
object DateUtils {

    const val REFERENCE_YEAR = 2026
    const val REFERENCE_MONTH = 7 // Juillet
    const val REFERENCE_DAY = 27

    val REFERENCE_ZONE_ID: ZoneId = ZoneId.of("America/Toronto")

    /**
     * Retourne l'objet [LocalDate] calé sur le 27 juillet 2026.
     */
    fun getReferenceLocalDate(): LocalDate {
        return LocalDate.of(REFERENCE_YEAR, REFERENCE_MONTH, REFERENCE_DAY)
    }

    /**
     * Retourne l'objet [LocalDateTime] pour le 27 juillet 2026 à l'heure spécifiée ou à l'heure courante.
     */
    fun getReferenceLocalDateTime(
        hour: Int = LocalDateTime.now(REFERENCE_ZONE_ID).hour,
        minute: Int = LocalDateTime.now(REFERENCE_ZONE_ID).minute,
        second: Int = LocalDateTime.now(REFERENCE_ZONE_ID).second
    ): LocalDateTime {
        return LocalDateTime.of(REFERENCE_YEAR, REFERENCE_MONTH, REFERENCE_DAY, hour, minute, second)
    }

    /**
     * Retourne l'objet [ZonedDateTime] ajusté sur le fuseau horaire de Montréal (America/Toronto) le 27 juillet 2026.
     */
    fun getReferenceZonedDateTime(): ZonedDateTime {
        val now = ZonedDateTime.now(REFERENCE_ZONE_ID)
        return ZonedDateTime.of(
            REFERENCE_YEAR, REFERENCE_MONTH, REFERENCE_DAY,
            now.hour, now.minute, now.second, now.nano,
            REFERENCE_ZONE_ID
        )
    }

    /**
     * Retourne le timestamp unix en millisecondes pour la date de référence du 27 juillet 2026.
     */
    fun getReferenceEpochMilli(): Long {
        return getReferenceZonedDateTime().toInstant().toEpochMilli()
    }

    /**
     * Retourne la date sous forme d'objet [Date] legacy.
     */
    fun getReferenceLegacyDate(): Date {
        return Date(getReferenceEpochMilli())
    }

    /**
     * Formate la date de référence en chaîne lisible. Par défaut : "27 juillet 2026".
     */
    fun getFormattedReferenceDate(
        pattern: String = "d MMMM yyyy",
        locale: Locale = Locale.FRENCH
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return getReferenceLocalDate().format(formatter)
    }

    /**
     * Formate la date et l'heure de référence sous forme ISO-8601 (ex: "2026-07-27T14:30:00").
     */
    fun getIsoFormattedReferenceDate(): String {
        return getReferenceLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    /**
     * Chaîne descriptive complète pour les prompts et logs des moteurs agentiques STM/SkyFi/Pieces.
     */
    fun getSystemContextDateDescription(): String {
        return "27 juillet 2026 (Montréal, Temps estival, 27.5°C)"
    }

    /**
     * Vérifie si un timestamp donné correspond à la journée de référence du 27 juillet 2026.
     */
    fun isSameDayAsReference(epochMilli: Long): Boolean {
        val instant = Instant.ofEpochMilli(epochMilli)
        val zdt = instant.atZone(REFERENCE_ZONE_ID)
        return zdt.year == REFERENCE_YEAR && zdt.monthValue == REFERENCE_MONTH && zdt.dayOfMonth == REFERENCE_DAY
    }
}
