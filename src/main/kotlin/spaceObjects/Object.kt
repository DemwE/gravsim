package spaceObjects

import com.raylib.Raylib.*

interface Object {
    val name: String
    var position: Vector2

    fun draw()
}

internal fun drawSmoothCircle(position: Vector2, radius: Float, color: Color) {
    DrawCircleSector(position, radius, 0f, 360f, 256, color)
}