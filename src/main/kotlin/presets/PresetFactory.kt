package presets

import SolarSystem
import engine.G
import lib.Seed
import kotlin.math.PI
import kotlin.math.sqrt

fun interface PresetFactory {
    fun create(seed: Seed, planets: Int?): SolarSystem
}

internal fun orbitalPeriodSeconds(radiusKm: Double, centralMassKg: Double): Double =
    2.0 * PI * sqrt(radiusKm * radiusKm * radiusKm / (G * centralMassKg))
