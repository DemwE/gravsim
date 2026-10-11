package window

import SolarSystem
import com.raylib.Raylib.*
import lib.Seed
import presets.PresetFactory
import ui.*
import java.time.Instant

class InputHandler(
    private val cameraController: CameraController,
    private val hud: SimulationHud,
    private val presetFactory: PresetFactory,
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
            if (IsKeyPressed(KEY_EQUAL) || IsKeyPressed(KEY_KP_ADD)) {
                timeScaleSetter(TimeWarpSteps.step(currentScale, 1))
            }
            if (IsKeyPressed(KEY_MINUS) || IsKeyPressed(KEY_KP_SUBTRACT)) {
                timeScaleSetter(TimeWarpSteps.step(currentScale, -1))
            }
            if (IsKeyPressed(KEY_ZERO) || IsKeyPressed(KEY_KP_0) || IsKeyPressed(KEY_O)) {
                timeScaleSetter(TimeWarpSteps.scaleAt(0))
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
            if (hud.pauseButtonContains(mousePos)) {
                pausedSetter(!pausedProvider())
                return
            }
            val stepIndex = hud.timeWarpStepAt(mousePos)
            if (stepIndex != null) {
                timeScaleSetter(TimeWarpSteps.scaleAt(stepIndex))
                return
            }
            val clickedObj = hud.selectObjectAt(mousePos, camera, systemProvider().objects)
            if (clickedObj != null) {
                hud.selectedObject = clickedObj
            }
        }

        if (IsKeyPressed(KEY_R)) {
            val old = systemProvider()
            old.close()
            val fresh = presetFactory.create(Seed(Instant.now().toEpochMilli()), defaultPlanets)
            systemSetter(fresh)
            timeScaleSetter(TimeWarpSteps.scaleAt(0))
            simulatedTimeSetter(0.0)
            hud.selectedObject = null
            cameraController.reset(camera)
        }
    }
}
