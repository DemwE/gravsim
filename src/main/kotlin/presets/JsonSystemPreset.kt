package presets

import SolarSystem
import SystemGenerationResult
import bodyTypes.*
import com.google.gson.*
import config.GenerationConfig
import engine.*
import lib.Seed
import spaceObjects.*
import units.*
import java.io.File
import kotlin.math.*

class JsonSystemPreset private constructor(
    val systemName: String,
    private val stars: List<StarDefinition>,
    private val planetDefinitions: List<PlanetDefinition>,
) : PresetFactory {
    override fun create(seed: Seed, planets: Int?): SolarSystem {
        require(planets == null) { "--planets cannot override planets defined in a JSON preset" }

        val createdStars = stars.map { definition ->
            Star(
                name = definition.name,
                type = definition.type,
                mass = unit(definition.massSolar, SolarMass).convertTo(Kilogram),
                radius = unit(definition.radiusKm, Kilometer),
                temperature = unit(definition.temperatureK, Kelvin),
                position = Vec2(definition.positionAu.x * AU_KM, definition.positionAu.y * AU_KM),
            )
        }

        if (createdStars.size == 2) {
            setBinaryStarOrbit(createdStars[0], createdStars[1])
        }

        val createdPlanets = planetDefinitions.map { definition ->
            val centers = when {
                definition.orbitAround.equals(BARYCENTER, ignoreCase = true) -> createdStars
                else -> listOfNotNull(createdStars.firstOrNull { it.name == definition.orbitAround })
            }
            require(centers.isNotEmpty()) {
                "Planet '${definition.name}' orbits unknown star '${definition.orbitAround}'"
            }

            val centerX = centers.sumOf { it.position.x * it.mass.value } / centers.sumOf { it.mass.value }
            val centerY = centers.sumOf { it.position.y * it.mass.value } / centers.sumOf { it.mass.value }
            val radiusKm = definition.orbitAu * AU_KM
            val angle = Math.toRadians(definition.angleDeg)
            val planet = CelestialBody(
                name = definition.name,
                type = definition.type,
                atmosphere = definition.atmosphere,
                mass = unit(definition.massEarth, EarthMass).convertTo(Kilogram),
                radius = unit(definition.radiusKm, Kilometer),
                position = Vec2(
                    centerX + cos(angle) * radiusKm,
                    centerY + sin(angle) * radiusKm,
                ),
            )
            setCircularOrbit(planet, centers, definition.clockwise)
            planet
        }

        val suggestedTimeScale = planetDefinitions.minOfOrNull { planet ->
            val centers = if (planet.orbitAround.equals(BARYCENTER, ignoreCase = true)) {
                createdStars
            } else {
                listOf(createdStars.first { it.name == planet.orbitAround })
            }
            suggestedTimeScaleForOrbitalPeriod(
                orbitalPeriodSeconds(
                    planet.orbitAu * AU_KM,
                    centers.sumOf { it.mass.value },
                ),
            )
        } ?: GenerationConfig.DEFAULT_SUGGESTED_TIME_SCALE

        val result = SystemGenerationResult(createdStars + createdPlanets, suggestedTimeScale)
        return SolarSystem(systemName, seed).apply { populate(result) }
    }

    companion object {
        private const val BARYCENTER = "barycenter"

        fun load(file: File): JsonSystemPreset {
            require(file.isFile && file.canRead()) { "Preset file does not exist or is not readable: ${file.path}" }
            val root = JsonParser.parseString(file.readText()).asObject("root")
            val name = root.requiredString("name").trim()
            require(name.isNotEmpty()) { "System name cannot be blank" }

            val starElements = root.requiredArray("stars")
            require(starElements.size() in 1..2) { "JSON presets currently support one or two stars" }
            val starDefinitions = starElements.mapIndexed { index, element ->
                val star = element.asObject("stars[$index]")
                val position = star.optionalArray("positionAu")?.toAuPosition("stars[$index].positionAu")
                StarDefinition(
                    name = star.requiredString("name").checkedName("stars[$index].name"),
                    type = star.requiredEnum("type", StarType.entries),
                    massSolar = star.requiredPositiveNumber("massSolar"),
                    radiusKm = star.requiredPositiveNumber("radiusKm"),
                    temperatureK = star.requiredPositiveNumber("temperatureK"),
                    positionAu = position ?: Vec2(),
                )
            }
            if (starDefinitions.size == 2) {
                require(starElements.all { it.asObject("star").has("positionAu") }) {
                    "Both stars in a binary system must define positionAu"
                }
                require(starDefinitions[0].positionAu != starDefinitions[1].positionAu) {
                    "Binary stars must have different positionAu coordinates"
                }
            }

            val planetElements = root.requiredArray("planets")
            require(planetElements.size() <= MAX_PLANETS) {
                "A preset can contain at most $MAX_PLANETS planets"
            }
            val planetDefinitions = planetElements.mapIndexed { index, element ->
                val planet = element.asObject("planets[$index]")
                val type = planet.requiredEnum("type", CelestialBodyType.entries)
                val atmosphere = planet.requiredEnum("atmosphere", AtmosphereType.entries)
                require(atmosphere in type.atmosphere) {
                    "Atmosphere '${atmosphere.name}' is not valid for body type '${type.name}' " +
                        "on planets[$index]"
                }
                PlanetDefinition(
                    name = planet.requiredString("name").checkedName("planets[$index].name"),
                    type = type,
                    atmosphere = atmosphere,
                    massEarth = planet.requiredPositiveNumber("massEarth"),
                    radiusKm = planet.requiredPositiveNumber("radiusKm"),
                    orbitAu = planet.requiredPositiveNumber("orbitAu"),
                    orbitAround = planet.requiredString("orbitAround").trim().also {
                        require(it.isNotEmpty()) { "planets[$index].orbitAround cannot be blank" }
                    },
                    angleDeg = planet.optionalNumber("angleDeg") ?: 0.0,
                    clockwise = planet.optionalBoolean("clockwise") ?: false,
                )
            }

            val allNames = starDefinitions.map { it.name } + planetDefinitions.map { it.name }
            require(allNames.distinct().size == allNames.size) { "Star and planet names must be unique" }
            require(starDefinitions.none { it.name.equals(BARYCENTER, ignoreCase = true) }) {
                "A star cannot be named '$BARYCENTER' because it is reserved for the system barycenter"
            }
            val validOrbits = starDefinitions.map { it.name }.toSet() + BARYCENTER
            planetDefinitions.forEach { planet ->
                require(planet.orbitAround in validOrbits || planet.orbitAround.equals(BARYCENTER, true)) {
                    "Planet '${planet.name}' references unknown orbitAround '${planet.orbitAround}'"
                }
            }
            if (starDefinitions.size == 2) {
                val binarySeparationAu = (starDefinitions[0].positionAu - starDefinitions[1].positionAu).length()
                val minimumBarycenterOrbitAu =
                    binarySeparationAu * GenerationConfig.MIN_BINARY_PLANET_ORBIT_FACTOR
                planetDefinitions
                    .filter { it.orbitAround.equals(BARYCENTER, ignoreCase = true) }
                    .forEach { planet ->
                        require(planet.orbitAu >= minimumBarycenterOrbitAu) {
                            "Planet '${planet.name}' has an unstable barycenter orbit: " +
                                "orbitAu must be at least $minimumBarycenterOrbitAu AU " +
                                "(2.5 times the binary separation of $binarySeparationAu AU)"
                        }
                    }
            }

            return JsonSystemPreset(name, starDefinitions, planetDefinitions)
        }

        private fun JsonElement.asObject(description: String): JsonObject =
            takeIf { it.isJsonObject }?.asJsonObject
                ?: throw IllegalArgumentException("Expected $description to be a JSON object")

        private fun JsonObject.requiredString(field: String): String {
            require(has(field) && !get(field).isJsonNull && get(field).isJsonPrimitive && get(field).asJsonPrimitive.isString) {
                "Missing or invalid string field '$field'"
            }
            return get(field).asString
        }

        private fun JsonObject.requiredArray(field: String): JsonArray {
            require(has(field) && get(field).isJsonArray) { "Missing or invalid array field '$field'" }
            return getAsJsonArray(field)
        }

        private fun JsonObject.optionalArray(field: String): JsonArray? {
            if (!has(field) || get(field).isJsonNull) return null
            require(get(field).isJsonArray) { "Field '$field' must be an array" }
            return getAsJsonArray(field)
        }

        private fun JsonArray.toAuPosition(field: String): Vec2 {
            require(size() == 2) { "Field '$field' must contain exactly two numbers: [x, y]" }
            val x = get(0).asFiniteNumber("$field[0]")
            val y = get(1).asFiniteNumber("$field[1]")
            return Vec2(x, y)
        }

        private fun JsonObject.requiredPositiveNumber(field: String): Double {
            val number = requiredNumber(field)
            require(number > 0.0) { "Field '$field' must be greater than zero" }
            return number
        }

        private fun JsonObject.requiredNumber(field: String): Double {
            require(has(field)) { "Missing number field '$field'" }
            return get(field).asFiniteNumber(field)
        }

        private fun JsonObject.optionalNumber(field: String): Double? {
            if (!has(field) || get(field).isJsonNull) return null
            return get(field).asFiniteNumber(field)
        }

        private fun JsonObject.optionalBoolean(field: String): Boolean? {
            if (!has(field) || get(field).isJsonNull) return null
            require(get(field).isJsonPrimitive && get(field).asJsonPrimitive.isBoolean) {
                "Field '$field' must be true or false"
            }
            return get(field).asBoolean
        }

        private fun JsonElement.asFiniteNumber(field: String): Double {
            require(isJsonPrimitive && asJsonPrimitive.isNumber) { "Field '$field' must be a number" }
            return asDouble.also { require(it.isFinite()) { "Field '$field' must be finite" } }
        }

        private fun <T : Enum<T>> JsonObject.requiredEnum(field: String, values: List<T>): T {
            val name = requiredString(field)
            return values.firstOrNull { it.name == name }
                ?: throw IllegalArgumentException(
                    "Unknown value '$name' for '$field'. Valid values: ${values.joinToString { it.name }}",
                )
        }

        private fun String.checkedName(field: String): String = trim().also {
            require(it.isNotEmpty()) { "Field '$field' cannot be blank" }
        }
    }
}

private data class StarDefinition(
    val name: String,
    val type: StarType,
    val massSolar: Double,
    val radiusKm: Double,
    val temperatureK: Double,
    val positionAu: Vec2,
)

private data class PlanetDefinition(
    val name: String,
    val type: CelestialBodyType,
    val atmosphere: AtmosphereType,
    val massEarth: Double,
    val radiusKm: Double,
    val orbitAu: Double,
    val orbitAround: String,
    val angleDeg: Double,
    val clockwise: Boolean,
)
