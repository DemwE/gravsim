package spaceObjects

import com.raylib.Raylib.*
import kotlin.math.sqrt

// G in km^3 / (kg * s^2), matching the simulation's kilometer-based positions.
const val G: Float = 6.67430e-20f

// Dodajemy tzw. "softening factor", aby zapobiec dzieleniu przez 0 i wystrzeliwaniu obiektów w kosmos przy bliskim kontakcie
const val SOFTENING: Float = 100f

fun updateGravity(objects: List<GravityObject>, dt: Float) {
    // 1. Obliczamy siły / przyspieszenia wywierane na każdy obiekt
    for (i in objects.indices) {
        val objA = objects[i]

        for (j in i + 1 until objects.size) {
            val objB = objects[j]

            // Wektor od A do B
            val dx = objB.position.x() - objA.position.x()
            val dy = objB.position.y() - objA.position.y()

            // Odległość kwadratowa z czynnikiem złagodzenia (softening)
            val distSq = dx * dx + dy * dy + SOFTENING
            val dist = sqrt(distSq.toDouble()).toFloat()

            // Wektor jednostkowy (kierunek siły)
            val nx = dx / dist
            val ny = dy / dist

            // Wartość przyspieszenia: a = G * m / r^2
            // Pamiętaj o wyciągnięciu wartości liczbowej z Quantity<Kilogram> (np. .value)
            val massA = objA.mass.value.toFloat()
            val massB = objB.mass.value.toFloat()

            val accelA = G * massB / distSq
            val accelB = G * massA / distSq

            // Zwiększamy prędkości obiektów
            objA.velocity = Vector2().x(objA.velocity.x() + nx * accelA * dt).y(objA.velocity.y() + ny * accelA * dt)

            // Obiekt B przyciąga się w przeciwną stronę (-nx, -ny)
            objB.velocity = Vector2().x(objB.velocity.x() - nx * accelB * dt).y(objB.velocity.y() - ny * accelB * dt)
        }
    }

    // 2. Aktualizujemy pozycję na podstawie obliczonej prędkości
    for (obj in objects) {
        obj.position = Vector2().x(obj.position.x() + obj.velocity.x() * dt).y(obj.position.y() + obj.velocity.y() * dt)
        obj.updateTrail()
    }
}