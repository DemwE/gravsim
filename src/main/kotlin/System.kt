import bodyTypes.CelestialBodyType
import bodyTypes.StarType
import com.raylib.Raylib.Vector2
import lib.Seed
import units.AstronomicalUnit
import kotlin.random.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import units.*

class System(
    val name: String,
    val seed: Seed,
) {
    val objects: MutableList<spaceObjects.GravityObject> = mutableListOf()

    fun populate(planets: Int) {
        require(planets >= 0) { "Planet count cannot be negative" }
        objects.clear()

        val random = Random(seed.value)
        val starCount = random.nextInt(1, 3)
        val astronomicalUnitKm = unit(1.0, AstronomicalUnit).convertTo(Kilometer).value
        val starGapKm = astronomicalUnitKm
        val planetGapKm = astronomicalUnitKm * 0.1
        val stars = mutableListOf<spaceObjects.Star>()

        repeat(starCount) { index ->
            val type = weightedRandom(StarType.entries, random) { it.weight }
            val mass = random.nextDouble(type.massRange.min.value, type.massRange.max.value)
            val radius = random.nextDouble(type.radiusRange.min.value, type.radiusRange.max.value)
            val temperature = random.nextDouble(
                type.temperatureRange.min.value,
                type.temperatureRange.max.value
            )

            stars += spaceObjects.Star(
                name = if (starCount == 1) name else "$name ${if (index == 0) "A" else "B"}",
                type = type,
                mass = unit(mass, SolarMass),
                radius = unit(radius, Kilometer),
                temperature = unit(temperature, Kelvin),
            )
        }

        if (stars.size == 2) {
            val minimumSeparation = stars.sumOf { it.radius.value } * 3.0
            val separation = maxOf(starGapKm, minimumSeparation)
            stars[0].position = Vector2().x((-separation / 2.0).toFloat()).y(0f)
            stars[1].position = Vector2().x((separation / 2.0).toFloat()).y(0f)
        }
        objects += stars

        val outermostStar = stars.maxOf {
            kotlin.math.abs(it.position.x().toDouble()) + it.radius.value
        }
        var previousPlanetRadius = 0.0
        var orbitRadius = 0.0

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
            val angle = random.nextDouble(0.0, 2.0 * PI)

            objects += spaceObjects.CelestialBody(
                name = "Planet ${index + 1}",
                type = type,
                atmosphere = atmosphere,
                mass = unit(mass, EarthMass),
                radius = unit(radius, Kilometer),
                position = Vector2()
                    .x((cos(angle) * orbitRadius).toFloat())
                    .y((sin(angle) * orbitRadius).toFloat())
            )
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
}