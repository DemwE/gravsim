package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import engine.AU_KM
import engine.MAP_HALF_KM

object WorldBoundariesRenderer {

    private val bTopLeft = Vector2()
    private val bTopRight = Vector2()
    private val bBottomRight = Vector2()
    private val bBottomLeft = Vector2()
    private val ringCenter = Vector2()
    private val rangeMarkers = arrayOf(
        10f to Fade(SKYBLUE, 0.10f),
        25f to Fade(SKYBLUE, 0.14f),
        50f to Fade(SKYBLUE, 0.18f),
        100f to Fade(SKYBLUE, 0.25f),
    )

    fun draw() {
        val halfKm = MAP_HALF_KM.toFloat()

        bTopLeft.x(-halfKm).y(-halfKm)
        bTopRight.x(halfKm).y(-halfKm)
        bBottomRight.x(halfKm).y(halfKm)
        bBottomLeft.x(-halfKm).y(halfKm)

        drawGrid(halfKm)

        // A quiet base outline keeps the world edge visible at every zoom level.
        val boundaryColor = Fade(SKYBLUE, 0.12f)
        DrawLineV(bTopLeft, bTopRight, boundaryColor)
        DrawLineV(bTopRight, bBottomRight, boundaryColor)
        DrawLineV(bBottomRight, bBottomLeft, boundaryColor)
        DrawLineV(bBottomLeft, bTopLeft, boundaryColor)

        // Short corner brackets give the simulation area a clean sci-fi frame.
        val bracket = halfKm * 0.035f
        val bracketColor = Fade(SKYBLUE, 0.55f)
        drawCorner(-halfKm, -halfKm, 1f, 1f, bracket, bracketColor)
        drawCorner(halfKm, -halfKm, -1f, 1f, bracket, bracketColor)
        drawCorner(halfKm, halfKm, -1f, -1f, bracket, bracketColor)
        drawCorner(-halfKm, halfKm, 1f, -1f, bracket, bracketColor)

        // Small navigation ticks mark the quarter points along the boundary.
        val tick = halfKm * 0.012f
        for (fraction in floatArrayOf(-0.5f, 0f, 0.5f)) {
            val offset = halfKm * fraction
            DrawLineV(point(offset, -halfKm), point(offset, -halfKm + tick), Fade(SKYBLUE, 0.28f))
            DrawLineV(point(offset, halfKm), point(offset, halfKm - tick), Fade(SKYBLUE, 0.28f))
            DrawLineV(point(-halfKm, offset), point(-halfKm + tick, offset), Fade(SKYBLUE, 0.28f))
            DrawLineV(point(halfKm, offset), point(halfKm - tick, offset), Fade(SKYBLUE, 0.28f))
        }

        // Astronomical range markers for planetary systems, with a gentle brightness ramp.
        ringCenter.x(0f).y(0f)
        val au = AU_KM.toFloat()
        for ((radiusAu, color) in rangeMarkers) {
            DrawCircleLinesV(ringCenter, radiusAu * au, color)
        }
    }

    private fun drawCorner(x: Float, y: Float, xDirection: Float, yDirection: Float, length: Float, color: Color) {
        DrawLineV(point(x, y), point(x + xDirection * length, y), color)
        DrawLineV(point(x, y), point(x, y + yDirection * length), color)
    }

    private fun drawGrid(halfKm: Float) {
        val step = 10f * AU_KM.toFloat()
        val divisions = (halfKm / step).toInt()
        val minorColor = Fade(SKYBLUE, 0.035f)
        val majorColor = Fade(SKYBLUE, 0.075f)
        val axisColor = Fade(SKYBLUE, 0.13f)

        for (index in -divisions..divisions) {
            val coordinate = index * step
            val color = when {
                index == 0 -> axisColor
                index % 5 == 0 -> majorColor
                else -> minorColor
            }
            DrawLineV(point(coordinate, -halfKm), point(coordinate, halfKm), color)
            DrawLineV(point(-halfKm, coordinate), point(halfKm, coordinate), color)
        }
    }

    private fun point(x: Float, y: Float) = Vector2().x(x).y(y)
}
