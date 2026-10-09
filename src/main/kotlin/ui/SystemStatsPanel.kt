package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import spaceObjects.CelestialBody
import spaceObjects.GravityObject
import spaceObjects.Star
import units.Kilogram
import units.SolarMass
import units.convertTo
import units.unit
import java.util.Locale

class SystemStatsPanel {
    private val panelBg = Color().r(15.toByte()).g(15.toByte()).b(20.toByte()).a(200.toByte())

    fun draw(screenWidth: Int, objects: List<GravityObject>, showOverlay: Boolean) {
        val panelWidth = 320
        val panelHeight = 135
        val panelX = screenWidth - panelWidth - 15
        DrawRectangle(panelX, 10, panelWidth, panelHeight, panelBg)
        DrawRectangleLines(panelX, 10, panelWidth, panelHeight, DARKGRAY)

        val starsCount = objects.count { it is Star }
        val planetsCount = objects.count { it is CelestialBody }
        val totalSystemMassKg = objects.sumOf { it.mass.value }
        val totalMassSm = unit(totalSystemMassKg, Kilogram).convertTo(SolarMass).value
        val fastestObj = objects.maxByOrNull { it.velocity.length() }
        val systemRadiusKm = objects.maxOfOrNull { it.position.length() } ?: 0.0

        GuiFont.drawText("SYSTEM STATISTICS", panelX + 12, 18, 14, SKYBLUE)
        GuiFont.drawText("Celestial bodies: $starsCount stars, $planetsCount planets", panelX + 12, 38, 13, LIGHTGRAY)
        GuiFont.drawText(String.format(Locale.US, "Total mass: %.3f M_sun", totalMassSm), panelX + 12, 56, 13, LIGHTGRAY)
        GuiFont.drawText("System radius: ${formatDistance(systemRadiusKm)}", panelX + 12, 74, 13, LIGHTGRAY)
        if (fastestObj != null) {
            GuiFont.drawText("Fastest body: ${fastestObj.name} (${formatVelocity(fastestObj.velocity.length())})", panelX + 12, 92, 13, LIGHTGRAY)
        }
        val overlayStatus = if (showOverlay) "ON" else "OFF"
        GuiFont.drawText("[TAB] Vectors & labels: $overlayStatus", panelX + 12, 114, 12, if (showOverlay) LIME else GRAY)
    }
}
