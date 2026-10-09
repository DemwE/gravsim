package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import spaceObjects.GravityObject
import spaceObjects.Vec2
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class WorldOverlay {

    fun draw(
        camera: Camera2D,
        objects: List<GravityObject>,
        selectedObject: GravityObject?,
        followedObject: GravityObject?,
        showOverlay: Boolean,
        scale: Float = 1.0f,
    ) {
        if (objects.isEmpty()) return

        selectedObject?.let { obj ->
            val screenPos = GetWorldToScreen2D(obj.position.toVector2(), camera)
            val visualRadius = maxOf(16f * scale, (obj.radius.value * camera.zoom()).toFloat() + 4f * scale)
            val selectionColor = if (followedObject == obj) GREEN else SKYBLUE

            DrawCircleLines(screenPos.x().toInt(), screenPos.y().toInt(), visualRadius, selectionColor)
            DrawCircleLines(screenPos.x().toInt(), screenPos.y().toInt(), visualRadius + 2f * scale, selectionColor)

            val indicatorText = if (followedObject == obj) "[FOLLOWING]" else "[SELECTED]"
            val fontSize = 11f * scale
            val textWidth = GuiFont.measureTextWidth(indicatorText, fontSize)
            GuiFont.drawText(indicatorText, screenPos.x() - textWidth / 2f, screenPos.y() - visualRadius - 16f * scale, fontSize, selectionColor)
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
                val crossSize = (8 * scale).toInt()
                DrawLine(bx - crossSize, by, bx + crossSize, by, YELLOW)
                DrawLine(bx, by - crossSize, bx, by + crossSize, YELLOW)
                GuiFont.drawText("Barycenter", bx + 10 * scale, by - 6 * scale, 11f * scale, YELLOW)
            }

            for (obj in objects) {
                val screenPos = GetWorldToScreen2D(obj.position.toVector2(), camera)
                val speed = obj.velocity.length()
                val label = "${obj.name} (${formatVelocity(speed)})"
                val objRadius = maxOf(8f * scale, (obj.radius.value * camera.zoom()).toFloat())

                GuiFont.drawText(label, screenPos.x() + objRadius + 4f * scale, screenPos.y() - 6f * scale, 12f * scale, RAYWHITE)

                if (speed > 0.01) {
                    val angle = atan2(obj.velocity.y, obj.velocity.x)
                    val vectorLen = (speed * 1.5 * scale).toFloat().coerceIn(15f * scale, 60f * scale)
                    val endX = screenPos.x() + cos(angle).toFloat() * vectorLen
                    val endY = screenPos.y() + sin(angle).toFloat() * vectorLen

                    val startV = Vector2().x(screenPos.x()).y(screenPos.y())
                    val endV = Vector2().x(endX).y(endY)
                    DrawLineV(startV, endV, LIME)

                    val arrowAngle1 = angle + Math.PI * 0.85
                    val arrowAngle2 = angle - Math.PI * 0.85
                    val arrowLen = 6f * scale
                    val a1V = Vector2().x((endX + cos(arrowAngle1) * arrowLen).toFloat()).y((endY + sin(arrowAngle1) * arrowLen).toFloat())
                    val a2V = Vector2().x((endX + cos(arrowAngle2) * arrowLen).toFloat()).y((endY + sin(arrowAngle2) * arrowLen).toFloat())
                    DrawLineV(endV, a1V, LIME)
                    DrawLineV(endV, a2V, LIME)
                }
            }
        }
    }
}
