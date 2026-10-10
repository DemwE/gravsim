package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*

internal object HudStyle {
    private val panelFill = Color().r(12.toByte()).g(18.toByte()).b(28.toByte()).a(224.toByte())
    private val panelBorder = Color().r(70.toByte()).g(91.toByte()).b(112.toByte()).a(210.toByte())

    fun drawPanel(x: Int, y: Int, width: Int, height: Int, accent: Color) {
        DrawRectangle(x, y, width, height, panelFill)
        DrawRectangleLines(x, y, width, height, panelBorder)
        DrawRectangle(x, y, width, maxOf(2, height / 45), Fade(accent, 0.9f))
    }

    fun drawDivider(x: Int, y: Int, width: Int) {
        DrawLine(x, y, x + width, y, Fade(LIGHTGRAY, 0.22f))
    }
}
