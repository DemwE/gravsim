package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import engine.AU_KM
import spaceObjects.CelestialBody
import spaceObjects.GravityObject
import spaceObjects.Star
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Draws round shapes after world projection, avoiding float precision loss at astronomical coordinates. */
object ScreenWorldRenderer {
    fun draw(camera: Camera2D, objects: List<GravityObject>) {
        val origin = GetWorldToScreen2D(Vector2().x(0f).y(0f), camera)
        val au = AU_KM.toFloat() * camera.zoom()
        val markers = arrayOf(10f to 0.10f, 25f to 0.14f, 50f to 0.18f, 100f to 0.25f)
        for ((radiusAu, alpha) in markers) {
            val radius = radiusAu * au
            if (radius < 1f) continue
            var previousX = origin.x() + radius
            var previousY = origin.y()
            for (i in 1..256) {
                val angle = 2.0 * PI * i / 256.0
                val x = origin.x() + cos(angle).toFloat() * radius
                val y = origin.y() + sin(angle).toFloat() * radius
                DrawLine(previousX.toInt(), previousY.toInt(), x.toInt(), y.toInt(), Fade(SKYBLUE, alpha))
                previousX = x
                previousY = y
            }
        }

        for (obj in objects) {
            val screen = GetWorldToScreen2D(Vector2().x(obj.position.x.toFloat()).y(obj.position.y.toFloat()), camera)
            val radius = (obj.radius.value * camera.zoom()).toFloat().coerceAtLeast(2f)
            when (obj) {
                is Star -> {
                    DrawCircleGradient(screen, radius * 2.5f, obj.color, Fade(obj.color, 0f))
                    DrawCircleSector(screen, radius, 0f, 360f, 128, obj.color)
                }
                is CelestialBody -> {
                    if (obj.atmosphere != bodyTypes.AtmosphereType.NONE) {
                        val atmosphere = obj.atmosphere.colorWithOpacity(opacity = 75)
                        DrawCircleGradient(screen, radius * 1.5f, atmosphere, Fade(atmosphere, 0f))
                    }
                    DrawCircleSector(screen, radius, 0f, 360f, 128, obj.color)
                }
            }
        }
    }
}
