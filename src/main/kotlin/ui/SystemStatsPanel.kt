package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import spaceObjects.*
import units.*
import java.util.Locale

class SystemStatsPanel {
    private val panelBg = Color().r(15.toByte()).g(15.toByte()).b(20.toByte()).a(200.toByte())

    fun draw(screenWidth: Int, objects: List<GravityObject>, showOverlay: Boolean, systemName: String = "System", scale: Float = 1.0f) {
        val panelWidth = (320 * scale).toInt()
        val panelHeight = (135 * scale).toInt()
        val panelX = screenWidth - panelWidth - (15 * scale).toInt()
        val panelY = (10 * scale).toInt()
        DrawRectangle(panelX, panelY, panelWidth, panelHeight, panelBg)
        DrawRectangleLines(panelX, panelY, panelWidth, panelHeight, DARKGRAY)

        val starsCount = objects.count { it is Star }
        val planetsCount = objects.count { it is CelestialBody }
        val totalSystemMassKg = objects.sumOf { it.mass.value }
        val totalMassSm = unit(totalSystemMassKg, Kilogram).convertTo(SolarMass).value
        val fastestObj = objects.maxByOrNull { it.velocity.length() }
        val systemRadiusKm = objects.maxOfOrNull { it.position.length() } ?: 0.0

        val textX = panelX + (12 * scale).toInt()
        GuiFont.drawText("SYSTEM: $systemName", textX, panelY + (8 * scale).toInt(), (14 * scale).toInt(), SKYBLUE)
        GuiFont.drawText("Celestial bodies: $starsCount stars, $planetsCount planets", textX, panelY + (28 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText(String.format(Locale.US, "Total mass: %.3f %s", totalMassSm, SolarMass.symbol), textX, panelY + (46 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText("System radius: ${formatDistance(systemRadiusKm)}", textX, panelY + (64 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
        if (fastestObj != null) {
            GuiFont.drawText("Fastest body: ${fastestObj.name} (${formatVelocity(fastestObj.velocity.length())})", textX, panelY + (82 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
        }
        val overlayStatus = if (showOverlay) "ON" else "OFF"
        GuiFont.drawText("[TAB] Vectors & labels: $overlayStatus", textX, panelY + (104 * scale).toInt(), (12 * scale).toInt(), if (showOverlay) LIME else GRAY)
    }
}
