package ui

import units.*
import java.util.Locale

fun formatSimulationTime(totalSeconds: Double): String {
    if (!totalSeconds.isFinite() || totalSeconds < 0.0) return "0.00 d"
    val days = totalSeconds / Day.factorToSI
    if (days >= Year.factorToSI / Day.factorToSI) {
        val daysPerYear = Year.factorToSI / Day.factorToSI
        val years = (days / daysPerYear).toLong()
        val remainingDays = (days - years * daysPerYear).toLong()
        return String.format(Locale.US, "%,d y %,d d", years, remainingDays)
    }
    return String.format(Locale.US, "%,.2f d", days)
}

fun formatTimeScale(scale: Double): String {
    if (!scale.isFinite() || scale <= 0.0) return "1s = 0s (x0)"

    val timeQty = unit(scale, Second)
    return when {
        scale >= Year.factorToSI -> {
            val yrs = timeQty.convertTo(Year).value
            String.format(Locale.US, "1s = %.2f y (x%.2e)", yrs, scale)
        }
        scale >= Day.factorToSI -> {
            val days = timeQty.convertTo(Day).value
            String.format(Locale.US, "1s = %.2f d (x%.2e)", days, scale)
        }
        scale >= Hour.factorToSI -> {
            val hrs = timeQty.convertTo(Hour).value
            String.format(Locale.US, "1s = %.2f h (x%.2e)", hrs, scale)
        }
        scale >= Minute.factorToSI -> {
            val mins = timeQty.convertTo(Minute).value
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
            String.format(Locale.US, "%.3f %s (%.2e kg)", sm, SolarMass.symbol, kg)
        }
        kg >= 1.0e23 -> {
            val em = mass.convertTo(EarthMass).value
            String.format(Locale.US, "%.3f %s (%.2e kg)", em, EarthMass.symbol, kg)
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
            val sr = radius.convertTo(SolarRadius).value
            String.format(Locale.US, "%,.0f km (%.2f %s)", km, sr, SolarRadius.symbol)
        }
        else -> {
            val er = radius.convertTo(EarthRadius).value
            String.format(Locale.US, "%,.0f km (%.2f %s)", km, er, EarthRadius.symbol)
        }
    }
}

fun formatDistance(km: Double): String {
    val distQty = unit(km, Kilometer)
    return if (km >= 1.0e6) {
        val au = distQty.convertTo(AstronomicalUnit).value
        String.format(Locale.US, "%.3f AU (%,.0f km)", au, km)
    } else {
        String.format(Locale.US, "%,.0f km", km)
    }
}
