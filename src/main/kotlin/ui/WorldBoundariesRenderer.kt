package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import config.WorldRenderConfig
import engine.AU_KM
import engine.MAP_HALF_KM

object WorldBoundariesRenderer {

    private val bTopLeft = Vector2()
    private val bTopRight = Vector2()
    private val bBottomRight = Vector2()
    private val bBottomLeft = Vector2()
    fun draw() {
        val halfKm = MAP_HALF_KM.toFloat()

        bTopLeft.x(-halfKm).y(-halfKm)
        bTopRight.x(halfKm).y(-halfKm)
        bBottomRight.x(halfKm).y(halfKm)
        bBottomLeft.x(-halfKm).y(halfKm)

        drawGrid()

        val boundaryColor = Fade(SKYBLUE, WorldRenderConfig.BOUNDARY_ALPHA)
        DrawLineV(bTopLeft, bTopRight, boundaryColor)
        DrawLineV(bTopRight, bBottomRight, boundaryColor)
        DrawLineV(bBottomRight, bBottomLeft, boundaryColor)
        DrawLineV(bBottomLeft, bTopLeft, boundaryColor)

        val bracket = halfKm * WorldRenderConfig.CORNER_BRACKET_FRACTION
        val bracketColor = Fade(SKYBLUE, WorldRenderConfig.CORNER_BRACKET_ALPHA)
        drawCorner(-halfKm, -halfKm, 1f, 1f, bracket, bracketColor)
        drawCorner(halfKm, -halfKm, -1f, 1f, bracket, bracketColor)
        drawCorner(halfKm, halfKm, -1f, -1f, bracket, bracketColor)
        drawCorner(-halfKm, halfKm, 1f, -1f, bracket, bracketColor)

        val tick = halfKm * WorldRenderConfig.BOUNDARY_TICK_FRACTION
        for (fraction in floatArrayOf(-0.5f, 0f, 0.5f)) {
            val offset = halfKm * fraction
            DrawLineV(point(offset, -halfKm), point(offset, -halfKm + tick), Fade(SKYBLUE, WorldRenderConfig.BOUNDARY_TICK_ALPHA))
            DrawLineV(point(offset, halfKm), point(offset, halfKm - tick), Fade(SKYBLUE, WorldRenderConfig.BOUNDARY_TICK_ALPHA))
            DrawLineV(point(-halfKm, offset), point(-halfKm + tick, offset), Fade(SKYBLUE, WorldRenderConfig.BOUNDARY_TICK_ALPHA))
            DrawLineV(point(halfKm, offset), point(halfKm - tick, offset), Fade(SKYBLUE, WorldRenderConfig.BOUNDARY_TICK_ALPHA))
        }
    }

    private fun drawCorner(x: Float, y: Float, xDirection: Float, yDirection: Float, length: Float, color: Color) {
        DrawLineV(point(x, y), point(x + xDirection * length, y), color)
        DrawLineV(point(x, y), point(x, y + yDirection * length), color)
    }

    private fun drawGrid() {
        val halfKm = MAP_HALF_KM.toFloat()
        val step = WorldRenderConfig.GRID_STEP_AU * AU_KM.toFloat()
        val divisions = (halfKm / step).toInt()
        val minorColor = Fade(SKYBLUE, WorldRenderConfig.GRID_MINOR_ALPHA)
        val majorColor = Fade(SKYBLUE, WorldRenderConfig.GRID_MAJOR_ALPHA)
        val axisColor = Fade(SKYBLUE, WorldRenderConfig.GRID_AXIS_ALPHA)

        for (index in -divisions..divisions) {
            val coordinate = index * step
            val color = when {
                index == 0 -> axisColor
                index % WorldRenderConfig.GRID_MAJOR_EVERY == 0 -> majorColor
                else -> minorColor
            }
            DrawLineV(point(coordinate, -halfKm), point(coordinate, halfKm), color)
            DrawLineV(point(-halfKm, coordinate), point(halfKm, coordinate), color)
        }
    }

    private fun point(x: Float, y: Float) = Vector2().x(x).y(y)
}
