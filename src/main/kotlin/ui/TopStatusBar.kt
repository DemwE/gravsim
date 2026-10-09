package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*

class TopStatusBar {
    private val topBarBg = Color().r(15.toByte()).g(15.toByte()).b(20.toByte()).a(200.toByte())

    fun draw(timeScale: Double, simulatedTime: Double, paused: Boolean, scale: Float = 1.0f) {
        val width = (480 * scale).toInt()
        val height = (85 * scale).toInt()
        val x = (10 * scale).toInt()
        val y = (10 * scale).toInt()

        DrawRectangle(x, y, width, height, topBarBg)
        DrawRectangleLines(x, y, width, height, DARKGRAY)

        val timeScaleFormatted = formatTimeScale(timeScale)
        val simTimeFormatted = formatSimulationTime(simulatedTime)

        val textX = x + (12 * scale).toInt()
        GuiFont.drawText("Simulation Speed: $timeScaleFormatted", textX, y + (8 * scale).toInt(), (16 * scale).toInt(), RAYWHITE)
        GuiFont.drawText("Simulation Time: $simTimeFormatted", textX, y + (28 * scale).toInt(), (16 * scale).toInt(), GOLD)

        if (paused) {
            GuiFont.drawText("[PAUSED - Space to resume]", textX, y + (50 * scale).toInt(), (15 * scale).toInt(), RED)
        } else {
            GuiFont.drawText("Simulation active", textX, y + (50 * scale).toInt(), (14 * scale).toInt(), GREEN)
        }

        val fpsX = x + width - (70 * scale).toInt()
        DrawFPS(fpsX, y + (8 * scale).toInt())
    }
}
