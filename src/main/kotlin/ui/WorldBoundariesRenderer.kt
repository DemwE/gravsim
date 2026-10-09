package ui

import com.raylib.Colors.DARKGRAY
import com.raylib.Raylib.*
import engine.AU_KM
import engine.MAP_HALF_KM

/**
 * Renders the cosmological domain bounds and astronomical distance rings.
 */
object WorldBoundariesRenderer {

    private val bTopLeft = Vector2()
    private val bTopRight = Vector2()
    private val bBottomRight = Vector2()
    private val bBottomLeft = Vector2()
    private val ringCenter = Vector2()

    fun draw() {
        val halfKm = MAP_HALF_KM.toFloat()

        bTopLeft.x(-halfKm).y(-halfKm)
        bTopRight.x(halfKm).y(-halfKm)
        bBottomRight.x(halfKm).y(halfKm)
        bBottomLeft.x(-halfKm).y(halfKm)

        val boundaryColor = Fade(DARKGRAY, 0.5f)
        DrawLineV(bTopLeft, bTopRight, boundaryColor)
        DrawLineV(bTopRight, bBottomRight, boundaryColor)
        DrawLineV(bBottomRight, bBottomLeft, boundaryColor)
        DrawLineV(bBottomLeft, bTopLeft, boundaryColor)

        // Astronomical range markers (10 AU, 25 AU, 50 AU)
        ringCenter.x(0f).y(0f)
        val au = AU_KM.toFloat()
        DrawCircleLinesV(ringCenter, 10f * au, Fade(DARKGRAY, 0.2f))
        DrawCircleLinesV(ringCenter, 25f * au, Fade(DARKGRAY, 0.2f))
        DrawCircleLinesV(ringCenter, 50f * au, Fade(DARKGRAY, 0.35f))
    }
}
