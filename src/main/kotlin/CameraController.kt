import com.raylib.Raylib.*
import spaceObjects.GravityObject

class CameraController {
    private var isDragging = false
    var followedObject: GravityObject? = null

    fun update(camera: Camera2D) {
        camera.offset(Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f))

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
                camera.target(targetObj.position.toVector2())
            }
        }
    }

    fun reset(camera: Camera2D) {
        followedObject = null
        camera.target(Vector2().x(0f).y(0f))
        camera.zoom(0.00001f)
    }

    fun follow(obj: GravityObject?) {
        followedObject = obj
    }
}
