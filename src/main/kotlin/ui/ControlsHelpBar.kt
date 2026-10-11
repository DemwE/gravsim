package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import config.HudConfig

class ControlsHelpBar {
    private val bottomBarBg = Color().r(10.toByte()).g(16.toByte()).b(24.toByte()).a(238.toByte())

    fun draw(screenWidth: Int, screenHeight: Int, scale: Float = 1.0f) {
        val bottomBarHeight = (HudConfig.BOTTOM_BAR_HEIGHT * scale).toInt()
        val bottomBarY = screenHeight - bottomBarHeight
        DrawRectangle(0, bottomBarY, screenWidth, bottomBarHeight, bottomBarBg)
        DrawLine(0, bottomBarY, screenWidth, bottomBarY, DARKGRAY)

        val helpText = "+ / - SPEED   ·   0 RESET SPEED   ·   SPACE PAUSE   ·   F FOLLOW   ·   ESC CLEAR   ·   TAB OVERLAY   ·   R GENERATE NEW   ·   C CAMERA"
        GuiFont.drawText(helpText, (15 * scale).toInt(), bottomBarY + (8 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
    }
}
