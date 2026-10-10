package ui

import com.raylib.Colors.*
import config.HudConfig
import spaceObjects.*
import units.*
import java.util.Locale

class SystemStatsPanel {
    fun draw(screenWidth: Int, objects: List<GravityObject>, showOverlay: Boolean, systemName: String = "System", scale: Float = 1.0f) {
        val panelWidth = (HudConfig.SYSTEM_PANEL_WIDTH * scale).toInt()
        val panelHeight = (HudConfig.SYSTEM_PANEL_HEIGHT * scale).toInt()
        val panelX = screenWidth - panelWidth - (15 * scale).toInt()
        val panelY = (10 * scale).toInt()
        HudStyle.drawPanel(panelX, panelY, panelWidth, panelHeight, SKYBLUE)

        val starsCount = objects.count { it is Star }
        val planetsCount = objects.count { it is CelestialBody }
        val totalSystemMassKg = objects.sumOf { it.mass.value }
        val totalMassSm = unit(totalSystemMassKg, Kilogram).convertTo(SolarMass).value
        val fastestObj = objects.maxByOrNull { it.velocity.length() }
        val systemRadiusKm = objects.maxOfOrNull { it.position.length() } ?: 0.0

        val textX = panelX + (12 * scale).toInt()
        GuiFont.drawText("SYSTEM OVERVIEW", textX, panelY + (8 * scale).toInt(), (11 * scale).toInt(), SKYBLUE)
        GuiFont.drawText(systemName, textX, panelY + (25 * scale).toInt(), (15 * scale).toInt(), RAYWHITE)
        HudStyle.drawDivider(textX, panelY + (45 * scale).toInt(), panelWidth - (24 * scale).toInt())
        GuiFont.drawText("Bodies  $starsCount stars  ·  $planetsCount planets", textX, panelY + (52 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText(String.format(Locale.US, "Mass  %.3f %s", totalMassSm, SolarMass.symbol), textX, panelY + (71 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText("Extent  ${formatDistance(systemRadiusKm)}", textX, panelY + (90 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
        if (fastestObj != null) {
            GuiFont.drawText("Fastest  ${fastestObj.name} · ${formatVelocity(fastestObj.velocity.length())}", textX, panelY + (109 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
        }
        val overlayStatus = if (showOverlay) "ON" else "OFF"
        GuiFont.drawText("TAB  Vectors & labels  $overlayStatus", textX, panelY + (130 * scale).toInt(), (11 * scale).toInt(), if (showOverlay) LIME else GRAY)
    }
}
