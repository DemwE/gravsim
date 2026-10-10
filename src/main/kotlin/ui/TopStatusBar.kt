package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*

class TopStatusBar {
    private val topBarBg = Color().r(15.toByte()).g(15.toByte()).b(20.toByte()).a(200.toByte())
    private val panelWidth = 520f
    private val panelHeight = 112f
    private val pauseWidth = 42f
    private val stepStartX = 68f
    private val stepWidth = 58f

    fun draw(timeScale: Double, simulatedTime: Double, paused: Boolean, scale: Float = 1.0f) {
        val width = (panelWidth * scale).toInt()
        val height = (panelHeight * scale).toInt()
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

        val selectedStep = TimeWarpSteps.indexOf(timeScale)
        val pauseX = x + (12 * scale).toInt()
        val pauseY = y + (69 * scale).toInt()
        val pauseColor = if (paused) GREEN else RED
        DrawRectangle(pauseX, pauseY, (pauseWidth * scale).toInt(), (30 * scale).toInt(), Fade(pauseColor, 0.18f))
        DrawRectangleLines(pauseX, pauseY, (pauseWidth * scale).toInt(), (30 * scale).toInt(), pauseColor)
        if (paused) {
            DrawTriangle(
                Vector2().x(pauseX + 14 * scale).y(pauseY + 6 * scale),
                Vector2().x(pauseX + 14 * scale).y(pauseY + 24 * scale),
                Vector2().x(pauseX + 29 * scale).y(pauseY + 15 * scale),
                pauseColor,
            )
        } else {
            val barWidth = 4 * scale
            val barHeight = 15 * scale
            DrawRectangle((pauseX + 13 * scale).toInt(), (pauseY + 7 * scale).toInt(), barWidth.toInt(), barHeight.toInt(), pauseColor)
            DrawRectangle((pauseX + 24 * scale).toInt(), (pauseY + 7 * scale).toInt(), barWidth.toInt(), barHeight.toInt(), pauseColor)
        }

        val triangleWidth = 16f * scale
        val triangleHeight = 12f * scale
        val firstCenterX = x + stepStartX * scale + stepWidth * scale / 2f
        val centerY = y + 80f * scale
        for (index in 0 until TimeWarpSteps.count) {
            val centerX = firstCenterX + index * stepWidth * scale
            val color = if (index <= selectedStep) LIME else DARKGRAY
            DrawTriangle(
                Vector2().x(centerX).y(centerY - triangleHeight / 2f),
                Vector2().x(centerX - triangleWidth / 2f).y(centerY + triangleHeight / 2f),
                Vector2().x(centerX + triangleWidth / 2f).y(centerY + triangleHeight / 2f),
                color,
            )
            val label = TimeWarpSteps.labelAt(index)
            val labelSize = 9f * scale
            val labelWidth = GuiFont.measureTextWidth(label, labelSize)
            GuiFont.drawText(label, centerX - labelWidth / 2f, y + 91f * scale, labelSize, if (index == selectedStep) RAYWHITE else GRAY)
        }

        val fpsX = x + width - (70 * scale).toInt()
        DrawFPS(fpsX, y + (8 * scale).toInt())
    }

    fun timeWarpStepAt(mousePos: Vector2, scale: Float): Int? {
        val factor = dpiScale()
        val x = mousePos.x() * factor
        val y = mousePos.y() * factor
        val panelX = 10f * scale
        val panelY = 10f * scale
        val centerY = panelY + 80f * scale
        if (y !in (centerY - 17f * scale)..(panelY + 108f * scale)) return null
        val firstCenterX = panelX + (stepStartX + stepWidth / 2f) * scale
        return (0 until TimeWarpSteps.count).firstOrNull { index ->
            val centerX = firstCenterX + index * stepWidth * scale
            x in (centerX - stepWidth * scale / 2f)..(centerX + stepWidth * scale / 2f)
        }
    }

    fun pauseButtonContains(mousePos: Vector2, scale: Float): Boolean {
        val factor = dpiScale()
        val x = mousePos.x() * factor
        val y = mousePos.y() * factor
        val left = 22f * scale
        val top = 79f * scale
        return x in left..(left + pauseWidth * scale) && y in top..(top + 30f * scale)
    }

    private fun dpiScale(): Float {
        val logicalWidth = GetScreenWidth()
        return if (logicalWidth > 0) (GetRenderWidth().toFloat() / logicalWidth).coerceAtLeast(1f) else 1f
    }
}
