package spaceObjects

import kotlin.math.cbrt
import kotlin.math.sqrt
import units.Kilogram
import units.Kilometer
import units.Quantity

// G w jednostkach (km^3 / (kg * s^2)), dopasowana do pozycji w kilometrach.
const val G: Double = 6.67430e-20

// Maksymalny pojedynczy krok integracji w sekundach symulacji.
const val MAX_PHYSICS_STEP: Double = 600.0

// Bezpiecznik — maksymalna liczba podkroków na jedną klatkę.
const val MAX_SUBSTEPS: Int = 2000

/**
 * Pojedynczy krok grawitacji (półjawna metoda Eulera).
 * Przyspieszenia zbierane są w tablicach, więc wynik nie zależy od kolejności par.
 */
fun stepPhysics(objects: List<GravityObject>, dt: Double) {
    if (objects.isEmpty() || dt <= 0.0) return

    val count = objects.size
    val accX = DoubleArray(count)
    val accY = DoubleArray(count)

    for (i in 0 until count) {
        val a = objects[i]
        for (j in i + 1 until count) {
            val b = objects[j]
            val dx = b.position.x - a.position.x
            val dy = b.position.y - a.position.y

            // Softening w skali promieni ciał — usuwa osobliwość 1/r^2 przy bliskim kontakcie.
            val softening = a.radius.value + b.radius.value
            val distSq = dx * dx + dy * dy + softening * softening
            val invDist = 1.0 / sqrt(distSq)
            val nx = dx * invDist
            val ny = dy * invDist

            val accA = G * b.mass.value / distSq
            val accB = G * a.mass.value / distSq

            accX[i] += nx * accA
            accY[i] += ny * accA
            accX[j] -= nx * accB
            accY[j] -= ny * accB
        }
    }

    for (i in 0 until count) {
        val obj = objects[i]
        val velocity = Vec2(obj.velocity.x + accX[i] * dt, obj.velocity.y + accY[i] * dt)
        obj.velocity = velocity
        obj.position = Vec2(obj.position.x + velocity.x * dt, obj.position.y + velocity.y * dt)
        obj.updateTrail()
    }
}

/**
 * Scala ciała, których tarcze się przecinają (zderzenie nieelastyczne):
 * zachowany pęd, suma mas, promień z sumy objętości. Zapobiega "wystrzeliwaniu" przy kolizji.
 */
fun resolveCollisions(objects: MutableList<GravityObject>) {
    var i = 0
    while (i < objects.size) {
        var j = i + 1
        var merged = false
        while (j < objects.size) {
            val a = objects[i]
            val b = objects[j]
            val dx = b.position.x - a.position.x
            val dy = b.position.y - a.position.y
            val contact = a.radius.value + b.radius.value

            if (dx * dx + dy * dy <= contact * contact) {
                val dominant = if (a.mass.value >= b.mass.value) a else b
                val absorbed = if (dominant === a) b else a
                mergeInto(dominant, absorbed)
                val index = objects.indexOfFirst { it === absorbed }
                if (index >= 0) objects.removeAt(index)
                merged = true
                break
            }
            j++
        }
        if (!merged) i++
    }
}

private fun mergeInto(primary: GravityObject, absorbed: GravityObject) {
    val m1 = primary.mass.value
    val m2 = absorbed.mass.value
    val totalMass = m1 + m2

    primary.position = Vec2(
        (primary.position.x * m1 + absorbed.position.x * m2) / totalMass,
        (primary.position.y * m1 + absorbed.position.y * m2) / totalMass
    )
    primary.velocity = Vec2(
        (primary.velocity.x * m1 + absorbed.velocity.x * m2) / totalMass,
        (primary.velocity.y * m1 + absorbed.velocity.y * m2) / totalMass
    )
    primary.mass = Quantity(totalMass, Kilogram)

    val r1 = primary.radius.value
    val r2 = absorbed.radius.value
    primary.radius = Quantity(cbrt(r1 * r1 * r1 + r2 * r2 * r2), Kilometer)
}
