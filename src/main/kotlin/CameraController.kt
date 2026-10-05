import com.raylib.Raylib.*

class CameraController {
    private var isDragging = false

    fun update(camera: Camera2D) {
        camera.offset(Vector2().x(GetScreenWidth() / 2f).y(GetScreenHeight() / 2f))

        val wheelMove = GetMouseWheelMove()
        if (wheelMove != 0f) {
            val mousePosition = GetMousePosition()
            val worldPositionBeforeZoom = GetScreenToWorld2D(mousePosition, camera)
            val newZoom = (camera.zoom() * (1f + wheelMove * 0.1f)).coerceIn(0.000001f, 1000f)
            camera.zoom(newZoom)
            val worldPositionAfterZoom = GetScreenToWorld2D(mousePosition, camera)
            camera.target(
                Vector2Subtract(
                    camera.target(),
                    Vector2Subtract(worldPositionBeforeZoom, worldPositionAfterZoom)
                )
            )
        }

        if (IsMouseButtonPressed(MOUSE_BUTTON_MIDDLE)) {
            isDragging = true
        }
        if (IsMouseButtonReleased(MOUSE_BUTTON_MIDDLE)) {
            isDragging = false
        }
        if (isDragging) {
            val mouseDelta = GetMouseDelta()
            camera.target(
                Vector2Subtract(
                    camera.target(),
                    Vector2Scale(mouseDelta, 1f / camera.zoom())
                )
            )
        }
    }
}
