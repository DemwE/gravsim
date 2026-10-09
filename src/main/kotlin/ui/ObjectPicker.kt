package ui

import com.raylib.Raylib.*
import spaceObjects.GravityObject

/**
 * World-space object picker converting screen cursor coordinates to celestial bodies.
 */
internal object ObjectPicker {

    fun pickObject(
        mousePos: Vector2,
        camera: Camera2D,
        objects: List<GravityObject>,
    ): GravityObject? {
        val zoom = camera.zoom()
        if (zoom <= 0f) return null

        val worldMouse = GetScreenToWorld2D(mousePos, camera)
        val mouseX = worldMouse.x().toDouble()
        val mouseY = worldMouse.y().toDouble()

        val screenWidth = GetRenderWidth()
        val logicalWidth = GetScreenWidth()
        val dpiScale = if (logicalWidth > 0) (screenWidth.toFloat() / logicalWidth.toFloat()).coerceAtLeast(1.0f) else 1.0f

        val hitPaddingKm = (25.0 * dpiScale) / zoom

        var closest: GravityObject? = null
        var minDistanceSq = Double.MAX_VALUE

        for (obj in objects) {
            val dx = mouseX - obj.position.x
            val dy = mouseY - obj.position.y
            val distSq = dx * dx + dy * dy
            val hitRadius = maxOf(hitPaddingKm, obj.radius.value)

            if (distSq <= hitRadius * hitRadius && distSq < minDistanceSq) {
                minDistanceSq = distSq
                closest = obj
            }
        }
        return closest
    }
}
