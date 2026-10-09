package spaceObjects

import com.raylib.Raylib.Vector2
import kotlin.math.sqrt

/**
 * Wektor 2D w Double — stan fizyczny symulacji.
 * Float (raylib Vector2) traci precyzję przy współrzędnych rzędu 1e8 km (1 AU ≈ 16 km),
 * dlatego pozycje i prędkości liczone są wyłącznie na Double.
 */
data class Vec2(val x: Double = 0.0, val y: Double = 0.0) {
    operator fun plus(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2): Vec2 = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Double): Vec2 = Vec2(x * scalar, y * scalar)

    fun length(): Double = sqrt(x * x + y * y)

    fun toVector2(): Vector2 = Vector2().x(x.toFloat()).y(y.toFloat())
}
