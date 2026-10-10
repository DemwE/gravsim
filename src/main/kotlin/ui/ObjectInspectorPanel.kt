package ui

import com.raylib.Colors.*
import config.HudConfig
import spaceObjects.*

class ObjectInspectorPanel {
    fun draw(screenHeight: Int, selectedObject: GravityObject?, followedObject: GravityObject?, scale: Float = 1.0f) {
        val obj = selectedObject ?: return

        val inspectorWidth = (HudConfig.INSPECTOR_WIDTH * scale).toInt()
        val inspectorHeight = (HudConfig.INSPECTOR_HEIGHT * scale).toInt()
        val inspectorX = (10 * scale).toInt()
        val inspectorY = screenHeight - inspectorHeight - (45 * scale).toInt()

        val borderCol = if (followedObject == obj) GREEN else SKYBLUE
        HudStyle.drawPanel(inspectorX, inspectorY, inspectorWidth, inspectorHeight, borderCol)

        val textX = inspectorX + (12 * scale).toInt()
        GuiFont.drawText("SELECTED BODY", textX, inspectorY + (10 * scale).toInt(), (11 * scale).toInt(), borderCol)
        GuiFont.drawText(obj.name, textX, inspectorY + (28 * scale).toInt(), (15 * scale).toInt(), WHITE)
        HudStyle.drawDivider(textX, inspectorY + (48 * scale).toInt(), inspectorWidth - (24 * scale).toInt())

        when (obj) {
            is Star -> {
                GuiFont.drawText("Star  ${obj.type.displayName}", textX, inspectorY + (55 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
                GuiFont.drawText("Temperature  ${obj.temperature.format()}", textX, inspectorY + (72 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
            }
            is CelestialBody -> {
                GuiFont.drawText("Planet  ${obj.type.displayName}", textX, inspectorY + (55 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
                GuiFont.drawText("Atmosphere  ${obj.atmosphere.displayName}", textX, inspectorY + (72 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
            }
        }

        GuiFont.drawText("Mass  ${formatMass(obj.mass)}", textX, inspectorY + (91 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText("Radius  ${formatRadius(obj.radius)}", textX, inspectorY + (108 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText("Velocity  ${formatVelocity(obj.velocity.length())}", textX, inspectorY + (125 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText("Distance  ${formatDistance(obj.position.length())}", textX, inspectorY + (142 * scale).toInt(), (12 * scale).toInt(), LIGHTGRAY)

        val followStatus = if (followedObject == obj) "[F] Stop following" else "[F] Follow body"
        GuiFont.drawText("$followStatus   ·   ESC Deselect", textX, inspectorY + (162 * scale).toInt(), (11 * scale).toInt(), if (followedObject == obj) LIME else YELLOW)
    }
}
