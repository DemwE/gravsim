import bodyTypes.*
import config.GenerationConfig
import engine.MAX_PLANETS
import engine.MAP_HALF_KM
import lib.NameGenerator
import lib.Seed
import presets.orbitalPeriodSeconds
import presets.suggestedTimeScaleForOrbitalPeriod
import spaceObjects.*
import units.*
import kotlin.math.*
import kotlin.random.Random

data class SystemGenerationResult(
    val objects: List<GravityObject>,
    val suggestedTimeScale: Double,
)

object SolarSystemGenerator {
    fun planetCountFromSeed(seed: Seed): Int =
        Random(seed.value xor GenerationConfig.SEED_PLANET_COUNT_SALT).nextInt(1, MAX_PLANETS + 1)

    fun generate(name: String, seed: Seed, planets: Int? = null): SystemGenerationResult {
        val planetCount = planets ?: planetCountFromSeed(seed)
        require(planetCount in 0..MAX_PLANETS) {
            "Planet count must be between 0 and $MAX_PLANETS"
        }

        val objects = mutableListOf<GravityObject>()
        val random = Random(seed.value)
        val starCount = random.nextInt(GenerationConfig.MIN_STARS, GenerationConfig.MAX_STARS_EXCLUSIVE)
        val starGapKm = unit(GenerationConfig.STAR_GAP_AU, AstronomicalUnit).convertTo(Kilometer).value
        val planetGapKm = starGapKm * GenerationConfig.PLANET_GAP_FRACTION_OF_STAR_GAP
        val stars = mutableListOf<Star>()

        repeat(starCount) { index ->
            val type = weightedRandom(StarType.entries, random) { it.weight }
            val mass = random.nextDouble(type.massRange.min.value, type.massRange.max.value)
            val radius = random.nextDouble(type.radiusRange.min.value, type.radiusRange.max.value)
            val temperature = random.nextDouble(
                type.temperatureRange.min.value,
                type.temperatureRange.max.value
            )

            val starName = NameGenerator.formatStarName(name, index, starCount)
            stars += Star(
                name = starName,
                type = type,
                mass = unit(mass, SolarMass).convertTo(Kilogram),
                radius = unit(radius, Kilometer),
                temperature = unit(temperature, Kelvin),
            )
        }

        var binarySeparation = 0.0
        if (stars.size == 1) {
            stars[0].position = Vec2()
            stars[0].velocity = Vec2()
        } else {
            val m1 = stars[0].mass.value
            val m2 = stars[1].mass.value
            val totalMass = m1 + m2

            val minimumSeparation = stars.sumOf { it.radius.value } * GenerationConfig.MIN_STAR_SEPARATION_RADII
            val separation = maxOf(starGapKm, minimumSeparation)
            binarySeparation = separation

            stars[0].position = Vec2(-separation * (m2 / totalMass), 0.0)
            stars[1].position = Vec2(separation * (m1 / totalMass), 0.0)
            setBinaryStarOrbit(stars[0], stars[1])
        }

        objects += stars

        val totalStarsMass = stars.sumOf { it.mass.value }
        val outermostStar = stars.maxOf { abs(it.position.x) + it.radius.value }
        val minimumPlanetDistance = if (starCount > 1) {
            binarySeparation * GenerationConfig.MIN_BINARY_PLANET_ORBIT_FACTOR
        } else {
            outermostStar + planetGapKm
        }

        val generatedPlanets = List(planetCount) {
            val type = weightedRandom(CelestialBodyType.entries, random) { it.weight }
            val atmosphere = weightedRandom(type.atmosphere, random) { it.weight }
            val mass = random.nextDouble(type.massRange.min.value, type.massRange.max.value)
            val radius = random.nextDouble(type.radiusRange.min.value, type.radiusRange.max.value)
            GeneratedPlanet(type, atmosphere, mass, radius)
        }

        val orbitRadii = generateOrbitRadii(
            innerRadius = minimumPlanetDistance + (generatedPlanets.firstOrNull()?.radiusKm ?: 0.0),
            outerRadius = MAP_HALF_KM * random.nextDouble(
                GenerationConfig.MIN_ORBIT_MAP_FRACTION,
                GenerationConfig.MAX_ORBIT_MAP_FRACTION,
            ),
            count = planetCount,
            random = random,
        )
        val innerOrbitRadius = orbitRadii.firstOrNull() ?: 0.0

        generatedPlanets.forEachIndexed { index, generatedPlanet ->
            val orbitRadius = orbitRadii[index]
            val angle = random.nextDouble(0.0, 2.0 * PI)
            val planetName = NameGenerator.formatPlanetName(name, index, starCount)
            val planet = CelestialBody(
                name = planetName,
                type = generatedPlanet.type,
                atmosphere = generatedPlanet.atmosphere,
                mass = unit(generatedPlanet.massEarth, EarthMass).convertTo(Kilogram),
                radius = unit(generatedPlanet.radiusKm, Kilometer),
                position = Vec2(cos(angle) * orbitRadius, sin(angle) * orbitRadius)
            )

            setCircularOrbit(planet, stars)
            objects += planet
        }

        var suggestedTimeScale = GenerationConfig.DEFAULT_SUGGESTED_TIME_SCALE
        if (planetCount > 0) {
            val innerPeriod = orbitalPeriodSeconds(innerOrbitRadius, totalStarsMass)
            suggestedTimeScale = suggestedTimeScaleForOrbitalPeriod(innerPeriod)
        }

        return SystemGenerationResult(objects, suggestedTimeScale)
    }

    private fun generateOrbitRadii(
        innerRadius: Double,
        outerRadius: Double,
        count: Int,
        random: Random,
    ): List<Double> {
        if (count == 0) return emptyList()

        val safeOuterRadius = maxOf(innerRadius, outerRadius)
        val innerLog = ln(innerRadius)
        val outerLog = ln(safeOuterRadius)
        if (count == 1) {
            return listOf(exp(random.nextDouble(innerLog, outerLog)))
        }

        val logStep = (outerLog - innerLog) / (count - 1)
        val jitterLimit = logStep * GenerationConfig.ORBIT_RADIUS_JITTER_FRACTION
        return List(count) { index ->
            val jitter = if (index == 0 || index == count - 1) {
                0.0
            } else {
                random.nextDouble(-jitterLimit, jitterLimit)
            }
            exp(innerLog + logStep * index + jitter)
        }
    }

    private data class GeneratedPlanet(
        val type: CelestialBodyType,
        val atmosphere: AtmosphereType,
        val massEarth: Double,
        val radiusKm: Double,
    )

    private fun <T> weightedRandom(
        items: List<T>,
        random: Random,
        weight: (T) -> Float,
    ): T {
        require(items.isNotEmpty()) { "Cannot select from an empty list" }
        val weights = items.map(weight)
        require(weights.all { it.isFinite() && it >= 0f }) {
            "Weights must be finite and non-negative"
        }

        val totalWeight = weights.sumOf { it.toDouble() }
        require(totalWeight > 0.0) { "At least one weight must be positive" }

        var selection = random.nextDouble(totalWeight)
        for (index in items.indices) {
            selection -= weights[index]
            if (selection < 0.0) {
                return items[index]
            }
        }

        return items[weights.indexOfLast { it > 0f }]
    }
}
