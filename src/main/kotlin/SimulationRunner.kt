import com.raylib.Colors.BLACK
import com.raylib.Raylib.*
import lib.Seed
import presets.SystemPreset
import ui.GuiFont
import ui.SimulationHud
import ui.WorldBoundariesRenderer
import ui.ScreenWorldRenderer
import window.CameraController
import window.InputHandler
import window.initializeWindow

private const val TARGET_FPS = 75
private const val MAX_FRAME_TIME = 0.05f

object SimulationRunner {
    fun run(preset: SystemPreset, seedValue: Long, planetCount: Int?) {
        SetTargetFPS(TARGET_FPS)
        initializeWindow()

        val camera = Camera2D()
            .offset(Vector2().x(GetRenderWidth() / 2f).y(GetRenderHeight() / 2f))
            .target(Vector2())
            .zoom(0.00001f)
        val cameraController = CameraController()
        val hud = SimulationHud()
        val inputHandler = InputHandler(cameraController, hud, planetCount)

        var system = preset.create(Seed(seedValue), planetCount)
        var timeScale = system.suggestedTimeScale
        var paused = false
        var simulatedTime = 0.0

        while (!WindowShouldClose()) {
            cameraController.update(camera)
            inputHandler.handleInput(
                camera = camera,
                systemProvider = { system },
                systemSetter = { system = it },
                timeScaleProvider = { timeScale },
                timeScaleSetter = { timeScale = it },
                pausedProvider = { paused },
                pausedSetter = { paused = it },
                simulatedTimeSetter = { simulatedTime = it },
            )

            if (!paused) {
                val dtSim = GetFrameTime().coerceAtMost(MAX_FRAME_TIME).toDouble() * timeScale
                system.update(dtSim)
                simulatedTime += dtSim
            }

            clearInvalidSelection(system, hud, cameraController)
            drawFrame(camera, system, cameraController, hud, timeScale, simulatedTime, paused)
        }

        GuiFont.unload()
        system.close()
        CloseWindow()
    }

    private fun clearInvalidSelection(
        system: SolarSystem,
        hud: SimulationHud,
        cameraController: CameraController,
    ) {
        if (hud.selectedObject != null && hud.selectedObject !in system.objects) {
            hud.selectedObject = null
            cameraController.follow(null)
        }
        if (cameraController.followedObject != null && cameraController.followedObject !in system.objects) {
            cameraController.follow(null)
        }
    }

    private fun drawFrame(
        camera: Camera2D,
        system: SolarSystem,
        cameraController: CameraController,
        hud: SimulationHud,
        timeScale: Double,
        simulatedTime: Double,
        paused: Boolean,
    ) {
        BeginDrawing()
        ClearBackground(BLACK)

        BeginMode2D(camera)
        WorldBoundariesRenderer.draw()
        system.objects.forEach { obj ->
            if (cameraController.isVisible(obj, camera)) obj.draw()
        }
        EndMode2D()

        ScreenWorldRenderer.draw(camera, system.objects)

        hud.drawWorldOverlay(camera, system.objects, cameraController.followedObject)
        hud.drawScreenHud(
            timeScale,
            simulatedTime,
            paused,
            system.objects,
            cameraController.followedObject,
            system.name,
        )
        EndDrawing()
    }
}
