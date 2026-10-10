package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import config.HudConfig

internal object HudStyle {
    private val panelFill = Color().r(12.toByte()).g(18.toByte()).b(28.toByte()).a(224.toByte())
    private val panelBorder = Color().r(70.toByte()).g(91.toByte()).b(112.toByte()).a(210.toByte())

    fun drawPanel(x: Int, y: Int, width: Int, height: Int, accent: Color) {
        DrawRectangle(x, y, width, height, panelFill)
        DrawRectangleLines(x, y, width, height, panelBorder)
        DrawRectangle(
            x,
            y,
            width,
            maxOf(HudConfig.PANEL_ACCENT_MIN_HEIGHT, height / HudConfig.PANEL_ACCENT_HEIGHT_DIVISOR),
            Fade(accent, HudConfig.PANEL_ACCENT_ALPHA),
        )
    }

    fun drawDivider(x: Int, y: Int, width: Int) {
        DrawLine(x, y, x + width, y, Fade(LIGHTGRAY, HudConfig.DIVIDER_ALPHA))
    }

    fun dpiScale(): Float {
        val logicalWidth = GetScreenWidth()
        return if (logicalWidth > 0) {
            (GetRenderWidth().toFloat() / logicalWidth).coerceAtLeast(HudConfig.MIN_DPI_SCALE)
        } else {
            HudConfig.MIN_DPI_SCALE
        }
    }

    fun effectiveScale(userScale: Float): Float = userScale * dpiScale()
}
