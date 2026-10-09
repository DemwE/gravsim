package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import spaceObjects.CelestialBody
import spaceObjects.GravityObject
import spaceObjects.Star

class ObjectInspectorPanel {
    private val panelBg = Color().r(15.toByte()).g(15.toByte()).b(20.toByte()).a(200.toByte())

    fun draw(screenHeight: Int, selectedObject: GravityObject?, followedObject: GravityObject?, scale: Float = 1.0f) {
        val obj = selectedObject ?: return

        val inspectorWidth = (360 * scale).toInt()
        val inspectorHeight = (180 * scale).toInt()
        val inspectorX = (10 * scale).toInt()
        val inspectorY = screenHeight - inspectorHeight - (45 * scale).toInt()

        DrawRectangle(inspectorX, inspectorY, inspectorWidth, inspectorHeight, panelBg)
        val borderCol = if (followedObject == obj) GREEN else SKYBLUE
        DrawRectangleLines(inspectorX, inspectorY, inspectorWidth, inspectorHeight, borderCol)

        val textX = inspectorX + (12 * scale).toInt()
        GuiFont.drawText("OBJECT INSPECTOR", textX, inspectorY + (10 * scale).toInt(), (14 * scale).toInt(), borderCol)
        GuiFont.drawText("Name: ${obj.name}", textX, inspectorY + (30 * scale).toInt(), (14 * scale).toInt(), WHITE)

        when (obj) {
            is Star -> {
                GuiFont.drawText("Star type: ${obj.type.displayName}", textX, inspectorY + (48 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
                GuiFont.drawText("Temperature: ${obj.temperature.format()}", textX, inspectorY + (66 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
            }
            is CelestialBody -> {
                GuiFont.drawText("Planet type: ${obj.type.displayName}", textX, inspectorY + (48 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
                GuiFont.drawText("Atmosphere: ${obj.atmosphere.displayName}", textX, inspectorY + (66 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
            }
        }

        GuiFont.drawText("Mass: ${formatMass(obj.mass)}", textX, inspectorY + (84 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText("Radius: ${formatRadius(obj.radius)}", textX, inspectorY + (102 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText("Velocity: ${formatVelocity(obj.velocity.length())}", textX, inspectorY + (120 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)
        GuiFont.drawText("Distance from center: ${formatDistance(obj.position.length())}", textX, inspectorY + (138 * scale).toInt(), (13 * scale).toInt(), LIGHTGRAY)

        val followStatus = if (followedObject == obj) "[F] Stop following" else "[F] Follow body"
        GuiFont.drawText("$followStatus   [ESC] Deselect", textX, inspectorY + (158 * scale).toInt(), (12 * scale).toInt(), if (followedObject == obj) LIME else YELLOW)
    }
}
