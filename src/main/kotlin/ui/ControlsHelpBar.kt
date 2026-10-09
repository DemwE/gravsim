package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*

class ControlsHelpBar {
    private val bottomBarBg = Color().r(10.toByte()).g(10.toByte()).b(15.toByte()).a(230.toByte())

    fun draw(screenWidth: Int, screenHeight: Int) {
        val bottomBarHeight = 28
        val bottomBarY = screenHeight - bottomBarHeight
        DrawRectangle(0, bottomBarY, screenWidth, bottomBarHeight, bottomBarBg)
        DrawLine(0, bottomBarY, screenWidth, bottomBarY, DARKGRAY)

        val helpText = "+/-: Speed | 0/O: Reset speed | C: Reset camera | Space: Pause | R: New system | Tab: Overlay | LMB: Select | F: Follow | ESC: Deselect"
        GuiFont.drawText(helpText, 15, bottomBarY + 6, 13, RAYWHITE)
    }
}
