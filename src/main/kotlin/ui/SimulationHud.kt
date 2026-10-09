package ui

import com.raylib.Raylib.*
import spaceObjects.GravityObject
import kotlin.math.sqrt

class SimulationHud(
    val topStatusBar: TopStatusBar = TopStatusBar(),
    val systemStatsPanel: SystemStatsPanel = SystemStatsPanel(),
    val objectInspectorPanel: ObjectInspectorPanel = ObjectInspectorPanel(),
    val controlsHelpBar: ControlsHelpBar = ControlsHelpBar(),
    val worldOverlay: WorldOverlay = WorldOverlay()
) {
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
        worldOverlay.draw(camera, objects, selectedObject, followedObject, showOverlay)
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

        topStatusBar.draw(timeScale, simulatedTime, paused)
        systemStatsPanel.draw(screenWidth, objects, showOverlay)
        objectInspectorPanel.draw(screenHeight, selectedObject, followedObject)
        controlsHelpBar.draw(screenWidth, screenHeight)
    }
}
