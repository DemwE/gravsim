package window

import SolarSystem
import com.raylib.Raylib.*
import lib.Seed
import ui.SimulationHud
import java.time.Instant

/**
 * Handles keyboard shortcuts and mouse interaction for the simulation.
 */
class InputHandler(
    private val cameraController: CameraController,
    private val hud: SimulationHud,
    private val defaultPlanets: Int?,
) {
    fun handleInput(
        camera: Camera2D,
        systemProvider: () -> SolarSystem,
        systemSetter: (SolarSystem) -> Unit,
        timeScaleProvider: () -> Double,
        timeScaleSetter: (Double) -> Unit,
        pausedProvider: () -> Boolean,
        pausedSetter: (Boolean) -> Unit,
        simulatedTimeSetter: (Double) -> Unit,
    ) {
        val ctrlDown = IsKeyDown(KEY_LEFT_CONTROL) || IsKeyDown(KEY_RIGHT_CONTROL)

        if (ctrlDown) {
            if (IsKeyPressed(KEY_EQUAL) || IsKeyPressed(KEY_KP_ADD)) hud.scaleUp()
            if (IsKeyPressed(KEY_MINUS) || IsKeyPressed(KEY_KP_SUBTRACT)) hud.scaleDown()
            if (IsKeyPressed(KEY_ZERO) || IsKeyPressed(KEY_KP_0) || IsKeyPressed(KEY_O)) hud.scaleReset()
        } else {
            val currentScale = timeScaleProvider()
            if (IsKeyPressed(KEY_EQUAL) || IsKeyPressed(KEY_KP_ADD)) timeScaleSetter(currentScale * 2.0)
            if (IsKeyPressed(KEY_MINUS) || IsKeyPressed(KEY_KP_SUBTRACT)) timeScaleSetter(currentScale * 0.5)
            if (IsKeyPressed(KEY_ZERO) || IsKeyPressed(KEY_KP_0) || IsKeyPressed(KEY_O)) {
                timeScaleSetter(systemProvider().suggestedTimeScale)
            }
        }

        if (IsKeyPressed(KEY_SPACE)) pausedSetter(!pausedProvider())

        if (IsKeyPressed(KEY_C)) cameraController.reset(camera)

        if (IsKeyPressed(KEY_TAB)) hud.showOverlay = !hud.showOverlay

        if (IsKeyPressed(KEY_F)) {
            val selected = hud.selectedObject
            if (selected != null) {
                if (cameraController.followedObject == selected) {
                    cameraController.follow(null)
                } else {
                    cameraController.follow(selected)
                }
            }
        }

        if (IsKeyPressed(KEY_ESCAPE)) {
            hud.selectedObject = null
            cameraController.follow(null)
        }

        if (IsMouseButtonPressed(MOUSE_BUTTON_LEFT)) {
            val mousePos = GetMousePosition()
            val clickedObj = hud.selectObjectAt(mousePos, camera, systemProvider().objects)
            if (clickedObj != null) {
                hud.selectedObject = clickedObj
            }
        }

        if (IsKeyPressed(KEY_R)) {
            val old = systemProvider()
            old.close()
            val fresh = SolarSystem(Seed(Instant.now().toEpochMilli()))
            fresh.populate(planets = defaultPlanets)
            systemSetter(fresh)
            timeScaleSetter(fresh.suggestedTimeScale)
            simulatedTimeSetter(0.0)
            hud.selectedObject = null
            cameraController.reset(camera)
        }
    }
}
