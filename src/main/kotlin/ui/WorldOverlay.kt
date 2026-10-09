package ui

import com.raylib.Colors.*
import com.raylib.Raylib.*
import spaceObjects.*
import kotlin.math.*

class WorldOverlay {

    private val baryScr = Vector2()

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
            obj.renderPosition.x(obj.position.x.toFloat()).y(obj.position.y.toFloat())
            val screenPos = GetWorldToScreen2D(obj.renderPosition, camera)
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
                baryScr.x((baryX / totalMass).toFloat()).y((baryY / totalMass).toFloat())
                val baryScreen = GetWorldToScreen2D(baryScr, camera)
                val bx = baryScreen.x().toInt()
                val by = baryScreen.y().toInt()
                val crossSize = (8 * scale).toInt()
                DrawLine(bx - crossSize, by, bx + crossSize, by, YELLOW)
                DrawLine(bx, by - crossSize, bx, by + crossSize, YELLOW)
                GuiFont.drawText("Barycenter", bx + 10 * scale, by - 6 * scale, 11f * scale, YELLOW)
            }

            for (obj in objects) {
                obj.renderPosition.x(obj.position.x.toFloat()).y(obj.position.y.toFloat())
                val screenPos = GetWorldToScreen2D(obj.renderPosition, camera)
                val speed = obj.velocity.length()
                val label = "${obj.name} (${formatVelocity(speed)})"
                val objRadius = maxOf(8f * scale, (obj.radius.value * camera.zoom()).toFloat())

                GuiFont.drawText(label, screenPos.x() + objRadius + 4f * scale, screenPos.y() - 6f * scale, 12f * scale, RAYWHITE)

                if (speed > 0.01) {
                    val angle = atan2(obj.velocity.y, obj.velocity.x)
                    val vectorLen = (speed * 1.5 * scale).toFloat().coerceIn(15f * scale, 60f * scale)
                    val startX = screenPos.x().toInt()
                    val startY = screenPos.y().toInt()
                    val endX = (screenPos.x() + cos(angle).toFloat() * vectorLen).toInt()
                    val endY = (screenPos.y() + sin(angle).toFloat() * vectorLen).toInt()

                    DrawLine(startX, startY, endX, endY, LIME)

                    val arrowAngle1 = angle + Math.PI * 0.85
                    val arrowAngle2 = angle - Math.PI * 0.85
                    val arrowLen = 6f * scale
                    val a1X = (endX + cos(arrowAngle1) * arrowLen).toInt()
                    val a1Y = (endY + sin(arrowAngle1) * arrowLen).toInt()
                    val a2X = (endX + cos(arrowAngle2) * arrowLen).toInt()
                    val a2Y = (endY + sin(arrowAngle2) * arrowLen).toInt()

                    DrawLine(endX, endY, a1X, a1Y, LIME)
                    DrawLine(endX, endY, a2X, a2Y, LIME)
                }
            }
        }
    }
}
