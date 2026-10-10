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

internal object SolarSystemPreset : PresetFactory {
    override fun create(seed: Seed, planets: Int?): SolarSystem {
        val sun = Star(
            name = "Sol",
            type = StarType.G,
            mass = unit(1.0, SolarMass).convertTo(Kilogram),
            radius = unit(696_340.0, Kilometer),
            temperature = unit(5_772.0, Kelvin),
        )
        val planetData = listOf(
            PlanetData("Mercury", CelestialBodyType.ROCKY_BODY, AtmosphereType.NONE, 0.0553, 2_439.7, 0.387),
            PlanetData("Venus", CelestialBodyType.ROCKY_BODY, AtmosphereType.CARBON_DIOXIDE, 0.815, 6_051.8, 0.723),
            PlanetData("Earth", CelestialBodyType.EARTH_LIKE_WORLD, AtmosphereType.NITROGEN, 1.0, 6_371.0, 1.0),
            PlanetData("Mars", CelestialBodyType.ROCKY_BODY, AtmosphereType.CARBON_DIOXIDE, 0.107, 3_389.5, 1.524),
            PlanetData("Jupiter", CelestialBodyType.CLASS_III_GAS_GIANT, AtmosphereType.HELIUM, 317.8, 69_911.0, 5.203),
            PlanetData("Saturn", CelestialBodyType.CLASS_I_GAS_GIANT, AtmosphereType.HELIUM, 95.2, 58_232.0, 9.537),
            PlanetData("Uranus", CelestialBodyType.ICY_BODY, AtmosphereType.METHANE, 14.5, 25_362.0, 19.191),
            PlanetData("Neptune", CelestialBodyType.ICY_BODY, AtmosphereType.METHANE, 17.1, 24_622.0, 30.07),
        )
        val bodies = planetData.map { it.create(sun) }
        val result = SystemGenerationResult(
            objects = listOf(sun) + bodies,
            suggestedTimeScale = suggestedTimeScaleForOrbitalPeriod(orbitalPeriodSeconds(
                planetData.first().orbitAu * AU_KM,
                sun.mass.value,
            )),
        )
        return SolarSystem("Solar System", seed).apply { populate(result) }
    }

    private data class PlanetData(
        val name: String,
        val type: CelestialBodyType,
        val atmosphere: AtmosphereType,
        val massEarth: Double,
        val radiusKm: Double,
        val orbitAu: Double,
    ) {
        fun create(sun: Star): CelestialBody {
            val orbitRadius = orbitAu * AU_KM
            return CelestialBody(
                name = name,
                type = type,
                atmosphere = atmosphere,
                mass = unit(massEarth, EarthMass).convertTo(Kilogram),
                radius = unit(radiusKm, Kilometer),
                position = Vec2(orbitRadius, 0.0),
            ).also { planet ->
                setCircularOrbit(planet, listOf(sun))
            }
        }
    }
}
