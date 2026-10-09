package spaceObjects

import com.raylib.Raylib.*
import units.Kilogram
import units.Kilometer
import units.Quantity

abstract class GravityObject(
    open val name: String,
    open var mass: Quantity<Kilogram>,
    open var radius: Quantity<Kilometer>,
    open var position: Vec2 = Vec2(),
    open var velocity: Vec2 = Vec2(),
    open val trailPoints: MutableList<Vector2> = mutableListOf(),
    open var maxTrailPoints: Int = 800,
    open var trailSegmentKm: Double = 1000.0,
) {
    abstract fun draw()

    open fun updateTrail() {
        val currentVec = position.toVector2()
        if (trailPoints.isEmpty()) {
            trailPoints.add(currentVec)
            return
        }

        val last = trailPoints.last()
        val dx = position.x - last.x()
        val dy = position.y - last.y()
        val threshold = trailSegmentKm
        if (dx * dx + dy * dy >= threshold * threshold) {
            trailPoints.add(currentVec)
            if (trailPoints.size > maxTrailPoints) {
                trailPoints.removeAt(0)
            }
        }
    }

    open fun drawTrail(baseColor: Color) {
        if (trailPoints.isEmpty()) return

        val currentPos = position.toVector2()
        val totalSegments = trailPoints.size
        val baseAlpha = baseColor.a().toInt() and 0xFF

        for (i in 0 until totalSegments) {
            val p1 = trailPoints[i]
            val p2 = if (i + 1 < trailPoints.size) trailPoints[i + 1] else currentPos

            val alphaProgress = (i + 1).toFloat() / totalSegments
            val alpha = (alphaProgress * baseAlpha).toInt().coerceIn(0, 255)

            val segmentColor = Color()
                .r(baseColor.r())
                .g(baseColor.g())
                .b(baseColor.b())
                .a(alpha.toByte())

            DrawLineV(p1, p2, segmentColor)
        }
    }

    open fun drawSmoothCircle(position: Vector2, radius: Float, color: Color) {
        DrawCircleSector(position, radius, 0f, 360f, 256, color)
    }
}
