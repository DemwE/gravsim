package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import config.HudConfig

class TopStatusBar {
    private val panelWidth = HudConfig.TOP_PANEL_WIDTH
    private val panelHeight = HudConfig.TOP_PANEL_HEIGHT
    private val pauseWidth = HudConfig.TOP_PAUSE_WIDTH
    private val stepStartX = HudConfig.TOP_STEP_START_X
    private val stepWidth = HudConfig.TOP_STEP_WIDTH

    fun draw(timeScale: Double, simulatedTime: Double, paused: Boolean, scale: Float = 1.0f) {
        val width = (panelWidth * scale).toInt()
        val height = (panelHeight * scale).toInt()
        val x = (10 * scale).toInt()
        val y = (10 * scale).toInt()

        HudStyle.drawPanel(x, y, width, height, SKYBLUE)

        val timeScaleFormatted = formatTimeScale(timeScale)
            .substringBefore(" (x")
            .replace("1s = ", "1 s = ")
        val simTimeFormatted = formatSimulationTime(simulatedTime)

        val textX = x + (12 * scale).toInt()
        GuiFont.drawText("TIME CONTROL", textX, y + (8 * scale).toInt(), (11 * scale).toInt(), SKYBLUE)
        GuiFont.drawText(if (paused) "PAUSED" else "RUNNING", textX + (405 * scale).toInt(), y + (8 * scale).toInt(), (11 * scale).toInt(), if (paused) RED else GREEN)
        val timeScaleSize = 16f * scale
        val elapsedText = "ELAPSED  $simTimeFormatted"
        val elapsedSize = 13f * scale
        GuiFont.drawText(timeScaleFormatted, textX.toFloat(), y + 29f * scale, timeScaleSize, RAYWHITE)
        val elapsedWidth = GuiFont.measureTextWidth(elapsedText, elapsedSize)
        val elapsedRight = x + width - (12 * scale).toInt()
        GuiFont.drawText(elapsedText, elapsedRight - elapsedWidth, y + 31f * scale, elapsedSize, GOLD)
        HudStyle.drawDivider(x + (12 * scale).toInt(), y + (53 * scale).toInt(), ((panelWidth - 24f) * scale).toInt())

        val selectedStep = TimeWarpSteps.indexOf(timeScale)
        val pauseX = x + (12 * scale).toInt()
        val pauseY = y + (57 * scale).toInt()
        val pauseColor = if (paused) GREEN else RED
        DrawRectangle(pauseX, pauseY, (pauseWidth * scale).toInt(), (34 * scale).toInt(), Fade(pauseColor, 0.12f))
        DrawRectangleLines(pauseX, pauseY, (pauseWidth * scale).toInt(), (34 * scale).toInt(), Fade(pauseColor, 0.8f))
        if (paused) {
            DrawTriangle(
                Vector2().x(pauseX + 14 * scale).y(pauseY + 8 * scale),
                Vector2().x(pauseX + 14 * scale).y(pauseY + 26 * scale),
                Vector2().x(pauseX + 29 * scale).y(pauseY + 17 * scale),
                pauseColor,
            )
        } else {
            val barWidth = 4 * scale
            val barHeight = 15 * scale
            DrawRectangle((pauseX + 13 * scale).toInt(), (pauseY + 9 * scale).toInt(), barWidth.toInt(), barHeight.toInt(), pauseColor)
            DrawRectangle((pauseX + 24 * scale).toInt(), (pauseY + 9 * scale).toInt(), barWidth.toInt(), barHeight.toInt(), pauseColor)
        }

        val buttonWidth = 52f * scale
        val buttonHeight = 34f * scale
        val triangleWidth = 16f * scale
        val triangleHeight = 13f * scale
        val firstCenterX = x + stepStartX * scale + stepWidth * scale / 2f
        val buttonY = y + 57f * scale
        for (index in 0 until TimeWarpSteps.count) {
            val centerX = firstCenterX + index * stepWidth * scale
            val selected = index == selectedStep
            val reached = index <= selectedStep
            val color = if (reached) LIME else SKYBLUE
            val left = (centerX - buttonWidth / 2f).toInt()
            val top = buttonY.toInt()
            DrawRectangle(
                left,
                top,
                buttonWidth.toInt(),
                buttonHeight.toInt(),
                when {
                    selected -> Fade(LIME, 0.2f)
                    reached -> Fade(LIME, 0.09f)
                    else -> Fade(SKYBLUE, 0.035f)
                },
            )
            DrawRectangleLines(
                left,
                top,
                buttonWidth.toInt(),
                buttonHeight.toInt(),
                when {
                    selected -> Fade(LIME, 0.9f)
                    reached -> Fade(LIME, 0.5f)
                    else -> Fade(SKYBLUE, 0.18f)
                },
            )
            val triangleCenterY = buttonY + buttonHeight / 2f
            DrawTriangle(
                Vector2().x(centerX - triangleWidth / 2f).y(triangleCenterY - triangleHeight / 2f),
                Vector2().x(centerX - triangleWidth / 2f).y(triangleCenterY + triangleHeight / 2f),
                Vector2().x(centerX + triangleWidth / 2f).y(triangleCenterY),
                if (reached) color else Fade(color, 0.55f),
            )
        }
    }

    fun timeWarpStepAt(mousePos: Vector2, scale: Float): Int? {
        val factor = HudStyle.dpiScale()
        val x = mousePos.x() * factor
        val y = mousePos.y() * factor
        val panelX = 10f * scale
        val panelY = 10f * scale
        if (y !in (panelY + 57f * scale)..(panelY + 91f * scale)) return null
        val firstCenterX = panelX + (stepStartX + stepWidth / 2f) * scale
        return (0 until TimeWarpSteps.count).firstOrNull { index ->
            val centerX = firstCenterX + index * stepWidth * scale
            x in (centerX - 26f * scale)..(centerX + 26f * scale)
        }
    }

    fun pauseButtonContains(mousePos: Vector2, scale: Float): Boolean {
        val factor = HudStyle.dpiScale()
        val x = mousePos.x() * factor
        val y = mousePos.y() * factor
        val left = 22f * scale
        val top = 67f * scale
        return x in left..(left + pauseWidth * scale) && y in top..(top + 34f * scale)
    }
}
