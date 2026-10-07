import bodyTypes.CelestialBodyType
import bodyTypes.StarType
import com.raylib.Raylib.Vector2
import lib.Seed
import spaceObjects.updateGravity
import units.AstronomicalUnit
import kotlin.random.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import units.*

// Stała G w jednostkach (km^3 / (kg * s^2))
const val G_KM: Double = 6.67430e-20

class SolarSystem(
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
                mass = unit(mass, SolarMass).convertTo(Kilogram),
                radius = unit(radius, Kilometer),
                temperature = unit(temperature, Kelvin),
            )
        }

        // ==========================================
        // 1. POZYCJONOWANIE I PRĘDKOŚĆ GWIAZD
        // ==========================================
        if (stars.size == 1) {
            stars[0].position = Vector2().x(0f).y(0f)
            stars[0].velocity = Vector2().x(0f).y(0f)
        } else if (stars.size == 2) {
            val minimumSeparation = stars.sumOf { it.radius.value } * 3.0
            val separation = maxOf(starGapKm, minimumSeparation)

            // Ustawienie pozycji (na osi X)
            stars[0].position = Vector2().x((-separation / 2.0).toFloat()).y(0f)
            stars[1].position = Vector2().x((separation / 2.0).toFloat()).y(0f)

            // Wyliczenie prędkości orbitalnych dla 2 gwiazd
            val m1 = stars[0].mass.value
            val m2 = stars[1].mass.value
            val totalStarMass = m1 + m2

            // Prędkość względna: v = sqrt(G * (m1 + m2) / r)
            val vRel = sqrt(G_KM * totalStarMass / separation)

            // Wektor prostopadły w górę/dół (kierunek osi Y)
            val v1y = -vRel * (m2 / totalStarMass)
            val v2y = vRel * (m1 / totalStarMass)

            stars[0].velocity = Vector2().x(0f).y(v1y.toFloat())
            stars[1].velocity = Vector2().x(0f).y(v2y.toFloat())
        }

        objects += stars

        // Wyznaczenie środka masy i sumarycznej masy gwiazd dla planet
        val totalStarsMass = stars.sumOf { it.mass.value }

        // Środek masy gwiazd (barycentrum)
        val centerOfMassX = stars.sumOf { it.position.x().toDouble() * it.mass.value } / totalStarsMass
        val centerOfMassY = stars.sumOf { it.position.y().toDouble() * it.mass.value } / totalStarsMass

        // Średnia prędkość barycentrum (powinna wynosić 0,0)
        val centerOfMassVx = stars.sumOf { it.velocity.x().toDouble() * it.mass.value } / totalStarsMass
        val centerOfMassVy = stars.sumOf { it.velocity.y().toDouble() * it.mass.value } / totalStarsMass

        // ==========================================
        // 2. TWORZENIE PLANET I NADAWANIE PRĘDKOŚCI
        // ==========================================
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

            // Wyznaczamy pozycję względem barycentrum gwiazd
            val px = centerOfMassX + cos(angle) * orbitRadius
            val py = centerOfMassY + sin(angle) * orbitRadius

            // Prędkość pierwszej prędkości kosmicznej wokół całkowitej masy gwiazd:
            // v = sqrt(G * M_total / r)
            val orbitalSpeed = sqrt(G_KM * totalStarsMass / orbitRadius)

            // Wektor styczny (prostopadły do kąta pozycji)
            // Daje ruch po orbicie przeciwnie do ruchu wskazówek zegara
            val vx = centerOfMassVx - sin(angle) * orbitalSpeed
            val vy = centerOfMassVy + cos(angle) * orbitalSpeed

            val planet = spaceObjects.CelestialBody(
                name = "Planet ${index + 1}",
                type = type,
                atmosphere = atmosphere,
                mass = unit(mass, EarthMass).convertTo(Kilogram),
                radius = unit(radius, Kilometer),
                position = Vector2()
                    .x(px.toFloat())
                    .y(py.toFloat())
            )

            // Nadajemy wyliczoną prędkość początkową
            planet.velocity = Vector2()
                .x(vx.toFloat())
                .y(vy.toFloat())

            objects += planet
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

    fun update(dt: Float) {
        updateGravity(objects, dt)
    }
}