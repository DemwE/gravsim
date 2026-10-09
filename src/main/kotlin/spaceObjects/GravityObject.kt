package spaceObjects

import com.raylib.Raylib.*
import units.*

interface GravityObject {
    val name: String
    var mass: Quantity<Kilogram>
    var radius: Quantity<Kilometer>
    var position: Vec2
    var velocity: Vec2

    // Historia pozycji. Trzymamy Vector2 (Float) — to dane wyłącznie do rysowania.
    val trailPoints: MutableList<Vector2>
    var maxTrailPoints: Int

    // Minimalny dystans (km) między kolejnymi próbkami śladu.
    var trailSegmentKm: Double

    fun draw()

    /**
     * Dodaje bieżącą pozycję do śladu, o ile obiekt przesunął się o [trailSegmentKm].
     * Próbkowanie po dystansie, a nie po klatce, uniezależnia ślad od FPS i prędkości symulacji.
     */
    fun updateTrail() {
        val threshold = trailSegmentKm
        if (trailPoints.isNotEmpty()) {
            val last = trailPoints.last()
            val dx = position.x - last.x()
            val dy = position.y - last.y()
            if (dx * dx + dy * dy < threshold * threshold) return
        }

        trailPoints.add(position.toVector2())

        if (trailPoints.size > maxTrailPoints) {
            trailPoints.removeAt(0)
        }
    }

    /**
     * Rysuje ślad z płynnym zanikaniem (alpha rośnie w stronę najnowszego odcinka).
     */
    fun drawTrail(baseColor: Color) {
        val count = trailPoints.size
        if (count < 2) return

        for (i in 0 until count - 1) {
            val p1 = trailPoints[i]
            val p2 = trailPoints[i + 1]

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
