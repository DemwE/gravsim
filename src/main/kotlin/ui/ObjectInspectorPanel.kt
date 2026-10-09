package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import spaceObjects.CelestialBody
import spaceObjects.GravityObject
import spaceObjects.Star

class ObjectInspectorPanel {
    private val panelBg = Color().r(15.toByte()).g(15.toByte()).b(20.toByte()).a(200.toByte())

    fun draw(screenHeight: Int, selectedObject: GravityObject?, followedObject: GravityObject?) {
        val obj = selectedObject ?: return

        val inspectorWidth = 360
        val inspectorHeight = 180
        val inspectorY = screenHeight - inspectorHeight - 45

        DrawRectangle(10, inspectorY, inspectorWidth, inspectorHeight, panelBg)
        val borderCol = if (followedObject == obj) GREEN else SKYBLUE
        DrawRectangleLines(10, inspectorY, inspectorWidth, inspectorHeight, borderCol)

        GuiFont.drawText("OBJECT INSPECTOR", 22, inspectorY + 10, 14, borderCol)
        GuiFont.drawText("Name: ${obj.name}", 22, inspectorY + 30, 14, WHITE)

        when (obj) {
            is Star -> {
                GuiFont.drawText("Star type: ${obj.type.displayName}", 22, inspectorY + 48, 13, LIGHTGRAY)
                GuiFont.drawText("Temperature: ${obj.temperature.format()}", 22, inspectorY + 66, 13, LIGHTGRAY)
            }
            is CelestialBody -> {
                GuiFont.drawText("Planet type: ${obj.type.displayName}", 22, inspectorY + 48, 13, LIGHTGRAY)
                GuiFont.drawText("Atmosphere: ${obj.atmosphere.displayName}", 22, inspectorY + 66, 13, LIGHTGRAY)
            }
        }

        GuiFont.drawText("Mass: ${formatMass(obj.mass)}", 22, inspectorY + 84, 13, LIGHTGRAY)
        GuiFont.drawText("Radius: ${formatRadius(obj.radius)}", 22, inspectorY + 102, 13, LIGHTGRAY)
        GuiFont.drawText("Velocity: ${formatVelocity(obj.velocity.length())}", 22, inspectorY + 120, 13, LIGHTGRAY)
        GuiFont.drawText("Distance from center: ${formatDistance(obj.position.length())}", 22, inspectorY + 138, 13, LIGHTGRAY)

        val followStatus = if (followedObject == obj) "[F] Stop following" else "[F] Follow body"
        GuiFont.drawText("$followStatus   [ESC] Deselect", 22, inspectorY + 158, 12, if (followedObject == obj) LIME else YELLOW)
    }
}
