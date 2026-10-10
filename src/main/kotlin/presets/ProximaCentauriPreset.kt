package presets

import SolarSystem
import SystemGenerationResult
import bodyTypes.AtmosphereType
import bodyTypes.CelestialBodyType
import bodyTypes.StarType
import engine.AU_KM
import lib.Seed
import spaceObjects.CelestialBody
import spaceObjects.Star
import spaceObjects.Vec2
import spaceObjects.setCircularOrbit
import units.*

internal object ProximaCentauriPreset : PresetFactory {
    override fun create(seed: Seed, planets: Int?): SolarSystem {
        val proxima = Star(
            name = "Proxima Centauri",
            type = StarType.M,
            mass = unit(0.1221, SolarMass).convertTo(Kilogram),
            radius = unit(107_300.0, Kilometer),
            temperature = unit(3_042.0, Kelvin),
        )

        val bodies = listOf(
            // Proxima b has no measured radius; this is an Earth-sized rendering estimate.
            createPlanet("Proxima Centauri b", 1.07, 6_500.0, 0.04856, proxima),
            createPlanet("Proxima Centauri d", 0.26, 0.692 * 6_371.0, 0.02881, proxima),
        ).onEach { setCircularOrbit(it, listOf(proxima)) }

        val result = SystemGenerationResult(
            objects = listOf(proxima) + bodies,
            suggestedTimeScale = suggestedTimeScaleForOrbitalPeriod(5.1 * Day.factorToSI),
        )
        return SolarSystem("Proxima Centauri", seed).apply { populate(result) }
    }

    private fun createPlanet(
        name: String,
        massEarth: Double,
        radiusKm: Double,
        orbitAu: Double,
        host: Star,
    ): CelestialBody {
        val orbitRadius = orbitAu * AU_KM
        return CelestialBody(
            name = name,
            type = CelestialBodyType.ROCKY_BODY,
            atmosphere = AtmosphereType.NONE,
            mass = unit(massEarth, EarthMass).convertTo(Kilogram),
            radius = unit(radiusKm, Kilometer),
            position = host.position + Vec2(orbitRadius, 0.0),
        )
    }
}
