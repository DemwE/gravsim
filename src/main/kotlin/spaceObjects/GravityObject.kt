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
    open val trailPoints: ArrayDeque<Vector2> = ArrayDeque(),
    open var maxTrailPoints: Int = 800,
    open var trailSegmentKm: Double = 1000.0,
) {
    abstract fun draw()

    open fun updateTrail() = updateTrail(position.x, position.y)

    open fun updateTrail(x: Double, y: Double) {
        if (trailPoints.isEmpty()) {
            trailPoints.add(Vector2().x(x.toFloat()).y(y.toFloat()))
            return
        }

        val last = trailPoints.last()
        val dx = x - last.x()
        val dy = y - last.y()
        val threshold = trailSegmentKm
        if (dx * dx + dy * dy >= threshold * threshold) {
            trailPoints.add(Vector2().x(x.toFloat()).y(y.toFloat()))
            if (trailPoints.size > maxTrailPoints) {
                trailPoints.removeFirst()
            }
        }
    }

    private var trailStrip: Vector2? = null
    private var trailStripCapacity = 0

    open fun drawTrail(baseColor: Color) {
        if (trailPoints.isEmpty()) return

        val total = trailPoints.size + 1
        if (total < 2) return

        if (trailStrip == null || trailStripCapacity < total) {
            trailStrip = Vector2(total.toLong())
            trailStripCapacity = total
        }
        val strip = trailStrip!!

        for (i in 0 until trailPoints.size) {
            val p = trailPoints[i]
            strip.position(i.toLong()).x(p.x()).y(p.y())
        }
        strip.position((total - 1).toLong()).x(position.x.toFloat()).y(position.y.toFloat())

        val baseAlpha = baseColor.a().toInt() and 0xFF
        val r = baseColor.r()
        val g = baseColor.g()
        val b = baseColor.b()

        val segments = total - 1
        val bands = minOf(16, segments)
        for (band in 0 until bands) {
            val startSeg = segments * band / bands
            val endSeg = if (band == bands - 1) segments else segments * (band + 1) / bands
            val count = endSeg - startSeg + 1

            val progress = endSeg.toFloat() / segments
            val alpha = (progress * baseAlpha).toInt().coerceIn(0, 255)
            val segmentColor = Color().r(r).g(g).b(b).a(alpha.toByte())

            DrawLineStrip(strip.position(startSeg.toLong()), count, segmentColor)
        }
    }

    open fun drawSmoothCircle(position: Vector2, radius: Float, color: Color) {
        if (radius < 8f) {
            DrawCircleV(position, radius, color)
        } else {
            DrawCircleSector(position, radius, 0f, 360f, 64, color)
        }
    }
}
