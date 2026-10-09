package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*

class ControlsHelpBar {
    private val bottomBarBg = Color().r(10.toByte()).g(10.toByte()).b(15.toByte()).a(230.toByte())

    fun draw(screenWidth: Int, screenHeight: Int, scale: Float = 1.0f) {
        val bottomBarHeight = (28 * scale).toInt()
        val bottomBarY = screenHeight - bottomBarHeight
        DrawRectangle(0, bottomBarY, screenWidth, bottomBarHeight, bottomBarBg)
        DrawLine(0, bottomBarY, screenWidth, bottomBarY, DARKGRAY)

        val helpText = "+/-: Speed | Ctrl + +/-: UI size | 0/O: Reset | C: Reset cam | Space: Pause | R: New sys | Tab: Overlay | LMB: Select | F: Follow | ESC: Deselect"
        GuiFont.drawText(helpText, (15 * scale).toInt(), bottomBarY + (6 * scale).toInt(), (13 * scale).toInt(), RAYWHITE)
    }
}
