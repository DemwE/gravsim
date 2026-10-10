package ui

import com.raylib.Raylib.*
import config.CameraConfig
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

        val dpiScale = HudStyle.dpiScale()

        val hitPaddingKm = (CameraConfig.OBJECT_PICK_PADDING_PIXELS * dpiScale) / zoom

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
