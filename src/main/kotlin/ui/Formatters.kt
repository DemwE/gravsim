package ui

import units.EarthMass
import units.Kilogram
import units.Kilometer
import units.Quantity
import units.SolarMass
import units.convertTo
import java.util.Locale

const val SECONDS_PER_MINUTE = 60.0
const val SECONDS_PER_HOUR = 3600.0
const val SECONDS_PER_DAY = 86400.0
const val SECONDS_PER_YEAR = 365.25 * SECONDS_PER_DAY

const val KM_PER_AU = 1.495978707e8
const val EARTH_RADIUS_KM = 6371.0
const val SOLAR_RADIUS_KM = 696340.0

fun formatSimulationTime(totalSeconds: Double): String {
    if (totalSeconds < 0.0 || !totalSeconds.isFinite()) return "00:00:00"

    val totalSecLong = totalSeconds.toLong()
    if (totalSeconds >= SECONDS_PER_YEAR) {
        val years = (totalSeconds / SECONDS_PER_YEAR).toLong()
        val remSec = totalSeconds % SECONDS_PER_YEAR
        val days = (remSec / SECONDS_PER_DAY).toLong()
        val remDaySec = remSec % SECONDS_PER_DAY
        val hours = (remDaySec / SECONDS_PER_HOUR).toLong()
        val remHourSec = remDaySec % SECONDS_PER_HOUR
        val mins = (remHourSec / SECONDS_PER_MINUTE).toLong()
        val secs = (remHourSec % SECONDS_PER_MINUTE).toLong()
        return String.format(Locale.US, "%d y %d d %02d:%02d:%02d", years, days, hours, mins, secs)
    }

    if (totalSeconds >= SECONDS_PER_DAY) {
        val days = (totalSeconds / SECONDS_PER_DAY).toLong()
        val remDaySec = totalSeconds % SECONDS_PER_DAY
        val hours = (remDaySec / SECONDS_PER_HOUR).toLong()
        val remHourSec = remDaySec % SECONDS_PER_HOUR
        val mins = (remHourSec / SECONDS_PER_MINUTE).toLong()
        val secs = (remHourSec % SECONDS_PER_MINUTE).toLong()
        return String.format(Locale.US, "%d d %02d:%02d:%02d", days, hours, mins, secs)
    }

    val hours = totalSecLong / 3600
    val mins = (totalSecLong % 3600) / 60
    val secs = totalSecLong % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hours, mins, secs)
}

fun formatTimeScale(scale: Double): String {
    if (!scale.isFinite() || scale <= 0.0) return "1s = 0s (x0)"

    return when {
        scale >= SECONDS_PER_YEAR -> {
            val yrs = scale / SECONDS_PER_YEAR
            String.format(Locale.US, "1s = %.2f y (x%.2e)", yrs, scale)
        }
        scale >= SECONDS_PER_DAY -> {
            val days = scale / SECONDS_PER_DAY
            String.format(Locale.US, "1s = %.2f d (x%.2e)", days, scale)
        }
        scale >= SECONDS_PER_HOUR -> {
            val hrs = scale / SECONDS_PER_HOUR
            String.format(Locale.US, "1s = %.2f h (x%.2e)", hrs, scale)
        }
        scale >= SECONDS_PER_MINUTE -> {
            val mins = scale / SECONDS_PER_MINUTE
            String.format(Locale.US, "1s = %.1f min (x%.1f)", mins, scale)
        }
        scale >= 1.0 -> {
            String.format(Locale.US, "1s = %.2f s (x%.2f)", scale, scale)
        }
        else -> {
            String.format(Locale.US, "1s = %.3f s (x%.3f)", scale, scale)
        }
    }
}

fun formatVelocity(kmPerSec: Double): String {
    return String.format(Locale.US, "%.2f km/s", kmPerSec)
}

fun formatMass(mass: Quantity<Kilogram>): String {
    val kg = mass.value
    return when {
        kg >= 1.0e29 -> {
            val sm = mass.convertTo(SolarMass).value
            String.format(Locale.US, "%.3f M_sun (%.2e kg)", sm, kg)
        }
        kg >= 1.0e23 -> {
            val em = mass.convertTo(EarthMass).value
            String.format(Locale.US, "%.3f M_earth (%.2e kg)", em, kg)
        }
        else -> {
            String.format(Locale.US, "%.2e kg", kg)
        }
    }
}

fun formatRadius(radius: Quantity<Kilometer>): String {
    val km = radius.value
    return when {
        km >= 100000.0 -> {
            val sr = km / SOLAR_RADIUS_KM
            String.format(Locale.US, "%,.0f km (%.2f R_sun)", km, sr)
        }
        else -> {
            val er = km / EARTH_RADIUS_KM
            String.format(Locale.US, "%,.0f km (%.2f R_earth)", km, er)
        }
    }
}

fun formatDistance(km: Double): String {
    return if (km >= 1.0e6) {
        val au = km / KM_PER_AU
        String.format(Locale.US, "%.3f AU (%,.0f km)", au, km)
    } else {
        String.format(Locale.US, "%,.0f km", km)
    }
}
