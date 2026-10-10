import engine.*
import lib.*
import spaceObjects.*
import kotlin.math.ceil
import kotlin.random.Random

class SolarSystem(
    val name: String,
    val seed: Seed,
) {
    constructor(seed: Seed) : this(
        name = NameGenerator.generateSystemName(Random(seed.value)),
        seed = seed,
    )

    val objects: MutableList<GravityObject> = mutableListOf()
    private val engine = PhysicsEngine(objects)

    var suggestedTimeScale: Double = 1.0e5
        private set

    fun populate(planets: Int? = null) {
        objects.clear()
        val result = SolarSystemGenerator.generate(name, seed, planets)
        populate(result)
    }

    fun populate(result: SystemGenerationResult) {
        objects.clear()
        objects.addAll(result.objects)
        suggestedTimeScale = result.suggestedTimeScale
        engine.rebuild()
    }

    fun update(dtSim: Double) {
        if (dtSim <= 0.0 || objects.isEmpty()) return

        val steps = ceil(dtSim / MAX_PHYSICS_STEP).toInt().coerceIn(1, MAX_SUBSTEPS)
        val step = dtSim / steps

        repeat(steps) {
            engine.step(step)

            for (i in 0 until engine.count) {
                objects[i].updateTrail(engine.positionX(i), engine.positionY(i))
            }

            engine.resolveCollisions()
        }

        engine.syncToObjects()
    }

    fun close() {
        engine.close()
    }
}
