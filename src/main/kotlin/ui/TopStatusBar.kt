package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*

class TopStatusBar {
    private val topBarBg = Color().r(15.toByte()).g(15.toByte()).b(20.toByte()).a(200.toByte())

    fun draw(timeScale: Double, simulatedTime: Double, paused: Boolean) {
        DrawRectangle(10, 10, 480, 85, topBarBg)
        DrawRectangleLines(10, 10, 480, 85, DARKGRAY)

        val timeScaleFormatted = formatTimeScale(timeScale)
        val simTimeFormatted = formatSimulationTime(simulatedTime)

        GuiFont.drawText("Simulation Speed: $timeScaleFormatted", 22, 18, 16, RAYWHITE)
        GuiFont.drawText("Simulation Time: $simTimeFormatted", 22, 38, 16, GOLD)

        if (paused) {
            GuiFont.drawText("[PAUSED - Space to resume]", 22, 60, 15, RED)
        } else {
            GuiFont.drawText("Simulation active", 22, 60, 14, GREEN)
        }

        DrawFPS(410, 18)
    }
}
