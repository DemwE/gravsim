package spaceObjects

import com.raylib.Raylib.*
import config.TrailConfig
import units.*

abstract class GravityObject(
    open val name: String,
    open var mass: Quantity<Kilogram>,
    open var radius: Quantity<Kilometer>,
    open var position: Vec2 = Vec2(),
    open var velocity: Vec2 = Vec2(),
    open val trailPoints: ArrayDeque<Vector2> = ArrayDeque(),
    open var maxTrailPoints: Int = TrailConfig.DEFAULT_MAX_POINTS,
    open var trailSegmentKm: Double = TrailConfig.DEFAULT_SEGMENT_KM,
) {
    val renderPosition: Vector2 = Vector2()

    abstract fun draw()

    fun updateTrail(x: Double, y: Double, frameOffsetX: Double, frameOffsetY: Double) {
        if (trailPoints.isEmpty()) {
            trailPoints.add(Vector2().x((x + frameOffsetX).toFloat()).y((y + frameOffsetY).toFloat()))
            return
        }

        val last = trailPoints.last()
        val dx = x - (last.x() - frameOffsetX)
        val dy = y - (last.y() - frameOffsetY)
        val threshold = trailSegmentKm
        if (dx * dx + dy * dy >= threshold * threshold) {
            val storedX = (x + frameOffsetX).toFloat()
            val storedY = (y + frameOffsetY).toFloat()
            val pt = if (trailPoints.size >= maxTrailPoints) {
                trailPoints.removeFirst().apply { x(storedX); y(storedY) }
            } else {
                Vector2().x(storedX).y(storedY)
            }
            trailPoints.add(pt)
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

        for ((i, p) in trailPoints.withIndex()) {
            strip.position(i.toLong()).x(p.x()).y(p.y())
        }
        strip.position((total - 1).toLong()).x(position.x.toFloat()).y(position.y.toFloat())

        val baseAlpha = baseColor.a().toInt() and 0xFF
        val r = baseColor.r()
        val g = baseColor.g()
        val b = baseColor.b()

        val segments = total - 1
        val bands = minOf(TrailConfig.DRAW_ALPHA_BANDS, segments)
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
}
