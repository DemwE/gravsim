package window

import com.raylib.Raylib.*
import engine.MAP_HALF_KM
import spaceObjects.GravityObject
import kotlin.math.abs

class CameraController {
    private var isDragging = false
    var followedObject: GravityObject? = null

    private val screenOffset = Vector2()
    private val targetVec = Vector2()

    fun update(camera: Camera2D) {
        screenOffset.x(GetRenderWidth() / 2f).y(GetRenderHeight() / 2f)
        camera.offset(screenOffset)

        val wheelMove = GetMouseWheelMove()
        if (wheelMove != 0f) {
            val mousePosition = GetMousePosition()
            val worldPositionBeforeZoom = GetScreenToWorld2D(mousePosition, camera)
            val newZoom = (camera.zoom() * (1f + wheelMove * 0.1f)).coerceIn(1.0e-8f, 1000f)
            camera.zoom(newZoom)
            val worldPositionAfterZoom = GetScreenToWorld2D(mousePosition, camera)
            camera.target(
                Vector2Subtract(
                    camera.target(),
                    Vector2Subtract(worldPositionBeforeZoom, worldPositionAfterZoom)
                )
            )
        }

        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            isDragging = true
        }
        if (IsMouseButtonReleased(MOUSE_BUTTON_LEFT)) {
            isDragging = false
        }
        if (isDragging) {
            val mouseDelta = GetMouseDelta()
            if (mouseDelta.x() != 0f || mouseDelta.y() != 0f) {
                camera.target(
                    Vector2Subtract(
                        camera.target(),
                        Vector2Scale(mouseDelta, 1f / camera.zoom())
                    )
                )
                if (mouseDelta.x() * mouseDelta.x() + mouseDelta.y() * mouseDelta.y() > 9f) {
                    followedObject = null
                }
            }
        } else {
            followedObject?.let { targetObj ->
                targetVec.x(targetObj.position.x.toFloat()).y(targetObj.position.y.toFloat())
                camera.target(targetVec)
            }
        }

        // Clamp camera target to map boundaries
        val halfKm = MAP_HALF_KM.toFloat()
        val clampedX = camera.target().x().coerceIn(-halfKm, halfKm)
        val clampedY = camera.target().y().coerceIn(-halfKm, halfKm)
        camera.target().x(clampedX).y(clampedY)
    }

    fun isVisible(obj: GravityObject, camera: Camera2D): Boolean {
        val zoom = camera.zoom()
        if (zoom <= 0f) return true

        val halfW = (GetRenderWidth() / (2f * zoom)).toDouble()
        val halfH = (GetRenderHeight() / (2f * zoom)).toDouble()
        val camX = camera.target().x().toDouble()
        val camY = camera.target().y().toDouble()

        // Give a generous margin so large atmospheres/glow and selection rings don't pop out
        val visualMarginKm = maxOf(obj.radius.value * 3.0, (100.0 / zoom))
        val dx = abs(obj.position.x - camX)
        val dy = abs(obj.position.y - camY)

        return (dx <= halfW + visualMarginKm && dy <= halfH + visualMarginKm)
    }

    fun reset(camera: Camera2D) {
        followedObject = null
        camera.target(targetVec.x(0f).y(0f))
        camera.zoom(0.00001f)
    }

    fun follow(obj: GravityObject?) {
        followedObject = obj
    }
}
