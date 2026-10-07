package spaceObjects

import com.raylib.Raylib.*
import units.*

interface GravityObject {
    val name: String
    val mass: Quantity<Kilogram>
    var position: Vector2
    var velocity: Vector2

    // Kolekcja punktów historii pozycji
    val trailPoints: MutableList<Vector2>
    var maxTrailPoints: Int

    fun draw()

    /**
     * Dodaje bieżącą pozycję do śladu.
     * Zapobiega również nakładaniu się bardzo bliskich punktów dla lepszej wydajności.
     */
    fun updateTrail(minDistanceKm: Float = 100f) {
        val currentX = position.x()
        val currentY = position.y()

        if (trailPoints.isNotEmpty()) {
            val last = trailPoints.last()
            val dx = currentX - last.x()
            val dy = currentY - last.y()
            // Rejestrujemy nowy punkt tylko gdy obiekt przesunął się o minimalny dystans
            if (dx * dx + dy * dy < minDistanceKm * minDistanceKm) return
        }

        trailPoints.add(Vector2().x(currentX).y(currentY))

        if (trailPoints.size > maxTrailPoints) {
            trailPoints.removeAt(0)
        }
    }

    /**
     * Rysuje pasek orbity z płynnym zanikaniem (alpha gradient).
     */
    fun drawTrail(baseColor: Color) {
        val count = trailPoints.size
        if (count < 2) return

        for (i in 0 until count - 1) {
            val p1 = trailPoints[i]
            val p2 = trailPoints[i + 1]

            // Keep the newest segment visible; the oldest segment fades to transparent.
            val alphaProgress = ((i + 1).toFloat() / (count - 1))
            val alpha = (alphaProgress * 255).toInt().coerceIn(0, 255)

            val segmentColor = Color()
                .r(baseColor.r())
                .g(baseColor.g())
                .b(baseColor.b())
                .a(alpha.toByte())

            DrawLineV(p1, p2, segmentColor)
        }
    }
}

internal fun drawSmoothCircle(position: Vector2, radius: Float, color: Color) {
    DrawCircleSector(position, radius, 0f, 360f, 256, color)
}