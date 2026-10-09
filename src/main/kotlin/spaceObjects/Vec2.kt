package spaceObjects

import com.raylib.Raylib.Vector2
import kotlin.math.sqrt

data class Vec2(val x: Double = 0.0, val y: Double = 0.0) {
    operator fun plus(other: Vec2): Vec2 = Vec2(x + other.x, y + other.y)
    operator fun minus(other: Vec2): Vec2 = Vec2(x - other.x, y - other.y)
    operator fun times(scalar: Double): Vec2 = Vec2(x * scalar, y * scalar)

    fun length(): Double = sqrt(x * x + y * y)

    fun toVector2(): Vector2 = Vector2().x(x.toFloat()).y(y.toFloat())
}
