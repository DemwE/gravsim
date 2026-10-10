package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import config.WorldRenderConfig
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
        for ((radiusAu, alpha) in WorldRenderConfig.RANGE_MARKERS_AU) {
            val radius = radiusAu * au
            if (radius < 1f) continue
            var previousX = origin.x() + radius
            var previousY = origin.y()
            for (i in 1..WorldRenderConfig.RANGE_RING_SEGMENTS) {
                val angle = 2.0 * PI * i / WorldRenderConfig.RANGE_RING_SEGMENTS
                val x = origin.x() + cos(angle).toFloat() * radius
                val y = origin.y() + sin(angle).toFloat() * radius
                DrawLine(previousX.toInt(), previousY.toInt(), x.toInt(), y.toInt(), Fade(SKYBLUE, alpha))
                previousX = x
                previousY = y
            }
        }

        for (obj in objects) {
            val screen = GetWorldToScreen2D(Vector2().x(obj.position.x.toFloat()).y(obj.position.y.toFloat()), camera)
            val radius = (obj.radius.value * camera.zoom()).toFloat()
                .coerceAtLeast(WorldRenderConfig.MIN_BODY_RADIUS_PIXELS)
            when (obj) {
                is Star -> {
                    DrawCircleGradient(screen, radius * WorldRenderConfig.STAR_GLOW_RADIUS_MULTIPLIER, obj.color, Fade(obj.color, 0f))
                    DrawCircleSector(screen, radius, 0f, 360f, WorldRenderConfig.CELESTIAL_BODY_CIRCLE_SEGMENTS, obj.color)
                }
                is CelestialBody -> {
                    if (obj.atmosphere != bodyTypes.AtmosphereType.NONE) {
                        val atmosphere = obj.atmosphere.colorWithOpacity(opacity = WorldRenderConfig.ATMOSPHERE_OPACITY)
                        DrawCircleGradient(screen, radius * WorldRenderConfig.ATMOSPHERE_RADIUS_MULTIPLIER, atmosphere, Fade(atmosphere, 0f))
                    }
                    DrawCircleSector(screen, radius, 0f, 360f, WorldRenderConfig.CELESTIAL_BODY_CIRCLE_SEGMENTS, obj.color)
                }
            }
        }
    }
}
