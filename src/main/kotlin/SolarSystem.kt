import bodyTypes.CelestialBodyType
import bodyTypes.StarType
import lib.NameGenerator
import lib.Seed
import spaceObjects.CelestialBody
import spaceObjects.G
import spaceObjects.GravityObject
import spaceObjects.MAX_PHYSICS_STEP
import spaceObjects.MAX_SUBSTEPS
import spaceObjects.Star
import spaceObjects.Vec2
import spaceObjects.resolveCollisions
import spaceObjects.setBinaryStarOrbit
import spaceObjects.setCircularOrbit
import spaceObjects.stepPhysics
import units.AstronomicalUnit
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random
import units.*

class SolarSystem(
    val name: String,
    val seed: Seed,
) {
    constructor(seed: Seed) : this(
        name = NameGenerator.generateSystemName(Random(seed.value)),
        seed = seed,
    )

    val objects: MutableList<GravityObject> = mutableListOf()

    var suggestedTimeScale: Double = 1.0e5
        private set

    fun populate(planets: Int) {
        require(planets >= 0) { "Planet count cannot be negative" }
        objects.clear()

        val random = Random(seed.value)
        val starCount = random.nextInt(1, 3)
        val astronomicalUnitKm = unit(1.0, AstronomicalUnit).convertTo(Kilometer).value
        val starGapKm = astronomicalUnitKm
        val planetGapKm = astronomicalUnitKm * 0.1
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

        if (stars.size == 1) {
            stars[0].position = Vec2()
            stars[0].velocity = Vec2()
        } else {
            val m1 = stars[0].mass.value
            val m2 = stars[1].mass.value
            val totalMass = m1 + m2

            val minimumSeparation = stars.sumOf { it.radius.value } * 3.0
            val separation = maxOf(starGapKm, minimumSeparation)

            stars[0].position = Vec2(-separation * (m2 / totalMass), 0.0)
            stars[1].position = Vec2(separation * (m1 / totalMass), 0.0)
            setBinaryStarOrbit(stars[0], stars[1])
        }

        objects += stars

        val totalStarsMass = stars.sumOf { it.mass.value }

        val outermostStar = stars.maxOf { abs(it.position.x) + it.radius.value }
        var previousPlanetRadius = 0.0
        var orbitRadius = 0.0
        var innerOrbitRadius = 0.0

        repeat(planets) { index ->
            val type = weightedRandom(CelestialBodyType.entries, random) { it.weight }
            val atmosphere = weightedRandom(type.atmosphere, random) { it.weight }
            val mass = random.nextDouble(type.massRange.min.value, type.massRange.max.value)
            val radius = random.nextDouble(type.radiusRange.min.value, type.radiusRange.max.value)

            orbitRadius = if (index == 0) {
                outermostStar + radius + planetGapKm
            } else {
                orbitRadius + previousPlanetRadius + radius + planetGapKm
            }
            previousPlanetRadius = radius
            if (index == 0) innerOrbitRadius = orbitRadius

            val angle = random.nextDouble(0.0, 2.0 * PI)
            val planetName = NameGenerator.formatPlanetName(name, index, starCount)
            val planet = CelestialBody(
                name = planetName,
                type = type,
                atmosphere = atmosphere,
                mass = unit(mass, EarthMass).convertTo(Kilogram),
                radius = unit(radius, Kilometer),
                position = Vec2(cos(angle) * orbitRadius, sin(angle) * orbitRadius)
            )

            setCircularOrbit(planet, stars)

            planet.trailSegmentKm = 2.0 * PI * orbitRadius / planet.maxTrailPoints

            objects += planet
        }

        if (planets > 0) {
            val innerPeriod = 2.0 * PI * sqrt(
                innerOrbitRadius * innerOrbitRadius * innerOrbitRadius / (G * totalStarsMass)
            )
            suggestedTimeScale = innerPeriod / 12.0
        }
    }

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

    fun update(dtSim: Double) {
        if (dtSim <= 0.0 || objects.isEmpty()) return

        val steps = ceil(dtSim / MAX_PHYSICS_STEP).toInt().coerceIn(1, MAX_SUBSTEPS)
        val step = dtSim / steps

        repeat(steps) {
            stepPhysics(objects, step)
            resolveCollisions(objects)
        }
    }
}
