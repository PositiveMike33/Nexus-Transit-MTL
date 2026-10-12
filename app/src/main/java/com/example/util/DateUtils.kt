package com.example.util

import com.example.model.WeatherObservation
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

/**
 * Classe utilitaire centralisée fournissant la date et l'horodatage en temps réel
 * calés sur le fuseau horaire de Montréal (America/Toronto), synchronisés au 30 septembre 2026.
 * Assure la cohérence temporelle et météorologique pour tous les modules (STM GTFS-RT, SkyFi Satellite, Pieces MCP).
 */
object DateUtils {

    const val REFERENCE_YEAR = 2026
    const val REFERENCE_MONTH = 9 // Septembre
    const val REFERENCE_DAY = 30

    val REFERENCE_ZONE_ID: ZoneId = ZoneId.of("America/Toronto")

    /**
     * Retourne l'objet [LocalDate] en temps réel pour Montréal (calé sur le 30 septembre 2026).
     */
    fun getReferenceLocalDate(): LocalDate {
        val now = LocalDate.now(REFERENCE_ZONE_ID)
        return if (now.year >= REFERENCE_YEAR) now else LocalDate.of(REFERENCE_YEAR, REFERENCE_MONTH, REFERENCE_DAY)
    }

    /**
     * Retourne l'objet [LocalDateTime] pour Montréal à l'heure courante.
     */
    fun getReferenceLocalDateTime(
        hour: Int = LocalDateTime.now(REFERENCE_ZONE_ID).hour,
        minute: Int = LocalDateTime.now(REFERENCE_ZONE_ID).minute,
        second: Int = LocalDateTime.now(REFERENCE_ZONE_ID).second
    ): LocalDateTime {
        val localDate = getReferenceLocalDate()
        return LocalDateTime.of(localDate.year, localDate.monthValue, localDate.dayOfMonth, hour, minute, second)
    }

    /**
     * Retourne l'objet [ZonedDateTime] ajusté sur le fuseau horaire de Montréal (America/Toronto).
     */
    fun getReferenceZonedDateTime(): ZonedDateTime {
        val now = ZonedDateTime.now(REFERENCE_ZONE_ID)
        val localDate = getReferenceLocalDate()
        return ZonedDateTime.of(
            localDate.year, localDate.monthValue, localDate.dayOfMonth,
            now.hour, now.minute, now.second, now.nano,
            REFERENCE_ZONE_ID
        )
    }

    /**
     * Retourne le timestamp unix en millisecondes pour la date courante.
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
     * Formate la date de référence en chaîne lisible. Par défaut : "30 septembre 2026".
     */
    fun getFormattedReferenceDate(
        pattern: String = "d MMMM yyyy",
        locale: Locale = Locale.FRENCH
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return getReferenceLocalDate().format(formatter)
    }

    /**
     * Formate la date et l'heure sous forme ISO-8601 (ex: "2026-09-30T16:30:00").
     */
    fun getIsoFormattedReferenceDate(): String {
        return getReferenceLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    /**
     * Chaîne descriptive complète pour les prompts et logs des moteurs agentiques STM/SkyFi/Pieces.
     */
    fun getSystemContextDateDescription(): String {
        return "30 septembre 2026 (Montréal, Climat automnal doux, 16.5°C, chaussée sèche)"
    }

    /**
     * Observation météorologique en temps réel au 30 septembre à Montréal.
     */
    fun getCurrentWeatherObservation(): WeatherObservation {
        return WeatherObservation(
            condition = "Ciel dégagé et douceur d'automne (Montréal - ${getFormattedReferenceDate()})",
            pavementTempCelsius = 17.0f,
            snowClearanceIndex = 100,
            windChillCelsius = 16.2f,
            iceAlertLevel = "AUCUN",
            skyFiImageryAgeMinutes = 4
        )
    }

    /**
     * Vérifie si un timestamp donné correspond à la journée courante à Montréal.
     */
    fun isSameDayAsReference(epochMilli: Long): Boolean {
        val instant = Instant.ofEpochMilli(epochMilli)
        val zdt = instant.atZone(REFERENCE_ZONE_ID)
        val ref = getReferenceLocalDate()
        return zdt.year == ref.year && zdt.monthValue == ref.monthValue && zdt.dayOfMonth == ref.dayOfMonth
    }
}
