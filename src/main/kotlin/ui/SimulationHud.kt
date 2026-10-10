package ui

import com.raylib.Raylib.*
import config.HudConfig
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
    var scale: Float = HudConfig.INITIAL_SCALE

    fun scaleUp() {
        scale = (scale + HudConfig.SCALE_STEP).coerceAtMost(HudConfig.MAX_SCALE)
    }

    fun scaleDown() {
        scale = (scale - HudConfig.SCALE_STEP).coerceAtLeast(HudConfig.MIN_SCALE)
    }

    fun scaleReset() {
        scale = HudConfig.RESET_SCALE
    }

    fun selectObjectAt(mousePos: Vector2, camera: Camera2D, objects: List<GravityObject>): GravityObject? {
        return ObjectPicker.pickObject(mousePos, camera, objects)
    }

    fun drawWorldOverlay(camera: Camera2D, objects: List<GravityObject>, followedObject: GravityObject?) {
        worldOverlay.draw(camera, objects, selectedObject, followedObject, showOverlay, effectiveScale())
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
        val effectiveScale = effectiveScale()

        topStatusBar.draw(timeScale, simulatedTime, paused, effectiveScale)
        systemStatsPanel.draw(screenWidth, objects, showOverlay, systemName, effectiveScale)
        objectInspectorPanel.draw(screenHeight, selectedObject, followedObject, effectiveScale)
        controlsHelpBar.draw(screenWidth, screenHeight, effectiveScale)
    }

    fun timeWarpStepAt(mousePos: Vector2): Int? {
        return topStatusBar.timeWarpStepAt(mousePos, effectiveScale())
    }

    fun pauseButtonContains(mousePos: Vector2): Boolean =
        topStatusBar.pauseButtonContains(mousePos, effectiveScale())

    private fun effectiveScale(): Float {
        return HudStyle.effectiveScale(scale)
    }
}
