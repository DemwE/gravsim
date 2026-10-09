package ui

import com.raylib.Raylib.*
import spaceObjects.GravityObject

class SimulationHud(
    val topStatusBar: TopStatusBar = TopStatusBar(),
    val systemStatsPanel: SystemStatsPanel = SystemStatsPanel(),
    val objectInspectorPanel: ObjectInspectorPanel = ObjectInspectorPanel(),
    val controlsHelpBar: ControlsHelpBar = ControlsHelpBar(),
    val worldOverlay: WorldOverlay = WorldOverlay()
) {
    var showOverlay: Boolean = false
    var selectedObject: GravityObject? = null
    var scale: Float = 1.3f

    fun scaleUp() {
        scale = (scale + 0.1f).coerceAtMost(2.5f)
    }

    fun scaleDown() {
        scale = (scale - 0.1f).coerceAtLeast(0.6f)
    }

    fun scaleReset() {
        scale = 1.0f
    }

    fun selectObjectAt(mousePos: Vector2, camera: Camera2D, objects: List<GravityObject>): GravityObject? {
        return ObjectPicker.pickObject(mousePos, camera, objects)
    }

    fun drawWorldOverlay(camera: Camera2D, objects: List<GravityObject>, followedObject: GravityObject?) {
        val screenWidth = GetRenderWidth()
        val logicalWidth = GetScreenWidth()
        val dpiScale = if (logicalWidth > 0) (screenWidth.toFloat() / logicalWidth.toFloat()).coerceAtLeast(1.0f) else 1.0f
        worldOverlay.draw(camera, objects, selectedObject, followedObject, showOverlay, scale * dpiScale)
    }

    fun drawScreenHud(
        timeScale: Double,
        simulatedTime: Double,
        paused: Boolean,
        objects: List<GravityObject>,
        followedObject: GravityObject?,
        systemName: String = "System",
    ) {
        val screenWidth = GetRenderWidth()
        val screenHeight = GetRenderHeight()
        val logicalWidth = GetScreenWidth()
        val dpiScale = if (logicalWidth > 0) (screenWidth.toFloat() / logicalWidth.toFloat()).coerceAtLeast(1.0f) else 1.0f
        val effectiveScale = scale * dpiScale

        topStatusBar.draw(timeScale, simulatedTime, paused, effectiveScale)
        systemStatsPanel.draw(screenWidth, objects, showOverlay, systemName, effectiveScale)
        objectInspectorPanel.draw(screenHeight, selectedObject, followedObject, effectiveScale)
        controlsHelpBar.draw(screenWidth, screenHeight, effectiveScale)
    }
}
