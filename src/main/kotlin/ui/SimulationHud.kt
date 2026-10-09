package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import spaceObjects.CelestialBody
import spaceObjects.GravityObject
import spaceObjects.Star
import spaceObjects.Vec2
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class SimulationHud {
    var showOverlay: Boolean = false
    var selectedObject: GravityObject? = null

    fun selectObjectAt(mousePos: Vector2, camera: Camera2D, objects: List<GravityObject>): GravityObject? {
        var closest: GravityObject? = null
        var minDistance = Float.MAX_VALUE

        for (obj in objects) {
            val screenPos = GetWorldToScreen2D(obj.position.toVector2(), camera)
            val dx = mousePos.x() - screenPos.x()
            val dy = mousePos.y() - screenPos.y()
            val dist = sqrt(dx * dx + dy * dy)
            val hitRadius = maxOf(25f, (obj.radius.value * camera.zoom()).toFloat())

            if (dist <= hitRadius && dist < minDistance) {
                minDistance = dist
                closest = obj
            }
        }
        return closest
    }

    fun drawWorldOverlay(camera: Camera2D, objects: List<GravityObject>, followedObject: GravityObject?) {
        if (objects.isEmpty()) return

        selectedObject?.let { obj ->
            val screenPos = GetWorldToScreen2D(obj.position.toVector2(), camera)
            val visualRadius = maxOf(16f, (obj.radius.value * camera.zoom()).toFloat() + 4f)
            val selectionColor = if (followedObject == obj) GREEN else SKYBLUE

            DrawCircleLines(screenPos.x().toInt(), screenPos.y().toInt(), visualRadius, selectionColor)
            DrawCircleLines(screenPos.x().toInt(), screenPos.y().toInt(), visualRadius + 2f, selectionColor)

            val indicatorText = if (followedObject == obj) "[FOLLOWING]" else "[SELECTED]"
            val textWidth = MeasureText(indicatorText, 10)
            DrawText(indicatorText, (screenPos.x() - textWidth / 2).toInt(), (screenPos.y() - visualRadius - 14f).toInt(), 10, selectionColor)
        }

        if (showOverlay) {
            var totalMass = 0.0
            var baryX = 0.0
            var baryY = 0.0
            for (obj in objects) {
                val m = obj.mass.value
                totalMass += m
                baryX += obj.position.x * m
                baryY += obj.position.y * m
            }
            if (totalMass > 0.0) {
                val baryVec = Vec2(baryX / totalMass, baryY / totalMass)
                val baryScreen = GetWorldToScreen2D(baryVec.toVector2(), camera)
                val bx = baryScreen.x().toInt()
                val by = baryScreen.y().toInt()
                DrawLine(bx - 8, by, bx + 8, by, YELLOW)
                DrawLine(bx, by - 8, bx, by + 8, YELLOW)
                DrawText("Barycenter", bx + 10, by - 6, 10, YELLOW)
            }

            for (obj in objects) {
                val screenPos = GetWorldToScreen2D(obj.position.toVector2(), camera)
                val speed = obj.velocity.length()
                val label = "${obj.name} (${formatVelocity(speed)})"
                val objRadius = maxOf(8f, (obj.radius.value * camera.zoom()).toFloat())

                DrawText(label, (screenPos.x() + objRadius + 4).toInt(), (screenPos.y() - 6).toInt(), 12, RAYWHITE)

                if (speed > 0.01) {
                    val angle = atan2(obj.velocity.y, obj.velocity.x)
                    val vectorLen = (speed * 1.5).toFloat().coerceIn(15f, 60f)
                    val endX = screenPos.x() + cos(angle).toFloat() * vectorLen
                    val endY = screenPos.y() + sin(angle).toFloat() * vectorLen

                    val startV = Vector2().x(screenPos.x()).y(screenPos.y())
                    val endV = Vector2().x(endX).y(endY)
                    DrawLineV(startV, endV, LIME)

                    val arrowAngle1 = angle + Math.PI * 0.85
                    val arrowAngle2 = angle - Math.PI * 0.85
                    val arrowLen = 6f
                    val a1V = Vector2().x((endX + cos(arrowAngle1) * arrowLen).toFloat()).y((endY + sin(arrowAngle1) * arrowLen).toFloat())
                    val a2V = Vector2().x((endX + cos(arrowAngle2) * arrowLen).toFloat()).y((endY + sin(arrowAngle2) * arrowLen).toFloat())
                    DrawLineV(endV, a1V, LIME)
                    DrawLineV(endV, a2V, LIME)
                }
            }
        }
    }

    fun drawScreenHud(
        timeScale: Double,
        simulatedTime: Double,
        paused: Boolean,
        objects: List<GravityObject>,
        followedObject: GravityObject?,
    ) {
        val screenWidth = GetScreenWidth()
        val screenHeight = GetScreenHeight()

        val topBarBg = Color().r(15.toByte()).g(15.toByte()).b(20.toByte()).a(200.toByte())
        DrawRectangle(10, 10, 480, 85, topBarBg)
        DrawRectangleLines(10, 10, 480, 85, DARKGRAY)

        val timeScaleFormatted = formatTimeScale(timeScale)
        val simTimeFormatted = formatSimulationTime(simulatedTime)

        DrawText("Simulation Speed: $timeScaleFormatted", 22, 18, 16, RAYWHITE)
        DrawText("Simulation Time: $simTimeFormatted", 22, 38, 16, GOLD)

        if (paused) {
            DrawText("[PAUSED - Space to resume]", 22, 60, 15, RED)
        } else {
            DrawText("Simulation active", 22, 60, 14, GREEN)
        }

        DrawFPS(410, 18)

        val systemPanelWidth = 320
        val systemPanelHeight = 135
        val systemPanelX = screenWidth - systemPanelWidth - 15
        DrawRectangle(systemPanelX, 10, systemPanelWidth, systemPanelHeight, topBarBg)
        DrawRectangleLines(systemPanelX, 10, systemPanelWidth, systemPanelHeight, DARKGRAY)

        val starsCount = objects.count { it is Star }
        val planetsCount = objects.count { it is CelestialBody }
        val totalSystemMassKg = objects.sumOf { it.mass.value }
        val totalMassSm = totalSystemMassKg / 1.989e30
        val fastestObj = objects.maxByOrNull { it.velocity.length() }
        val systemRadiusKm = objects.maxOfOrNull { it.position.length() } ?: 0.0

        DrawText("SYSTEM STATISTICS", systemPanelX + 12, 18, 14, SKYBLUE)
        DrawText("Celestial bodies: $starsCount stars, $planetsCount planets", systemPanelX + 12, 38, 13, LIGHTGRAY)
        DrawText(String.format(Locale.US, "Total mass: %.3f M_sun", totalMassSm), systemPanelX + 12, 56, 13, LIGHTGRAY)
        DrawText("System radius: ${formatDistance(systemRadiusKm)}", systemPanelX + 12, 74, 13, LIGHTGRAY)
        if (fastestObj != null) {
            DrawText("Fastest body: ${fastestObj.name} (${formatVelocity(fastestObj.velocity.length())})", systemPanelX + 12, 92, 13, LIGHTGRAY)
        }
        val overlayStatus = if (showOverlay) "ON" else "OFF"
        DrawText("[TAB] Vectors & labels: $overlayStatus", systemPanelX + 12, 114, 12, if (showOverlay) LIME else GRAY)

        selectedObject?.let { obj ->
            val inspectorWidth = 360
            val inspectorHeight = 180
            val inspectorY = screenHeight - inspectorHeight - 45

            DrawRectangle(10, inspectorY, inspectorWidth, inspectorHeight, topBarBg)
            val borderCol = if (followedObject == obj) GREEN else SKYBLUE
            DrawRectangleLines(10, inspectorY, inspectorWidth, inspectorHeight, borderCol)

            DrawText("OBJECT INSPECTOR", 22, inspectorY + 10, 14, borderCol)
            DrawText("Name: ${obj.name}", 22, inspectorY + 30, 14, WHITE)

            when (obj) {
                is Star -> {
                    DrawText("Star type: ${obj.type.displayName}", 22, inspectorY + 48, 13, LIGHTGRAY)
                    DrawText("Temperature: ${obj.temperature.format()}", 22, inspectorY + 66, 13, LIGHTGRAY)
                }
                is CelestialBody -> {
                    DrawText("Planet type: ${obj.type.displayName}", 22, inspectorY + 48, 13, LIGHTGRAY)
                    DrawText("Atmosphere: ${obj.atmosphere.displayName}", 22, inspectorY + 66, 13, LIGHTGRAY)
                }
            }

            DrawText("Mass: ${formatMass(obj.mass)}", 22, inspectorY + 84, 13, LIGHTGRAY)
            DrawText("Radius: ${formatRadius(obj.radius)}", 22, inspectorY + 102, 13, LIGHTGRAY)
            DrawText("Velocity: ${formatVelocity(obj.velocity.length())}", 22, inspectorY + 120, 13, LIGHTGRAY)
            DrawText("Distance from center: ${formatDistance(obj.position.length())}", 22, inspectorY + 138, 13, LIGHTGRAY)

            val followStatus = if (followedObject == obj) "[F] Stop following" else "[F] Follow body"
            DrawText("$followStatus   [ESC] Deselect", 22, inspectorY + 158, 12, if (followedObject == obj) LIME else YELLOW)
        }

        val bottomBarHeight = 28
        val bottomBarY = screenHeight - bottomBarHeight
        val bottomBarBg = Color().r(10.toByte()).g(10.toByte()).b(15.toByte()).a(230.toByte())
        DrawRectangle(0, bottomBarY, screenWidth, bottomBarHeight, bottomBarBg)
        DrawLine(0, bottomBarY, screenWidth, bottomBarY, DARKGRAY)

        val helpText = "+/-: Speed | 0/O: Reset speed | C: Reset camera | Space: Pause | R: New system | Tab: Overlay | LMB: Select | F: Follow | ESC: Deselect"
        DrawText(helpText, 15, bottomBarY + 7, 13, RAYWHITE)
    }
}
